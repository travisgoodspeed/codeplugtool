package com.kk4vcz.codeplug.radios.kenwood;

import java.io.IOException;

import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.codeplug.Main;

/*
 * This implements the Channel abstraction as used in the Kenwood TH-D74 HT.  Experiments have
 * shown that stale data often remains, so many fields are ignored when their value wouldn't
 * be meaningful.
 */

public class THD74Channel implements Channel {
	/*
	 * This is a bit confusing, because the LA3QMA page describes just 22 fields, but we see 23 in my radio with version 1.09 firmware.
	 * 
	 * 
	 * Channel string is like this:
	 * ME p1,p2,p3,...,p23
	 * ME 001,0146520000,0000600000,0,0,0,0,1,0,0,0,0,0,0,0,08,08,000,0,CQCQCQ,0,00,0
	 * ME 002,0146520000,0000600000,0,0,0,0,1,1,0,0,0,0,0,0,12,08,000,0,CQCQCQ,0,00,0
	 * 
	 * ME  p1,        p2,        p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14,  p15,  p16, p17,  p18, p19,    p20, p21, p22, p23
	 * ME 010,0145370000,0000600000,  0,  0,  0,  0,  1,  0,   0,   0,   0,   0,   0,   2,    08,  08,  000,   0, CQCQCQ,   0,  00,   0
	 * ME 002,0146520000,0000600000,  0,  0,  0,  0,  1,  1,   0,   0,   0,   0,   0,   0,    12,  08,  000,   0, CQCQCQ,   0,  00,   0
	 * 
	 * p1  -- 3 digit channel number, 000-999
	 * p2  -- 10 digit freq in Hz.
	 * p3  -- 10 digit offset freq in Hz.  (Raw freq in split?)
	 * p4  -- Step size,
	 * p5  -- Transmit step size.
	 * p6  -- Mode
	 * p7  -- Fine mode.
	 * p8  -- Fine step size.
	 * P9  -- Tone status.
	 * p10 -- CTCSS status.
	 * p11 -- DCS status.
	 * p12 -- CTCSS/DCS status.
	 * p13 -- Reverse
	 * p14 -- Mystery bit, seems to enable split mode.
	 * p15 -- Shift direction.
	 * p16 -- Tone frequency.
	 * p17 -- CTCSS frequency.
	 * p18 -- DCS frequency.
	 * p19 -- Cross encode/decode.
	 * p20 -- DSTAR URCALL
	 * p21 -- DSTAR squelch type.
	 * p22 -- DSTAR squelch code.
	 * p23 -- Lockout.
	 */
	
	int p1 = 000; //Channel number.
	long p2 = 146520000; //RX frequency
	long p3 = 600000; //Offset frequency.
	int p4 = 0; //Step size.
	int p5 = 0; //TX step size.
	int p6 = 0; //Mode
	int p7 = 0; //Fine mode.
	int p8 = 1; //Fine step size.
	int p9 = 0; //Tone off by default.
	int p10 = 0; //CTCSS off.
	int p11 = 0; //DCS status.
	int p12 = 0; //DTCSS/DCS Status
	int p13 = 0; //Reverse off.
	int p14 = 0; //Mystery parameter.
	int p15=0; //Simplex by default.  1 for up, 2 for down, 3 might be split.
	int p16=0; //Tone freq.
	int p17=0; //CTCSS freq.
	int p18=0; //DCS freq.
	int p19=0; //Cross encode/decode.
	String p20="CQCQCQ"; //DSTAR call.
	int p21=0; //DSTAR squelch type.
	int p22=0; //DSTAR squelch code.
	int p23=0; //lockout 
	
	
	public String render() throws IOException {
		/* This reproduces the original command, so that we can write it back to the radio.
		 */
		
		return String.format(
				"ME %03d,%010d,%010d,"+
				"%d,%d,%d,%d,%d,%d,"+
				"%d,%d,%d,%d,%d,%d,"+		
				"%02d,%02d,%03d,%d,%s,%d,%02d,%d",
				
				p1, p2, p3,
				p4, p5, p6, p7, p8, p9,
				p10, p11, p12, p13, p14, p15,
				p16, p17, p18, p19, p20, p21, p22, p23 
				);
	}
	
	
	private void parse(String row) throws IOException {
		//We add one extra word, then compare the result.
		String[] words=("wasted,"+row.substring(3)).split(",");
		//System.out.format("# %s\n", row);
		
		if(words.length!=24) {
			throw new IOException(String.format("ERROR: %d words, not the 23 expected\n", words.length));
		}
		
		p1=Integer.parseInt(words[1]);
		p2=Integer.parseInt(words[2]);
		p3=Integer.parseInt(words[3]);
		p4=Integer.parseInt(words[4]);
		p5=Integer.parseInt(words[5]);
		p6=Integer.parseInt(words[6]);
		p7=Integer.parseInt(words[7]);
		p8=Integer.parseInt(words[8]);
		p9=Integer.parseInt(words[9]);
		p10=Integer.parseInt(words[10]);
		p11=Integer.parseInt(words[11]);
		p12=Integer.parseInt(words[12]);
		p13=Integer.parseInt(words[13]);
		p14=Integer.parseInt(words[14]);
		p15=Integer.parseInt(words[15]);
		p16=Integer.parseInt(words[16]);
		p17=Integer.parseInt(words[17]);
		p18=Integer.parseInt(words[18]);
		p19=Integer.parseInt(words[19]);
		p20=words[20];
		p21=Integer.parseInt(words[21]);
		p22=Integer.parseInt(words[22]);
		p23=Integer.parseInt(words[23]);
		
		
		/* If p14=1,p15=3 we're in split mode.
		if(p14!=0) {
			System.out.format("p14=%d\n%s\n", p14, row);
		}
		*/
		
		//Regenerate the string tomake sure we parsed it right.
		if(!render().contentEquals(row)) {
			System.out.println("# WARNING: Rendered string disagrees with source!");
			System.out.format("# %s\n", row);
			System.out.format("# %s\n", render());
		}
	}
	
	public THD74Channel(Channel ch) throws IOException {
		Main.ApplyChannel(this, ch);
	}
	public THD74Channel(String row) throws IOException {
		parse(row);
	}
	
	public String dump() {
		return String.format("%03d -- %l", p1, p2);
	}

	@Override
	public long getRXFrequency() {
		return p2;
	}
	
	@Override
	public String getSplitDir() {
		if(p14==1)
			return "split";
		
		switch(p15) {
		case 0: //simplex
			return "";
		case 1: //Up
			return "+";
		case 2: //Down
			return "-";
		case 3: //Split
			return "split";
		}
		return "error";
	}
	
	@Override
	public void setRXFrequency(long freq) {
		p2=freq; 
	}


	@Override
	public void setOffset(String dir, long freq) {
		if(dir.equals("+")) {
			p15=1; //Up
			p3=freq;
		} else if(dir.equals("-")) {
			p15=2; //Down
			p3=freq;
		} else if(dir.equals("split")) {
			/* From the initial description of the protocol,
			 * it seems that there is no split and that we need
			 * to fake it with other methods.  This is the code
			 * for faking it.
			
			if(freq>getRXFrequency()) {
				p15=1; //Up
				p3=freq-getRXFrequency();
			}else if(freq<getRXFrequency()) {
				p15=2; //Down
				p3=getRXFrequency()-freq;
			}else {
				p15=0;//simplex
			}
			*/
			
			
			/* This seems to work well on my American TH-D74,
			 * but uses a field missing from the public protocol docs. */
			p14=1; //Split mode.
			p15=3;
			p3=freq;
		} else {
			p15=0; //simplex
		}
		
	}


	@Override
	public long getOffset() {
		// Always the absolute value of the offset.
		// Direction is read separately.
		return p3;
	}

	@Override
	public long getTXFrequency() {
		/* This function always returns a normalized frequency,
		 * so we calculate the shift from P15 and return the result.
		 */
		
		switch(p15) {
		case 0: //simplex
			return getRXFrequency();
		case 1: //Up
			return getRXFrequency()+p3;
		case 2: //Down
			return getRXFrequency()-p3;
		case 3: //Split
			return p3;
		}

		System.out.format("Unexpected shift %d, expected less than 4.  Assuming simplex.\n", p15);
		return getRXFrequency();
	}

	static int tones[]= {
			670, 693, 719, 744,
			770, 797, 825, 854,
			885, 915, 948, 974,
			1000, 1035, 1072, 1109,
			1148, 1188, 1230, 1273,
			1318, 1365, 1413, 1462,
			1514, 1567, 1598, 1622,
			1655, 1679, 1713, 1738,
			1773, 1799, 1835, 1862,
			1899,  1928, 1966, 1995,
			2035,  2065, 2107, 2181,
			2257,  2291, 2336, 2418,
			2503,  2541			
	};
	
	/*
	 * Tones are a little tricky.  p16 contains the tone when just transmitting,
	 * but p17 contains the tone in CT mode.  Return zero when no tone is enabled.
	 */
	
	public int getTXToneFreq() {
		if(p9==1 && p10==0) // T mode, just transmitting the tone.
			return tones[p16];
		if(p9==0 && p10==1) // CT mode, transmitting and receiving the tone.
			return tones[p17];
		
		//Other cases.
		return 0;
	}
	public int getRXToneFreq() {
		if(p9==1 && p10==0) // T mode doesn't demand a tone.
			return 0;
		if(p9==0 && p10==1) // CT mode, transmitting and receiving the tone.
			return tones[p17];
		
		return 0;
	}
	
	@Override
	public int getToneFreq() {
		if(p9==1)
			return tones[p16];
		if(p10==1)
			return tones[p17];
		
		return 0;
	}
	

	
	@Override
	public void setToneFreq(int freq) {
		for(int i=0; i<tones.length; i++) {
			if(tones[i]==freq) {
				//Set both of the potential tones for now.
				p16=i;
				p17=i;
				return;
			}
		}
		
		//We don't have to complain about a zero tone.
		if(freq!=0)
			System.out.format("Unsupported tone %f Hz.", freq/10.0);
	}
	
	@Override
	public String getToneMode() {
		if(p9==1) return "tone";
		if(p10==1) return "ct";
		if(p11==1) return "dcs";
		if(p12==1)
			System.out.format("ERROR: p12 is not a supported tone mode.  Defaulting to none.\n");
		
		//None by default.
		return "";
	}

	@Override
	public void setToneMode(String mode) {
		if(mode.equals("tone")) {
			p9=1;
			p10=0;
			p11=0;
			p12=0;
		}else if(mode.equals("ct")) {
			p9=0;
			p10=1;
			p11=0;
			p12=0;
		}else if(mode.equals("dcs")) {
			p9=0;
			p10=0;
			p11=1;
			p12=0;
		}else if(mode.equals("")) {
			p9=0;
			p10=0;
			p11=0;
			p12=0;
		}else {
			System.out.format("ERROR: %s is not a supported tone mode.  Defaulting to none.\n", mode);
			p9=0;
			p10=0;
			p11=0;
			p12=0;
		}
	}

	static String modes[]= {"FM", "DV", "AM", "LSB", "USB", "CW", "NFM", "DR", "WFM", "R-CW"};
	@Override
	public String getMode() {
		return modes[p6];
	}

	@Override
	public void setMode(String m) {
		for(int i=0; i<modes.length; i++) {
			if(m.equals(modes[i])) {
				p6=i;
				return;
			}
		}
		System.out.format("ERROR: %s is not a supported mode.  Defaulting to FM.\n", m);
		p6=0;
	}


	@Override
	public int getIndex() {
		return p1;
	}


	@Override
	public void setIndex(int i) {
		p1=i;
	}


	//TODO Names aren't part of the entry string.  Not sure what to do about that.
	String name="";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String n) {
		name=n;
	}

	//This is the listing of integer codes indexed by their value within the radio.
	static int codemap[]= {
		23, 25, 26, 31, 32, 36, 43, 47, 51, 53, 54, 65, 71, 72, 73, 74, 114, 115, 116, 122,
		125, 131, 132, 134, 143, 145, 152, 155, 156, 162, 165, 172, 174, 205, 212, 223, 225,
		226, 243, 244, 245, 246, 251, 252, 255, 261, 263, 265, 266, 271, 274, 306, 311, 315,
		325, 331, 332, 343, 346, 351, 356, 364, 365, 371, 411, 412, 413, 423, 431, 432, 445,
		446, 452, 454, 455, 462, 464, 465, 466, 503, 506, 516, 523, 526, 532, 546, 565, 606,
		612, 624, 627, 631, 632, 654, 662, 664, 703, 712, 723, 731, 732, 734, 743, 754
	};
	
	@Override
	public int getDTCSCode() {
		return codemap[p18];
	}

	@Override
	public void setDTCSCode(int code) {
		for(int i=0; i<codemap.length; i++)
			if(codemap[i]==code) {
				p18=i;
				return;
			}
		System.out.format("ERROR: %s wasn't set as a DTCSCode.\n", code);
	}


	@Override
	public String getURCALL() {
		return p20;
	}


	@Override
	public void setURCALL(String call) {
		p20=call;
	}

}
