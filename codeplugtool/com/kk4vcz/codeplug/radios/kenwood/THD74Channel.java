package com.kk4vcz.codeplug.radios.kenwood;

import java.io.IOException;

import com.kk4vcz.codeplug.Channel;

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
	 * ME  p1,        p2,        p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13,   ??   p14,  p15, p16, p17, p18,    p19, p20,p21,p22
	 * ME 010,0145370000,0000600000,  0,  0,  0,  0,  1,  0,   0,   0,   0,   0,   0,   2,    08,  08, 000,   0, CQCQCQ,   0, 00,  0
	 * ME 002,0146520000,0000600000,  0,  0,  0,  0,  1,  1,   0,   0,   0,   0,   0,   0,  12,  08, 000,   0, CQCQCQ,   0, 00,  0
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
	 * p14 -- Mystery parameter goes somewhere near here.
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
	String p20=""; //DSTAR call.
	int p21=0; //DSTAR squelch type.
	int p22=0; //DSTAR squelch code.
	int p23=0; //lockout 
	
	private String render() throws IOException {
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
		System.out.format("# %s\n", row);
		
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
		
		
		//Regenerate the string tomake sure we parsed it right.
		if(!render().contentEquals(row)) {
			System.out.println("# WARNING: Rendered string disagrees with source!");
			System.out.format("# %s\n", render());
		}
		
		
		//Print the results.
		System.out.format("# Channel %03d\n", p1);
		System.out.format("# %f MHz %s (TX %f MHz)\n",
				getRXFrequency()/1000000.0, getMode(),
				getTXFrequency()/1000000.0 );
		if(getToneSent())
			System.out.format("# TX Tone %f\n", getTXToneFreq()/10.0);
		if(getToneRequired())
			System.out.format("# RX Tone %f\n", getRXToneFreq()/10.0);
		
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
			670,693, 719, 744,
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
	 * Tones are a little tricky.  p[16] contains the tone when just transmitting,
	 * but p17 contains the tone in CT mode.  Return zero when no tone is enabled.
	 */
	
	@Override
	public int getTXToneFreq() {
		if(p9==1 && p10==0) // T mode, just transmitting the tone.
			return tones[p16];
		if(p9==0 && p10==1) // CT mode, transmitting and receiving the tone.
			return tones[p17];
		
		//Other cases.
		return 0;
	}
	@Override
	public int getRXToneFreq() {
		if(p9==1 && p10==0) // T mode doesn't demand a tone.
			return 0;
		if(p9==0 && p10==1) // CT mode, transmitting and receiving the tone.
			return tones[p17];
		
		return 0;
	}

	@Override
	public boolean getToneSent() {
		if(p9==1 && p10==0) // T mode, just transmitting the tone.
			return true;
		if(p9==0 && p10==1) // CT mode, transmitting and receiving the tone.
			return true;
		
		//Other cases.
		return false;
	}

	@Override
	public boolean getToneRequired() {
		if(p9==1 && p10==0) // T mode doesn't demand a tone.
			return false;
		if(p9==0 && p10==1) // CT mode, transmitting and receiving the tone.
			return true;
		
		return false;
	}

	static String modes[]= {"FM", "DSTAR", "AM", "LSB", "USB", "CW", "NFM", "DR", "WFM", "R-CW"};
	@Override
	public String getMode() {
		return modes[p6];
	}

}
