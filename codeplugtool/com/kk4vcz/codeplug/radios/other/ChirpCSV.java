package com.kk4vcz.codeplug.radios.other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.codeplug.Radio;

/* This handy little class implements Chirp's CSV file format,
 * which is incredibly useful for importing and exporting codeplugs.
 */

public class ChirpCSV implements Radio {
	CSVChannel[] channels=new CSVChannel[1000];
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("I should probably write a proper test case for this class.");
	}
	
	//Empty constructor with no channels.
	public ChirpCSV() {
		//Empty document.
	}
	
	//Dump a radio.
	public ChirpCSV(Radio r) throws IOException {
		for(int i=r.getChannelMin(); i<=r.getChannelMax(); i++) {
			Channel ch=r.readChannel(i);
			if(ch!=null) {
				channels[i]=new CSVChannel(ch);
			}
		}
	}
	
	//Read a file.
	public ChirpCSV(File f) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));

		// Toss the first line, which contains the header.
		reader.readLine();
		// Print the body lines.
		while(reader.ready()) {
			Channel c = new CSVChannel(reader.readLine());
			if (c != null) {
				writeChannel(c.getIndex(), c);
			}
		}
		reader.close();

		System.out.println("done");
	}
	
	//Read a file.
	public ChirpCSV(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		// Toss the first line, which contains the header.
		reader.readLine();
		// Print the body lines.
		while(reader.ready()) {
			Channel c = new CSVChannel(reader.readLine());
			if (c != null) {
				writeChannel(c.getIndex(), c);
			}
		}
		reader.close();
		System.out.println("done");
	}
	
	public void exportToFile(File f) throws IOException {
		BufferedWriter writer=new BufferedWriter(new FileWriter(f));
		
		//Standard header.
		writer.write("Location,Name,Frequency,Duplex,Offset,Tone,rToneFreq,cToneFreq,DtcsCode,DtcsPolarity,Mode,TStep,Skip,Comment,URCALL,RPT1CALL,RPT2CALL,DVCODE\n");
		for(int i=getChannelMin(); i<=getChannelMax(); i++) {
			CSVChannel ch=channels[i];
			if(ch!=null) {
				writer.write(ch.renderCSV());
			}
		}
		
		writer.flush();
		writer.close();
	}
	
	public void exportToOutputStream(OutputStream os) throws IOException {
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os));
		
		//Standard header.
		writer.write("Location,Name,Frequency,Duplex,Offset,Tone,rToneFreq,cToneFreq,DtcsCode,DtcsPolarity,Mode,TStep,Skip,Comment,URCALL,RPT1CALL,RPT2CALL,DVCODE\n");
		for(int i=getChannelMin(); i<=getChannelMax(); i++) {
			CSVChannel ch=channels[i];
			if(ch!=null) {
				writer.write(ch.renderCSV());
			}
		}
		
		writer.flush();
		writer.close();
	}
	
	@Override
	public void writeChannel(int index, Channel ch) throws IOException {
		channels[index]=new CSVChannel(ch);
	}

	@Override
	public Channel readChannel(int index) throws IOException {
		if(channels[index]==null)
			return null;
		return new CSVChannel(channels[index]);
	}

	@Override
	public void deleteChannel(int index) throws IOException {
		channels[index]=null;
	}

	@Override
	public String getVersion() throws IOException {
		return null;
	}

	@Override
	public String getSerialNumber() throws IOException {
		return null;
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
		return 0;
	}


}
