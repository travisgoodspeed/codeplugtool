package com.kk4vcz.codeplug;

/*
 * This is an abstract form of a channel.  Different implementations
 * hand the common cases.
 */

public interface Channel {
	//Frequency in Hz; split is figured out by the radio driver.
	long getRXFrequency();
	long getTXFrequency();
	
	//Tenths of a Hz.
	int getTXToneFreq();
	int getRXToneFreq();
	boolean getToneSent();
	boolean getToneRequired();
	
	
	//FM, FMN, FMW, AM, USB, LSB, USB-D, LSB-D, DMR, P25, DSTAR, CW, R-CW, etc
	String getMode();
}
