package com.kk4vcz.codeplug.connections;

import java.net.*;
import java.util.concurrent.TimeUnit;
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
		String[] newargs= {"d710", "localhost:54321", "info"};
		//String[] newargs= {"d710", "localhost:54321", "dump"};
		
		
		//Perform twenty connections to verify that there's no problem with reconnecting.
		for(int i=0; i<20; i++)
			CommandLineInterface.main(newargs);
		System.out.println("done.");
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
		//socket=new Socket(server, port);
		socket=new Socket();
		
		//First try.
		socket.connect(new InetSocketAddress(server,  port), 1000);		
		
		//If bytes are waiting, we flush the channel for safety.
		InputStream is=socket.getInputStream();
		if(is.available()>0) {
			System.out.println("Flushing waiting bytes for safety.");
			byte[] b=new byte[is.available()];
			is.read(b);
		}
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

	@Override
	public void close() throws IOException {
		socket.close();
		
		/* This part sucks, but it's important.  With repeated connections as seen in Android,
		 * we need to delay a bit after closing the socket so that the server can be ready for
		 * the next connection.
		 */
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}
