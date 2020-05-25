package com.kk4vcz.codeplug.radios.kenwood;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.kk4vcz.codeplug.CATRadio;
import com.kk4vcz.codeplug.Channel;


/*
 * The Kenwood TH-D74 is a tri-band HT with handy extra modes for SSB on a
 * wide variety of frequencies.
 * 
 * See https://github.com/LA3QMA/TH-D74-Kenwood for the CAT commands.
 */


public class THD74 implements CATRadio {
	BufferedReader reader;
	PrintWriter writer;
	
	
	public THD74(InputStream is, OutputStream os) throws IOException {
		reader=new BufferedReader(new InputStreamReader(is));
		writer=new PrintWriter(os);
	}
	
	//Sends a command and returns the response.
	private String transact(String cmd) throws IOException{
		/*
		 * In the TH-D74, we write the cat command and then immediately read back
		 * the result.
		 */
		writer.print(cmd+"\r");
		writer.flush();
		
		return reader.readLine().strip();
	}
	

	@Override
	public void writeChannel(int index, Channel ch) throws IOException {
		// TODO Auto-generated method stub
		THD74Channel channel=new THD74Channel(ch);
		channel.setIndex(index);
		String cmd=channel.render();
		String res=transact(cmd);
		if(!cmd.equals(res)) {
			System.out.println("Command disagrees with response:\n"+cmd+"\n"+res);
			System.exit(1);
		}
	}
	

	@Override
	public Channel readChannel(int index) throws IOException {
		String row=transact(String.format("me %03d", index));
		
		//Unused channel number.
		if(row.equals("N"))
			return null;
		
		THD74Channel ch=new THD74Channel(row);
		
		return ch;
	}

	@Override
	public String getID() throws IOException {
		String base=transact("id").substring(3);
		String sub=transact("ty").substring(3);
		
		return base+","+sub;
	}

	@Override
	public void setFrequency(long frequency) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getFrequency() throws IOException {
		String freq=transact("FQ 0");
		return Integer.valueOf(freq.substring(5)).longValue();
	}

	@Override
	public String getCallsign() throws IOException {
		return transact("CS").substring(3);
	}

	@Override
	public void setCallsign(String callsign) throws IOException {
		transact("CS "+callsign);
	}

	@Override
	public void setFrequencyB(long frequency) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getFrequencyB() throws IOException {
		String freq=transact("FQ 1");
		return Integer.valueOf(freq.substring(5)).longValue();
	}

	@Override
	public String getVersion() throws IOException {
		return transact("fv").substring(3);
	}

	@Override
	public String getSerialNumber() throws IOException {
		String pair[]=transact("ae").substring(3).split(",");
		return pair[0];
	}
}
