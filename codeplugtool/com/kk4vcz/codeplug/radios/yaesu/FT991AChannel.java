package com.kk4vcz.codeplug.radios.yaesu;

import java.io.IOException;

import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.codeplug.Main;

public class FT991AChannel implements Channel {
	int index;//p1
	long freq;//p2
	int clarval; //p3
	char rxclar; //p4 TODO
	char txclar; //p5 TODO
	char mode; //p6
	char ismem; //p7
	char tonemode; //p8
	String p9; //Mystery, always "00"
	char shiftdir; //p10
	char p11; //Mystery, always 0.
	String name;
	
	// Test routine.
	public static void main(String[] args) {
		try {
			//String sample="MT021052770000+0000004020000            ;";
			String sample="MT021052770000+0000004020000abcdefghijkl;";
			FT991AChannel chan=new FT991AChannel(21, sample);
			System.out.format("%s\n%s\n",sample,chan.render());
			System.out.println(Main.RenderChannel(chan));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public FT991AChannel(int i, String mt) {
		//System.out.format("%s\n", s);
		
		/* From reading the CAT guide, you might think that P1
		 * is the index of the channel, but it's not.  P1 is
		 * the most recently activated memory number, and you only
		 * know the channel's number from the request, and not from the response.
		 */
		//P1
		//index=Integer.valueOf(mt.substring(2,5));  //Looks right, but it's wrong.
		index=i;
		
		//P2
		freq=Integer.valueOf(mt.substring(5,14)).longValue();
		
		//P3
		char clardir=mt.charAt(14);
		String clar=mt.substring(15, 19); //TODO Support clarifiers.
		clarval=Integer.valueOf(clar);
		if(clardir=='-')
			clarval*=-1;
		
		//P4,P5
		rxclar=mt.charAt(19); //RX clarifier off or on?
		txclar=mt.charAt(20); //TX clarifier off or on?
		
		//P6 -- Mode
		mode=mt.charAt(21);
		
		//P7 -- VFO of memory?
		ismem=mt.charAt(22);
		
		//P8 -- Tone encoding.
		tonemode=mt.charAt(23);
		
		//P9 -- Mystery, always 00.
		p9=mt.substring(24,26);
		
		//P10 -- Shift direction.
		shiftdir=mt.charAt(26);
		
		//P11 -- Mystery, always 0.
		p11=mt.charAt(27);
		
		//Name, fixed to twelve characters.
		name=mt.substring(28, 40);
	}
	
	public String render() throws IOException {
		/* This reproduces the original command, so that we can write it back to the radio.
		 */
		
		return String.format(
				"MT%03d%09d%c%04d%c%c%c%c%c%s%c%c%10s",
				
				index,freq,
				clarval>=0?'+':'-', clarval, rxclar, txclar,
						mode,ismem,tonemode,
						p9, shiftdir, p11,
						name
				);
	}
	

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public void setIndex(int i) {
		index=i;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRXFrequency(long freq) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getRXFrequency() {
		return freq;
	}

	@Override
	public long getTXFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSplitDir() {
		switch(shiftdir) {
		case '0': //simplex
			return "";
		case '1':
			return "+";
		case '2':
			return "-";
		}
		//How does split mode work?
		System.out.format("Unknown shift mode %c, assuming simplex.\n",shiftdir);
		return "";
	}

	@Override
	public long getOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOffset(String dir, long freq) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDTCSCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDTCSCode(int code) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getToneFreq() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setToneFreq(int freq) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getToneMode() {
		switch(tonemode) {
		case '0':
			return "";
		case '1':
			return "ct";
		case '2':
			return "tone";
		case '3':
			return "dcs"; //TODO, should be D/O or something.
		case '4':
			return "dcs";
		}
		
		System.out.format("Unknowng tone mode %c, guessing none.\n",tonemode);
		return "";
	}

	@Override
	public void setToneMode(String mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getMode() {
		switch(mode) {
		case '1': return "LSB";
		case '2': return "USB";
		case '3': return "CW";
		case '4': return "FM";
		case '5': return "AM";
		case '6': return "RTTY-LSB";
		case '7': return "CW-R";
		case '8': return "DATA-LSB";
		case '9': return "RTTY-USB";
		case 'A': return "DATA-FM";
		case 'B': return "FM-N";
		case 'C': return "DATA-USB";
		case 'D': return "AM-N";
		case 'E': return "C4FM";
		}
		System.out.format("Confused by mode %c, defaulting to FM.\n",mode);
		return "FM";
	}

	@Override
	public void setMode(String m) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getURCALL() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void setURCALL(String call) {
		// TODO Auto-generated method stub

	}

}
