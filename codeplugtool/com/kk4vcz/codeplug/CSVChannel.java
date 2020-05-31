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
		
		String tm=words[5];
		if(tm.equals("")) {
			setToneMode("");
		} else if(tm.equals("Tone")) {
			setToneMode("tone");
			setToneFreq((int) (Double.parseDouble(words[6])*10));
		} else if(tm.equals("TSQL")) {
			setToneMode("ct");
			setToneFreq((int) (Double.parseDouble(words[7])*10));
		} else if(tm.equals("DTCS")) {
			setToneMode("dcs");
		} else {
			System.out.format("ERROR: %s is an unsupported CSV tone mode.\n", tm);
		}
		
		setMode(words[10]);
		if(words[8].length()>0)
			setDTCSCode(Integer.parseInt(words[8]));
		else
			setDTCSCode(0);
		//TODO URCALL
	}
	public CSVChannel(String line) {
		parse(line);
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

	private String mode="FM";
	@Override
	public String getMode() {
		return mode;
	}
	@Override
	public void setMode(String m) {
		if(m.equals("Auto"))
			mode="FM";
		else
			mode=m;
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
	
	int tonefreq=0;
	@Override
	public int getToneFreq() {
		return tonefreq;
	}
	@Override
	public void setToneFreq(int freq) {
		tonefreq=freq;
	}
	
	String tonemode="";
	@Override
	public String getToneMode() {
		return tonemode;
	}
	@Override
	public void setToneMode(String mode) {
		if(mode.equals("") || mode.equals("tone") || mode.equals("ct") || mode.equals("dcs")) {
			tonemode=mode;
			return;
		}
		
		System.out.format("ERROR: %s isn't a known tone mode.  Defaulting to none.\n", mode);
		tonemode="";
	}
	
	int dtcscode=0;
	@Override
	public int getDTCSCode() {
		return dtcscode;
	}
	@Override
	public void setDTCSCode(int code) {
		dtcscode=code;
	}
	
	
	String urcall="CQCQCQ";
	@Override
	public String getURCALL() {
		return urcall;
	}
	@Override
	public void setURCALL(String call) {
		urcall=call;
	}
	
	

}
