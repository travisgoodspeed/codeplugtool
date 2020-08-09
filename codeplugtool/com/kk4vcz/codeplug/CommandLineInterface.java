package com.kk4vcz.codeplug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;
import com.kk4vcz.codeplug.api.RepeaterBook;
import com.kk4vcz.codeplug.connections.JSerialCommConnection;
import com.kk4vcz.codeplug.connections.TCPConnection;
import com.kk4vcz.codeplug.radios.kenwood.THD72;
import com.kk4vcz.codeplug.radios.kenwood.THD74;
import com.kk4vcz.codeplug.radios.kenwood.TMD710G;
import com.kk4vcz.codeplug.radios.other.CSVChannel;
import com.kk4vcz.codeplug.radios.other.ChirpCSV;
import com.kk4vcz.codeplug.radios.yaesu.FT991A;

/*
 * This is a rough CLI wrapper for the tool, intended to be used from Unix
 * or in shell scripting.  Run without parameters for usage info.
 * 
 * I'll rewrite this properly once some of the moving pieces are in order.
 */

public class CommandLineInterface {
	public static void usage() {
		System.out.print(
				"Usage: \n" +
				"cpt [driver] [device/hostname:port] [verbs]\n\n" + 
				"Drivers:\n"+
					"\tKenwood\n"+
						"\t\td72  -- TH-D72 Dual-Band HT\n"+
						"\t\td74  -- TH-D74 Tri-Band HT\n"+
						"\t\td710 -- TM-D710 Mobile\n"+
					"\tYaesu\n"+
						"\t\t991a -- Yaesu FT-991A\n"+
					"\tOthers\n"+
						"\t\tcsv  -- Chirp's CSV format.\n"
				+ "Ports:\n");
		
		SerialPort[] ports=SerialPort.getCommPorts();
		for(int i=0; i<ports.length; i++) {
			System.out.format("\t%s\t-- %s\n", ports[i].getSystemPortName(), ports[i].getDescriptivePortName());
		}
		
		System.out.print(
				"Verbs:\n"+
				"\tinfo                  -- Prints the radio's info.\n"+
				"\tdump                  -- Dumps the radio's channels to the console.\n"+
				"\tupload foo.csv        -- Uploads a CSV file from CHIRP to the radio.\n"+
				"\tdownload foo.csv      -- Downloads a CSV file from the radio.\n"+
				"\traw \'ME 000\'          -- Runs a raw command and prints the result.\n"+
				"\trr 100 \'Knoxville\'    -- Query RadioReference starting at channel 100.\n"
				);
		System.out.print(
				"Examples:\n"+
				"\tjava -jar CodePlugTool.jar d710 ttyS1 info\n"+
				"\tjava -jar CodePlugTool.jar d74 localhost:54321 info\n"
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
	
	public static void download(Radio radio, String filename) throws IOException{
		System.out.println(String.format("Downloading channels from radio to %s.  This might take a while.", filename));
		ChirpCSV r=new ChirpCSV(radio);
		r.exportToFile(new File(filename));
	}
	
	public static void erase(Radio radio) throws IOException{
		for(int i=radio.getChannelMin(); i<radio.getChannelMax(); i++) {
			System.out.format("Erasing channel %d.\n", i);
			radio.deleteChannel(i);
		}
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
	
	static RadioConnection getConnection(String path) throws IOException{
		if(path.indexOf(':')>0) {
			return TCPConnection.getConnection(path);
		}
		return JSerialCommConnection.getConnection(path);
	}
	
	public static void main(String[] args) {
		if (args.length < 3) {
			usage();
			System.exit(1);
		}

		String driver = args[0];

		try {
			RadioConnection conn=null;
			CATRadio radio=null;
			
			if (driver.equals("d74")) {
				conn = getConnection(args[1]);
				conn.setBaudRate(9600);
				radio = new THD74(conn.getInputStream(), conn.getOutputStream());
			} else if (driver.equals("d72")) {
				conn = getConnection(args[1]);
				conn.setBaudRate(9600);
				radio = new THD72(conn.getInputStream(), conn.getOutputStream());
			} else if (driver.equals("d710")) {
				conn = getConnection(args[1]);
				conn.setBaudRate(57600);
				radio = new TMD710G(conn.getInputStream(), conn.getOutputStream());
			} else if (driver.equals("991a")) {
				conn = getConnection(args[1]);
				conn.setBaudRate(38400);
				radio = new FT991A(conn.getInputStream(), conn.getOutputStream());
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
				}else if(args[i].equals("download")) {
					download(radio, args[++i]);
				}else if(args[i].equals("erase")) {
					erase(radio);
				}else if(args[i].equals("raw")) {
					System.out.println(radio.rawCommand(args[++i]));
				}else if(args[i].equals("rr")) {
					int target=Integer.parseInt(args[++i]);
					String loc=args[++i];
					Radio res=new RepeaterBook().queryProximity(loc, 25,  0);
					System.out.format("Queried RadioReference for '%s' to channel %d and further.\n", loc, target);
					Main.CopyChannels(radio,  target,  res);
				}
			}
			
			if(conn!=null)
				conn.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
