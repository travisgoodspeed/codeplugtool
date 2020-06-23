package com.kk4vcz.codeplug.radios.kenwood;

import java.io.IOException;

import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.codeplug.Main;

public class TMD710GChannel implements Channel {
	/* Like other Kenwoods, the LA3QMA page is the best source for the channel format.
	 * 
	 * ME p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16
	 * 
	 * 1	Memory channel number 3 digit
	 * 2	RX frequency in Hz 10 digit. C clears the channel
	 * 3	RX step size
	 * 4	Shift direction
	 * 5	Reverse
	 * 6	Tone status
	 * 7	CTCSS status
	 * 8	DCS status
	 * 9	Tone frequency
	 * 10	CTCSS frequency
	 * 11	DCS frequency
	 * 12	Offset frequency in Hz 8 digit
	 * 13	Mode
	 * 14	TX frequency in Hz 10 digit, or transmit freq for odd split
	 * 15	TX step size
	 * 16	Lock out
	 */
	
	int p1=0; //Memory channel number.
	long p2=146520000; //RX Frequency
	int p3=0; //rx step size.
	int p4=0; //shift direction
	int p5=0; //reverse
	int p6=0; //tone status
	int p7=0; //ctcss status
	int p8=0; //dcs status
	int p9=8; //tone frequency
	int p10=8; //CTCSS frequency
	int p11=47; //DCS frequency
	long p12=600000; //offset frequency in Hz, 8 digits
	int p13=0; //mode
	long p14=0; //TX freq in Hz, 10 digits
	int p15=0; //TX step size;
	int p16=0; //lock out
	
	String name="";
	
	//Test routine.
	public static void main(String[] args) {
		//Sample channel for the W4KEV repeater.
		String me="ME 010,0145370000,0,2,0,0,0,0,08,08,000,00600000,0,0000000000,0,0";
		String mn="MN 010,W4KEV";
		
		TMD710GChannel ch=new TMD710GChannel(me, mn);
		System.out.println(Main.RenderChannel(ch));
		
		System.out.println(me);
		System.out.println(ch.renderme());
		System.out.println(mn);
		System.out.println(ch.rendermn());
	}
	
	
	public TMD710GChannel(String me, String mn) {
		//First we parse the ME line.
		String[] words=("wasted,"+me.substring(3)).split(",");
		
		//All of the parameters are in a row, and we keep their meaning natively
		//to avoid complicated conversions.
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
		
		//Then we parse the name.
		name=mn.substring(7);
		
		
		//Regenerate the string to make sure we parsed it right.
		if(!renderme().contentEquals(me)) {
			System.out.println("# WARNING: Rendered string disagrees with source!");
			System.out.format("# %s\n", me);
			System.out.format("# %s\n", renderme());
		}
		
		//Regenerate the string to make sure we parsed it right.
		if(!rendermn().contentEquals(mn)) {
			System.out.println("# WARNING: Rendered string disagrees with source!");
			System.out.format("# %s\n", mn);
			System.out.format("# %s\n", rendermn());
		}
	}
	
	public TMD710GChannel(Channel src) throws IOException {
		Main.ApplyChannel(this, src);
	}
	
	public String renderme() {
		return String.format("ME %03d,%010d,%d,%d,%d,%d,%d,%d,%02d,%02d,%03d,%08d,%d,%010d,%d,%d", 
				p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16
				);
	}
	public String rendermn() {
		//TODO Check the name length, newlines?
		return String.format("MN %03d,%s", p1, name);
	}
	

	@Override
	public int getIndex() {
		return p1;
	}

	@Override
	public void setIndex(int i) {
		p1=i;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String n) {
		name=n;
	}
	
	private long roundfreq(long f) {
		long freq=(long) f/5000;
		return freq*5000;
	}
	
	@Override
	public void setRXFrequency(long freq) {
		//TODO Round the frequency when it violates the step size. 
		p2=roundfreq(freq);
	}

	@Override
	public long getRXFrequency() {
		return p2;
	}

	@Override
	public long getTXFrequency() {
		/* This function always returns a normalized frequency,
		 * so we calculate the shift from P12 and return the result.
		 * 
		 * Split mode is a special case where p14 is non-zero.
		 */
		
		if(p14!=0)
			return p14;
		
		switch(p4) {
		case 0: //simplex
			return getRXFrequency();
		case 1: //Up
			return getRXFrequency()+p12;
		case 2: //Down
			return getRXFrequency()-p12;
		}

		System.out.format("Unexpected shift %d, expected less than 4.  Assuming simplex.\n", p15);
		return getRXFrequency();
	}

	@Override
	public String getSplitDir() {
		if(p14!=0)
			return "split";

		switch(p4) {
		case 0: //simplex
			return "";
		case 1: //Up
			return "+";
		case 2: //Down
			return "-";
		}
		return "error";
	}

	@Override
	public long getOffset() {
		// Always the absolute value of the offset.
		// Direction is read separately.
		return p12;
	}

	@Override
	public void setOffset(String dir, long freq) {
		if(dir.equals("+")) {
			p4=1; //Up
			p12=freq;
			p14=0;
		} else if(dir.equals("-")) {
			p4=2; //Down
			p12=freq;
			p14=0;
		} else if(dir.equals("split")) {
			p4=0; //Would be simplex, except the offset is set.
			//TODO Verify that the transmit frequency fits a legal step size.
			p14=freq;
			p12=0;
		} else {
			p4=0; //simplex
			p14=0;
		}
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
		return codemap[p11];
	}

	@Override
	public void setDTCSCode(int code) {
		for(int i=0; i<codemap.length; i++)
			if(codemap[i]==code) {
				p11=i;
				return;
			}
		System.out.format("ERROR: %s wasn't set as a DTCSCode.\n", code);
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

	@Override
	public int getToneFreq() {
		return tones[p9];
	}

	@Override
	public void setToneFreq(int freq) {
		for(int i=0; i<tones.length; i++) {
			if(tones[i]==freq) {
				p9=i;
				return;
			}
		}
	}

	@Override
	public String getToneMode() {
		if(p6==1) return "tone";
		if(p7==1) return "ct";
		if(p8==1) return "dcs";
		
		//None by default.
		return "";
	}

	@Override
	public void setToneMode(String mode) {
		if(mode.equals("tone")) {
			p6=1;
			p7=0;
			p8=0;
		}else if(mode.equals("ct")) {
			p6=0;
			p7=1;
			p8=0;
		}else if(mode.equals("dcs")) {
			p6=0;
			p7=0;
			p8=1;
		}else if(mode.equals("")) {
			p6=0;
			p7=0;
			p8=0;
		}else {
			System.out.format("ERROR: %s is not a supported tone mode.  Defaulting to none.\n", mode);
			p6=0;
			p7=0;
			p8=0;
		}
	}

	static String modes[]= {"FM", "NFM", "AM"};
	@Override
	public String getMode() {
		return modes[p13];
	}

	@Override
	public void setMode(String m) {
		for(int i=0; i<modes.length; i++) {
			if(m.equals(modes[i])) {
				p13=i;
				return;
			}
		}
		System.out.format("ERROR: %s is not a supported mode.  Defaulting to FM.\n", m);
		p13=0;
	}

	@Override
	public String getURCALL() {
		// No DSTAR support.
		return null;
	}

	@Override
	public void setURCALL(String call) {
		// No DSTAR support.
	}

}
