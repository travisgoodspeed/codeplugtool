package com.kk4vcz.codeplug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * RadioConnection interfaces are used to expose generic streams to the radio,
 * which are probably from a serial port but might also be Bluetooth RFCOMM,
 * USB or a TCP socket.
 * 
 * Meaningless entries, such as the baud rate of a TCP socket, are to return
 * without error.
 */

public interface RadioConnection {
	InputStream getInputStream() throws IOException;
	OutputStream getOutputStream() throws IOException;
	
	int setBaudRate(int baudrate);
	boolean isOpen();
}
