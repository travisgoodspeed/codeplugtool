package com.kk4vcz.codeplug.connections;

import java.net.*;
import java.io.*;

import com.kk4vcz.codeplug.CommandLineInterface;
import com.kk4vcz.codeplug.RadioConnection;

/*
 * This class is mostly used for testing the Android app without
 * Bluetooth, but it might also be handy for programming a radio
 * across a local network.
 * 
 * Start the server like this:
 * socat tcp-l:54321,reuseaddr,fork file:/dev/ttyS1,nonblock,raw,echo=0
 */

public class TCPConnection implements RadioConnection {
	public static void main(String[] args) {
		String[] newargs= {"d710n", "localhost:54321", "info"};
		//String[] newargs= {"d710", "ttyS1", "dump"};
		CommandLineInterface.main(newargs);
	}
	
	static public TCPConnection getConnection(String path) throws IOException{
		String[] words=path.split(":");
		String hostname="localhost";
		int port=54321;
		if(words.length==2) {
			hostname=words[0];
			port=Integer.parseInt(words[1]);
		}
		return new TCPConnection(hostname, port);
	}

	private Socket socket;
	public TCPConnection(String server, int port) throws IOException {
		socket=new Socket(server, port);
	}

	@Override
	public InputStream getInputStream()  throws IOException {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream()  throws IOException {
		return socket.getOutputStream();
	}

	@Override
	public int setBaudRate(int baudrate) {
		// No such thing as a baud rate in TCP.  Better hope your server knows it.
		return 0;
	}

	@Override
	public boolean isOpen() {
		return socket.isConnected();
	}
}
