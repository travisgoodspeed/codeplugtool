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

public class THD72 implements CATRadio {
	BufferedReader reader;
	PrintWriter writer;
	
	//Test routine.
	public static void main(String[] args) {
		String[] newargs= {"d72", "ttyUSB2", "info"};
		//String[] newargs= {"d72", "ttyUSB2", "dump"};
		CommandLineInterface.main(newargs);
	}

	public THD72(InputStream is, OutputStream os) throws IOException {
		reader=new BufferedReader(new InputStreamReader(is));
		writer=new PrintWriter(os);

		//Fake transaction to clear buffers.
		writer.write("asdf\r");
		writer.flush();
		reader.readLine();
	}

	@Override
	public void writeChannel(int index, Channel ch) throws IOException {
		THD72Channel channel=new THD72Channel(ch);
		System.out.println(Main.RenderChannel(channel));
		channel.setIndex(index);
		
		/* Skip the illegal frequencies.
		 */
		if(ch.getRXFrequency()>500000000 ||
				(ch.getRXFrequency()>200000000 && ch.getRXFrequency()<400000000)
				) {
			System.out.format("Cowardly refusing to write %f MHz to memory %03d.\n", (float) ch.getRXFrequency(), index);
			return;
		}
		
		/*
		 * Unlike the D74, the D710 does not echo the channel back,
		 * so we'll read it manually to compare the values.
		 */
		String cmdme=channel.renderme();
		rawCommand(cmdme);
		String res=rawCommand(String.format("ME %03d", index));
		if(!cmdme.equals(res)) {
			System.out.println("Command disagrees with response:\n"+cmdme+"\n"+res);
			System.exit(1);
		}
		
		// Then we set the name.
		String cmdmn=channel.rendermn();
		rawCommand(cmdmn);
		res=rawCommand(String.format("MN %03d", index));
		if(!cmdmn.trim().equals(res.trim())) {
			System.out.println("Command disagrees with response:\n"+cmdmn+"\n"+res);
			System.exit(1);
		}
	}
	

	@Override
	public Channel readChannel(int index) throws IOException {
		String rowme=rawCommand(String.format("me %03d", index));
		
		//Unused channel number.
		if(rowme.equals("N"))
			return null;
		
		String rowmn=rawCommand(String.format("mn %03d", index));
		
		THD72Channel ch=new THD72Channel(rowme, rowmn);
		
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
		
		return reader.readLine().trim();
	}

	@Override
	public void deleteChannel(int index) throws IOException {
		rawCommand(String.format("ME %03d,", index));
	}
}
