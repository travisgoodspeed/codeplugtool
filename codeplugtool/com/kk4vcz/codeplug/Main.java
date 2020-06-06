package com.kk4vcz.codeplug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//We use this library in command-line Linux, but try not to have radio classes depend upon it.
import com.fazecast.jSerialComm.*;
import com.kk4vcz.codeplug.radios.kenwood.THD74;

/* Hey y'all,
 * 
 * This class contains the main() method for self-testing, as well as some janky static functions
 * that should probably be moved elsewhere.  The main() method of the .jar file is in
 * CommandLineInterface.java, not here.
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
		dst.setMode(src.getMode());
		dst.setToneMode(src.getToneMode());
		dst.setToneFreq(src.getToneFreq());
		dst.setToneMode(src.getToneMode());
		dst.setDTCSCode(src.getDTCSCode());
	}

	// Utility function to print a channel.
	public static String RenderChannel(Channel c) {
		String freq;
		String tone="";

		if (c == null)
			return "EMPTY";

		String splitdir = c.getSplitDir();
		if (splitdir.equals("split")) {
			freq = String.format("%f MHz (TX %05.1f MHz)", c.getRXFrequency() / 1000000.0,
					c.getTXFrequency() / 1000000.0);
		} else if (splitdir.equals("+") || splitdir.equals("-")) {
			freq = String.format("%f MHz (TX  %s%02.1f MHz)", c.getRXFrequency() / 1000000.0,
					splitdir, c.getOffset() / 1000000.0);
		} else { // Simplex
			freq = String.format("%f MHz", c.getRXFrequency() / 1000000.0,
					c.getTXFrequency() / 1000000.0);
		}
		
		if(c.getToneMode().equals("tone")) {
			tone=String.format(" T%05.1f", c.getToneFreq()/10.0);
		}else if(c.getToneMode().equals("ct")) {
			tone=String.format("CT%05.1f", c.getToneFreq()/10.0);
		}else if(c.getToneMode().equals("dcs")) {
			tone=String.format("DTCS%04d", c.getDTCSCode());
		}else if(c.getToneMode().equals("")) {
			tone="";
		}else {
			tone=c.getToneMode();
		}
		
		if(c.getMode().equals("DV") || c.getMode().equals("DR"))
			tone=String.format("%7s", c.getURCALL());
			
			
		return String.format("%03d %5s %14s %s %s", c.getIndex(), c.getMode(), freq, tone, c.getName());
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
			File f = new File("knoxville.csv");
			BufferedReader reader = new BufferedReader(new FileReader(f));

			// Toss the first line, which contains the header.
			reader.readLine();
			// Print the body lines.
			while(reader.ready()) {
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

		testCSV();
		testTHD74();
	}
}
