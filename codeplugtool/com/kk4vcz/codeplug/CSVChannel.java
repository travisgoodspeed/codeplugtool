package com.kk4vcz.codeplug;

/* This is a simple implementation of CSV channel entries, compatible with those from Chirp.
 * Location,Name,Frequency,Duplex,Offset,Tone,rToneFreq,cToneFreq,DtcsCode,DtcsPolarity,Mode,TStep,Skip,Comment,URCALL,RPT1CALL,RPT2CALL,DVCODE
 * 0,,146.010000,,0.600000,,88.5,88.5,023,NN,FM,5.00,,,,,,
 * 100,,147.315000,+,0.600000,,88.5,88.5,023,NN,FM,5.00,,Rogersville,,,,
 * 101,W4KEV,146.895000,-,0.600000,Tone,100.0,88.5,023,NN,FM,5.00,,Dandridge,,,,
 * 102,W4KEV,145.410000,-,0.600000,Tone,127.3,127.3,023,NN,FM,5.00,,"Greeneville, Bald Mtn",,,,
 * 
 * Location,Name,Frequency,Duplex,    Offset,Tone,rToneFreq,cToneFreq,DtcsCode,DtcsPolarity,Mode,TStep,Skip,Comment,URCALL,RPT1CALL,RPT2CALL,DVCODE
 *        0,    ,146.010000,     ,  0.600000,    ,     88.5,     88.5,     023,          NN,  FM,  5.00,,,,,,
 *        0,   1,         2,    3,         4,   5,        6,        7,       8,           9,   10,   11, etc
 */

public class CSVChannel implements Channel {

	private void parse(String line) {
		/*
		 * This is an ugly-ass shotgun parser.  It should be rewirtten properly when time allows.
		 */
		String[] words=line.split(",");
		setIndex(Integer.parseInt(words[0]));
		setName(words[1]);
		setRXFrequency((long) (Double.parseDouble(words[2])*1000000.0));
		setOffset(words[3], (long) (Double.parseDouble(words[4])*1000000.0));
		
		//TODO tones
		//TODO Mode
		//TODO URCALL
	}
	public CSVChannel(String line) {
		parse(line);
	}
	
	public void apply(Channel c) {
		Main.ApplyChannel(this, c);;
		
	}

	private int index=0;
	@Override
	public int getIndex() {
		return index;
	}
	@Override
	public void setIndex(int i) {
		index=i;
	}

	private long rxfreq=0;
	@Override
	public long getRXFrequency() {
		return rxfreq;
	}

	
	@Override
	public long getTXFrequency() {
		if(splitdir.equals("+"))
			return rxfreq+offset;
		if(splitdir.equals("-"))
			return rxfreq-offset;
		
		if(splitdir.equals("split"))
			return offset;
		
		//Simplex by default.
		return rxfreq;
	}

	private String splitdir="";
	@Override
	public String getSplitDir() {
		return splitdir;
	}

	private long offset=0;
	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public int getTXToneFreq() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRXToneFreq() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getToneSent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getToneRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	private String mode="FM";
	@Override
	public String getMode() {
		return mode;
	}
	@Override
	public void setRXFrequency(long freq) {
		rxfreq=freq;
	}
	@Override
	public void setOffset(String dir, long freq) {
		splitdir=dir;
		offset=freq;
	}
	
	String name="";
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String n) {
		name=n;
	}
	

}
