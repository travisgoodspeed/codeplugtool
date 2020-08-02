package com.kk4vcz.codeplug;

/*
 * This is an abstract form of a channel.  Different implementations
 * hand the common cases.
 */

public interface Channel {
	//Channel number in memory.
	int getIndex();
	void setIndex(int i);
	
	//Name
	String getName();
	void setName(String n);
	
	//Frequency in Hz; split is figured out by the radio driver.
	void setRXFrequency(long freq);
	long getRXFrequency();
	long getTXFrequency();
	String getSplitDir();//+, -, "simplex", or "split"
	long getOffset();
	void setOffset(String dir, long freq);
	
	int getDTCSCode();
	void setDTCSCode(int code);
	
	int getToneFreq();
	void setToneFreq(int freq);
	String getToneMode();          //"", "dcs", "tone", or "ct"
	void setToneMode(String mode);
	
	
	//FM, FMN, FMW, AM, USB, LSB, USB-D, LSB-D, DMR, P25, DSTAR, CW, R-CW, etc
	String getMode();
	void setMode(String m);
	
	
	//Only valid in DV (DSTAR) mode.
	String getURCALL();
	void setURCALL(String call);
}
