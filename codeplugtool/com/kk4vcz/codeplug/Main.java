package com.kk4vcz.codeplug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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

	// Utility function to apply the values of the second parameter to the first.
	public static void ApplyChannel(Channel dst, Channel src) {
		dst.setRXFrequency(src.getRXFrequency());
		dst.setOffset(src.getSplitDir(), src.getOffset());
		dst.setIndex(src.getIndex());
		dst.setName(src.getName());
	}

	// Utility function to print a channel.
	public static String RenderChannel(Channel c) {
		String freq;

		if (c == null)
			return "EMPTY";

		String splitdir = c.getSplitDir();
		if (splitdir.equals("split")) {
			freq = String.format("%f MHz (TX %f MHz)", c.getRXFrequency() / 1000000.0,
					c.getTXFrequency() / 1000000.0);
		} else if (splitdir.equals("+") || splitdir.equals("-")) {
			freq = String.format("%f MHz (TX %s%f MHz)", c.getRXFrequency() / 1000000.0,
					splitdir, c.getOffset() / 1000000.0);
		} else { // Simplex
			freq = String.format("%f MHz", c.getRXFrequency() / 1000000.0,
					c.getTXFrequency() / 1000000.0);
		}
		
		/*
		System.out.format("# Channel %03d\n", c.getIndex());
		System.out.format("# %f MHz %s (TX %f MHz)\n", c.getRXFrequency() / 1000000.0, c.getMode(),
				c.getTXFrequency() / 1000000.0);
		if (c.getToneSent())
			System.out.format("# TX Tone %f\n", c.getTXToneFreq() / 10.0);
		if (c.getToneRequired())
			System.out.format("# RX Tone %f\n", c.getRXToneFreq() / 10.0);
			*/
		return String.format("%03d %s", c.getIndex(), freq);
	}

	public static void testTHD74() {
		try {
			SerialPort port;
			CATRadio radio;

			port = SerialPort.getCommPort("/dev/ttyACM0");
			port.setBaudRate(9600);
			if (port.openPort()) {
				port.setBaudRate(9600);
				System.out.println("Opened port " + port.getSystemPortName());

				// 1ms response time.
				port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1, 0);

				radio = new THD74(port.getInputStream(), port.getOutputStream());

				System.out.println("Model:       " + radio.getID());
				System.out.println("Version:     " + radio.getVersion());
				System.out.println("Serial:      " + radio.getSerialNumber());
				System.out.println("Callsign:    " + radio.getCallsign());
				System.out.println("Frequency:   " + radio.getFrequency());

				for (int i = 0; i < 11; i++) {
					Channel c = radio.readChannel(i);
					if (c != null)
						System.out.println(RenderChannel(c));
				}

				System.out.println("done");
			} else {
				System.out.println("Failed to open " + port.getSystemPortName());
				System.exit(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void testCSV() {
		try {
			// FileInputStream in = new FileInputStream("knoxville.csv");
			File f = new File("knoxville.csv");
			BufferedReader reader = new BufferedReader(new FileReader(f));

			// Toss the first line.
			reader.readLine();

			for (int i = 0; i < 11; i++) {
				Channel c = new CSVChannel(reader.readLine());
				if (c != null)
					System.out.println(RenderChannel(c));
			}
			reader.close();

			System.out.println("done");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		System.out.println("Codeplug Tool by KK4VCZ and Friends");

		testTHD74();
		testCSV();

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
		System.out.println("\tOther:");
		System.out.println("\t\tCSV");

	}
}
