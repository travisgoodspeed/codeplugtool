package com.kk4vcz.codeplug.radios.kenwood;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.kk4vcz.codeplug.CATRadio;
import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.codeplug.CommandLineInterface;
import com.kk4vcz.codeplug.Main;

/*
 * This driver ought to also work for the TM-V71.
 * 
 * List of commands available on LA3QMA's handy page:
 * https://github.com/LA3QMA/TM-V71_TM-D710-Kenwood
 */

public class TMD710G implements CATRadio {
	BufferedReader reader;
	PrintWriter writer;
	
	//Test routine.
	public static void main(String[] args) {
		//String[] newargs= {"d710", "ttyS1", "info"};
		String[] newargs= {"d710", "ttyS1", "dump"};
		CommandLineInterface.main(newargs);
	}

	public TMD710G(InputStream is, OutputStream os) throws IOException {
		reader=new BufferedReader(new InputStreamReader(is));
		writer=new PrintWriter(os);
	}

	@Override
	public void writeChannel(int index, Channel ch) throws IOException {
		// TODO Auto-generated method stub
		TMD710GChannel channel=new TMD710GChannel(ch);
		System.out.println(Main.RenderChannel(channel));
		channel.setIndex(index);
		String cmdme=channel.renderme();
		String res=rawCommand(cmdme);
		if(!cmdme.equals(res)) {
			System.out.println("Command disagrees with response:\n"+cmdme+"\n"+res);
			System.exit(1);
		}
		
		//TODO Memory Name
	}
	

	@Override
	public Channel readChannel(int index) throws IOException {
		String rowme=rawCommand(String.format("me %03d", index));
		
		//Unused channel number.
		if(rowme.equals("N"))
			return null;
		
		String rowmn=rawCommand(String.format("mn %03d", index));
		
		TMD710GChannel ch=new TMD710GChannel(rowme, rowmn);
		
		return ch;
	}
	@Override
	public String getVersion() throws IOException {
		//Grabbing just the main unit's version number.  Can differ from head unit.
		return rawCommand("fv 0").substring(5);
	}

	@Override
	public String getSerialNumber() throws IOException {
		String pair[]=rawCommand("ae").substring(3).split(",");
		return pair[0];
	}

	@Override
	public int getChannelMin() throws IOException {
		return 0;
	}

	@Override
	public int getChannelMax() throws IOException {
		return 999;
	}

	@Override
	public long peek32(long adr) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getID() throws IOException {
		String base=rawCommand("id").substring(3);
		String sub=rawCommand("ty").substring(3);
		
		return base+","+sub;
	}


	@Override
	public void setFrequency(long frequency) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public long getFrequency() throws IOException {
		String resp=rawCommand("FO 0");
		return Integer.valueOf(resp.split(",")[1]).longValue();
	}

	@Override
	public void setFrequencyB(long frequency) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public long getFrequencyB() throws IOException {
		String resp=rawCommand("FO 1");
		return Integer.valueOf(resp.split(",")[1]).longValue();
	}

	@Override
	public String getCallsign() throws IOException {
		// We can't get the callsign from the main unit, only the head unit knows it.
		return null;
	}

	@Override
	public void setCallsign(String callsign) throws IOException {
		// We can't set it either.
	}

	@Override
	public String rawCommand(String cmd) throws IOException {
		/*
		 * In Kenwoods, we write the cat command and then immediately read back
		 * the result.
		 */
		writer.print(cmd+"\r");
		writer.flush();
		
		return reader.readLine().strip();
	}

	@Override
	public void deleteChannel(int index) throws IOException {
		rawCommand(String.format("ME %03d,", index));
	}

}
