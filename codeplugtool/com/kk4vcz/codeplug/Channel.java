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
	
	//Tenths of a Hz.
	int getTXToneFreq();
	int getRXToneFreq();
	boolean getToneSent();
	boolean getToneRequired();
	
	
	//FM, FMN, FMW, AM, USB, LSB, USB-D, LSB-D, DMR, P25, DSTAR, CW, R-CW, etc
	String getMode();
}
