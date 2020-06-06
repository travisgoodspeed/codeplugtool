package com.kk4vcz.codeplug.radios.yaesu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.kk4vcz.codeplug.CATRadio;
import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.codeplug.CommandLineInterface;
import com.kk4vcz.codeplug.radios.kenwood.THD74Channel;

/*
 * The Yaesu FT-991A's protocol is publicly documented in the "CAT Operations
 * Reference Manual."  Much like the Kenwood radios, memories are available live
 * and indexed by a number between 1 and 117.
 * 
 * One complication is that the radio is designed for HF, so the split mode
 * operations familiar to us VHF operators are rather awkward.  This modules
 * is not nearly complete.
 */

public class FT991A implements CATRadio {
	BufferedReader reader;
	PrintWriter writer;
	
	public FT991A(InputStream is, OutputStream os) throws IOException {
		reader=new BufferedReader(new InputStreamReader(is));
		writer=new PrintWriter(os);
	}
	

	@Override
	public String rawCommand(String cmd) throws IOException {
		writer.print(cmd+";");
		writer.flush();
		
		char[] line=new char[1024];
		reader.read(line);
		
		return new String(line).strip();
	}

	@Override
	public void writeChannel(int index, Channel ch) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Channel readChannel(int index) throws IOException {
		/* MR and MW also allow the memories to be read, but MT includes
		 * the name so we use it here.
		 */
		String row=rawCommand(String.format("MT%03d", index));
		
		//Unused channel number.
		if(row.charAt(0)!='M' || row.charAt(1)!='T')
			return null;
		
		return new FT991AChannel(index, row);
	}

	@Override
	public String getVersion() throws IOException {
		return "unknown";
	}

	@Override
	public String getSerialNumber() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getID() throws IOException {
		String id=rawCommand("ID").substring(2,6);
		if(id.equals("0670"))
			return "Yaesu FT-991A";
		
		return String.format("Unknown ID %s", id);
	}

	@Override
	public void setFrequency(long frequency) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public long getFrequency() throws IOException {
		String s=rawCommand("FA");
		return Integer.valueOf(s.substring(2,11)).longValue();
	}

	@Override
	public void setFrequencyB(long frequency) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public long getFrequencyB() throws IOException {
		String s=rawCommand("FB");
		return Integer.valueOf(s.substring(2,11)).longValue();
	}

	@Override
	public String getCallsign() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCallsign(String callsign) throws IOException {
		// TODO Auto-generated method stub

	}

	// Test routine.
	public static void main(String[] args) {
		String[] newargs = { "991a",
				"/dev/serial/by-id/usb-Silicon_Labs_CP2105_Dual_USB_to_UART_Bridge_Controller_00C455ED-if00-port0",
				//"info"
				"dump"
		
		};
		CommandLineInterface.main(newargs);
	}


	@Override
	public int getChannelMin() throws IOException {
		return 1;
	}


	@Override
	public int getChannelMax() throws IOException {
		return 127;
	}

}
