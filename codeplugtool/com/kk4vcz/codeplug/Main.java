package com.kk4vcz.codeplug;

import java.io.IOException;

//We use this library in command-line Linux, but try not to have radio classes depend upon it.
import com.fazecast.jSerialComm.*;
import com.kk4vcz.codeplug.radios.kenwood.THD74;

/* Hey y'all,
 * 
 * This is a main class that can be run from the command line as a
 * stand alone .jar file.  It is the only class that is allowed to use
 * the jSerialComm driver.
 * 
 * --KK4VCZ
 */

public class Main {
	public static SerialPort port;
	public static CATRadio radio;

	public static void main(String[] args) {
		System.out.println("Codeplug Tool by KK4VCZ and Friends");

		try {
			port = SerialPort.getCommPort("/dev/ttyACM0");
			port.setBaudRate(9600);
			if (port.openPort()) {
				port.setBaudRate(9600);				
				System.out.println("Opened port " + port.getSystemPortName());
				
				//1ms response time.
				port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1, 0);
				
				radio = new THD74(port.getInputStream(), port.getOutputStream());

				System.out.println("Model:       " + radio.getID());
				System.out.println("Version:     " + radio.getVersion());
				System.out.println("Serial:      " + radio.getSerialNumber());
				System.out.println("Callsign:    " + radio.getCallsign());
				System.out.println("Frequency:   " + radio.getFrequency());
				
				for(int i=0; i<11; i++) {
					Channel c=radio.readChannel(i);
				}

				System.out.println("done");
				System.exit(0);
			} else {
				System.out.println("Failed to open " + port.getSystemPortName());
				System.exit(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// usage();
	}

	public static void usage() {
		System.out.println("Available ports:");
		SerialPort[] ports = SerialPort.getCommPorts();
		for (int i = 0; i < ports.length; i++) {
			System.out.println("\t" + ports[i].getSystemPortName() + "\t" + ports[i].getDescriptivePortName());
		}

		System.out.println("Supported Radios:");
		System.out.println("\tKenwood:");
		System.out.println("\t\tTH-D74");
	}
}
