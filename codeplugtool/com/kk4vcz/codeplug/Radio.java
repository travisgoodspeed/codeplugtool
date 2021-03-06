package com.kk4vcz.codeplug;

import java.io.IOException;

/* This is an abstract interface to a Radio.  Don't implement it directly,
 * but rather implement either CATRadio for devices which are controlled live
 * or ImageRadio for devices which use a byte image read and written from memory.
 */

public interface Radio {
	public void writeChannel(int index, Channel ch) throws IOException;
	public Channel readChannel(int index) throws IOException;
	public void deleteChannel(int index) throws IOException;
	
	public String getVersion() throws IOException;
	public String getSerialNumber() throws IOException;
	
	public int getChannelMin() throws IOException;
	public int getChannelMax() throws IOException;
	
	public long peek32(long adr) throws IOException;
}
