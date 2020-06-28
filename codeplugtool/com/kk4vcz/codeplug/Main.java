package com.kk4vcz.codeplug;

import com.kk4vcz.codeplug.connections.JSerialCommConnection;

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
	
	//Utility function to get a connection in unix.  Fails in Android.
	public static RadioConnection getConnection(String source) {
		//TODO Handle types other than serial ports.
		return JSerialCommConnection.getConnection(source);
	}

}
