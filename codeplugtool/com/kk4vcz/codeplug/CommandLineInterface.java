package com.kk4vcz.codeplug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;
import com.kk4vcz.codeplug.radios.kenwood.THD74;
import com.kk4vcz.codeplug.radios.kenwood.TMD710G;
import com.kk4vcz.codeplug.radios.yaesu.FT991A;

/*
 * This is a rough CLI wrapper for the tool, intended to be used from Unix
 * or in shell scripting.  Run without parameters for usage info.
 * 
 * I'll rewrite this properly once some of the moving pieces are in order.
 */

public class CommandLineInterface {
	public static void usage() {
		System.out.print("Usage: \n" + "cpt [driver] [port/file] [verbs]\n\n" + "Drivers:\n" + "\tKenwood\n"
				+ "\t\td74 -- TH-D74 Tri-Band HT\n" + "\tYaesu\n" + "\t\t991a -- Yaesu FT-991A\n" + "\tOthers\n" + "\t\tcsv -- Chirp's CSV format.\n"
				+ "Ports:\n");
		
		SerialPort[] ports=SerialPort.getCommPorts();
		for(int i=0; i<ports.length; i++) {
			System.out.format("\t%s\t-- %s\n", ports[i].getSystemPortName(), ports[i].getDescriptivePortName());
		}
		
		System.out.print(
				"Verbs:\n"+
				"\tdump           -- Dumps the radio's channels to the console.\n"+
				"\tinfo           -- Prints the radio's info.\n"+
				"\tupload foo.csv -- Uploads a CSV file from CHIRP to the radio.\n"+
				"\traw \'ME 000\' -- Runs a raw command and prints the result.\n"
				);
		
	}
	
	public static void dump(Radio radio) throws IOException {
		for (int i = radio.getChannelMin(); i <= radio.getChannelMax(); i++) {
			Channel c = radio.readChannel(i);
			if (c != null)
				System.out.println(Main.RenderChannel(c));
		}

	}
	
	public static void upload(Radio radio, String filename) throws IOException{
		File f = new File(filename);
		BufferedReader reader = new BufferedReader(new FileReader(f));

		// Toss the first line, which contains the header.
		reader.readLine();
		// Print the body lines.
		while(reader.ready()) {
			Channel c = new CSVChannel(reader.readLine());
			if (c != null) {
				radio.writeChannel(c.getIndex(), c);
			}
		}
		reader.close();

		System.out.println("done");
	}
	
	public static void info(CATRadio radio) throws IOException {
		System.out.println("Model:       " + radio.getID());
		System.out.println("Version:     " + radio.getVersion());
		System.out.println("Serial:      " + radio.getSerialNumber());
		System.out.println("Callsign:    " + radio.getCallsign());
		System.out.println("Frequency A: " + radio.getFrequency());
		System.out.println("Frequency B: " + radio.getFrequencyB());
	}
	
	public static void dumpmem(CATRadio radio) throws IOException {
		/* This doesn't work yet, just messing around to see what gets a reply. */
		for(int i=0; i<1024; i++) {
			radio.peek32(i);
		}
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			usage();
			System.exit(1);
		}

		String driver = args[0];

		try {
			SerialPort port;
			CATRadio radio=null;
			
			if (driver.equals("d74")) {
				port = SerialPort.getCommPort(args[1]);
				if(!port.openPort()) {
					System.out.println("Failed to open "+args[1]);
					System.exit(1);
				}
				
				port.setBaudRate(9600);
				// 1ms response time.
				port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1, 0);
				radio = new THD74(port.getInputStream(), port.getOutputStream());
			} else if (driver.equals("d710")) {
				port = SerialPort.getCommPort(args[1]);
				if(!port.openPort()) {
					System.out.println("Failed to open "+args[1]);
					System.exit(1);
				}
				
				port.setBaudRate(57600);
				// 1ms response time.
				port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1, 0);
				radio = new TMD710G(port.getInputStream(), port.getOutputStream());
			} else if (driver.equals("991a")) {
				port = SerialPort.getCommPort(args[1]);
				if(!port.openPort()) {
					System.out.println("Failed to open "+args[1]);
					System.exit(1);
				}
				
				port.setBaudRate(38400);
				// 1ms response time.
				port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1, 0); //TODO fix this timeout by sampling individual bytes.
				radio = new FT991A(port.getInputStream(), port.getOutputStream());
			} else {
				System.out.println("Unknown driver "+args[0]);
				System.exit(1);
			}

			for (int i = 2; i < args.length; i++) {
				if(args[i].equals("dump")) {
					dump(radio);
				}else if(args[i].equals("dumpmem")) {
					dumpmem(radio);
				}else if(args[i].equals("info")) {
					info(radio);
				}else if(args[i].equals("upload")) {
					upload(radio, args[++i]);
				}else if(args[i].equals("raw")) {
					System.out.println(radio.rawCommand(args[++i]));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
