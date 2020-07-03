package com.kk4vcz.codeplug.connections;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;
import com.kk4vcz.codeplug.RadioConnection;

public class JSerialCommConnection implements RadioConnection {
	static public JSerialCommConnection getConnection(String portname) {
		return new JSerialCommConnection(SerialPort.getCommPort(portname));
	}
	
	private SerialPort port;
	public JSerialCommConnection(SerialPort port) {
		this.port=port;
		if(port.openPort()) {
			port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1, 0);
		}
	}

	@Override
	public InputStream getInputStream() {
		return port.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() {
		return port.getOutputStream();
	}

	@Override
	public int setBaudRate(int baudrate) {
		port.setBaudRate(baudrate);
		return port.getBaudRate();
	}

	public static void main(String[] args) {
		//Self test to verify that the serial port library is properly linked.  It just prints the ports.
		SerialPort[] ports=SerialPort.getCommPorts();
		for(int i=0; i<ports.length; i++) {
			System.out.format("\t%s\t-- %s\n", ports[i].getSystemPortName(), ports[i].getDescriptivePortName());
		}
	}

	@Override
	public boolean isOpen() {
		return port.isOpen();
	}

	@Override
	public void close() throws IOException {
		port.closePort();
	}

}
