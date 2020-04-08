import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.io.File;  // Import the File class
import java.io.IOException;
import java.io.FileWriter;   // Import the FileWriter class


/* detect command */
// dfu-util -l data readout
// - execute terminal command
// - write output to file
// - read file output into interface
// - print file output in ui

/* flash command */
// make recover in panda/board/pedal/directory
// file chooser

/* verify command
dump rom
compare with original
*/

/* log data 
dfu-util 0.9

Copyright 2005-2009 Weston Schmidt, Harald Welte and OpenMoko Inc.
Copyright 2010-2016 Tormod Volden and Stefan Schmidt
This program is Free Software and has ABSOLUTELY NO WARRANTY
Please report bugs to http://sourceforge.net/p/dfu-util/tickets/

Found DFU: [0483:df11] ver=2200, devnum=62, cfg=1, intf=0, path="20-6", alt=3, name="@Device Feature/0xFFFF0000/01*004 e", serial="3296376E3334"
Found DFU: [0483:df11] ver=2200, devnum=62, cfg=1, intf=0, path="20-6", alt=2, name="@OTP Memory /0x1FFF7800/01*512 e,01*016 e", serial="3296376E3334"
Found DFU: [0483:df11] ver=2200, devnum=62, cfg=1, intf=0, path="20-6", alt=1, name="@Option Bytes  /0x1FFFC000/01*016 e", serial="3296376E3334"
Found DFU: [0483:df11] ver=2200, devnum=62, cfg=1, intf=0, path="20-6", alt=0, name="@Internal Flash  /0x08000000/04*016Kg,01*064Kg,07*128Kg", serial="3296376E3334"

../../tests/pedal/enter_canloader.py --recover; sleep 0.5
"dfu-util" -d 0483:df11 -a 0 -s 0x08004000 -D obj/comma.bin
*/

public class Flasher {
   private JFrame mainFrame;
   private JLabel headerLabel;
   private JLabel statusLabel;
   private JPanel controlPanel;
   private String serial;

   public Flasher(){
      prepareGUI();
   }
   public static void main(String[] args){
      logger("log.txt");
      Flasher  Flasher = new Flasher();      
      Flasher.showButtonDemo();
   }
   private void prepareGUI(){
      mainFrame = new JFrame("dfu-util");
      mainFrame.setSize(400,400);
      mainFrame.setLayout(new GridLayout(3, 1));
      
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });    
      headerLabel = new JLabel("", JLabel.CENTER);        
      statusLabel = new JLabel("",JLabel.CENTER);    
      statusLabel.setSize(350,100);

      controlPanel = new JPanel();
      controlPanel.setLayout(new FlowLayout());

      mainFrame.add(headerLabel);
      mainFrame.add(controlPanel);
      mainFrame.add(statusLabel);
      mainFrame.setVisible(true);  
   }
   private static ImageIcon createImageIcon(String path, String description) {
      java.net.URL imgURL = Flasher.class.getResource(path);
      if (imgURL != null) {
         return new ImageIcon(imgURL, description);
      } else {            
         System.err.println("Couldn't find file: " + path);
         return null;
      }
   }
   private void showButtonDemo(){
      //headerLabel.setText("dfu-util"); 

      //resources folder should be inside SWING folder.
      ImageIcon icon = createImageIcon("/resources/java_icon.png","Java");

      JButton infoButton = new JButton("Detect", icon);        
      JButton flashButton = new JButton("Flash", icon);
      JButton dumpButton = new JButton("Dump", icon);
      dumpButton.setHorizontalTextPosition(SwingConstants.LEFT);   

      infoButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            statusLabel.setText("Get Info Button clicked.");
            System.out.println("Get Info Button clicked.");
            getInfo();
         }          
      });
      flashButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            statusLabel.setText("Flash Button clicked.");
            System.out.println("Flash Button clicked");
            flash();
         }
      });
      dumpButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         	System.out.println("Dump Button clicked");
            statusLabel.setText("Dump Button clicked.");
         }
      });
      controlPanel.add(infoButton);
      controlPanel.add(flashButton);
      controlPanel.add(dumpButton);       

      mainFrame.setVisible(true);  
   }
   private void log(String data){
      try {
         FileWriter myWriter = new FileWriter("log.txt", true);
         myWriter.write(data);
         myWriter.close();
         System.out.println("Successfully wrote to the file.");
      } catch (IOException e) {
         System.out.println("An error occurred.");
         e.printStackTrace();
      }
   }
   private void getInfo(){
   	String command = "dfu-util -l";
   	String output = executeCommand(command);
   	System.out.println(output);
      log(output);
   }
   private void flash(){
   	String command = "make recover";
   	String output = executeCommand(command);
   	System.out.println(output);
      log(output);
   }
   public static String executeCommand(String command) {
   	StringBuffer output = new StringBuffer();
   	Process p;
   	try {
   		p = Runtime.getRuntime().exec(command);
   		p.waitFor();
   		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
   		String line = "";			
   		while ((line = reader.readLine())!= null) {
   			output.append(line + "\n");
   		}
   	} catch (Exception e) {
   		e.printStackTrace();
   	}
   	return output.toString();
   }
   public static void logger(String fileName){
      try {
         File f = new File(fileName);
         if (f.createNewFile()) {
            System.out.println(fileName + " file created: " + f.getName());
         } else {
            System.out.println(fileName + " file already exist.");
         }
      } catch (IOException e) {
         System.out.println("Logging error occured.");
         e.printStackTrace();
      }
   }
}
