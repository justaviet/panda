/*
	STM32 MCU Flashing Utility GUI
	
	while(TRUE)
	dfu-utils -l
	if(STM32 BOOTLOADER = 1)
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;


public class Flash {
	public static boolean dfuDevice = false;

	public printOutput getStreamWrapper(InputStream is, String type) {
		return new printOutput(is, type);
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("STM32 Firmware Utility");
		System.out.println("Searching for DFU Device...");
		Runtime rt = Runtime.getRuntime();
		Flash f = new Flash();
		printOutput errorReported, outputMessage;

		while(dfuDevice == false) {
				System.out.println("Searching...");
				try {
					Process proc = Runtime.getRuntime().exec("dfu-util -l");
					errorReported = f.getStreamWrapper(proc.getErrorStream(), "ERROR");
					outputMessage = f.getStreamWrapper(proc.getInputStream(), "OUTPUT");
					errorReported.start();
					outputMessage.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}

		if (dfuDevice == true) {
			System.out.println("Device Found");
			try {
				System.out.println("Beginning firmware update....")
				Process proc = Runtime.getRuntime().exec("make recover");
				errorReported = f.getStreamWrapper(proc.getErrorStream(), "ERROR");
				outputMessage = f.getStreamWrapper(proc.getInputStream(), "OUTPUT");
				errorReported.start();
				outputMessage.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Flashing complete!")
	}

	private class printOutput extends Thread {
		InputStream is = null;
		printOutput(InputStream is, String type) {
			this.is = is;
		}
		public void run() {
			String s = null;
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				while ((s = br.readLine()) != null) {
					if (s.contains("Found DFU")) {
						dfuDevice = true;
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}