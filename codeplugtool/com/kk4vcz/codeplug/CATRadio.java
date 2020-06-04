package com.kk4vcz.codeplug;

import java.io.IOException;

/*
 * These are functions which work over CAT from a live radio
 * but cannot work on an image.  Your radio probably supports
 * most of these, but the others should quietly fail with a log
 * message.
 */

public interface CATRadio extends Radio {
	
	String getID() throws IOException;
	void setFrequency(long frequency) throws IOException;
	long getFrequency() throws IOException;
	void setFrequencyB(long frequency) throws IOException;
	long getFrequencyB() throws IOException;
	
	String getCallsign() throws IOException;
	void setCallsign(String callsign) throws IOException;
	
	String rawCommand(String cmd) throws IOException;
}
