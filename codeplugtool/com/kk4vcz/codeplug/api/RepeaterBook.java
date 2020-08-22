package com.kk4vcz.codeplug.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.kk4vcz.codeplug.CommandLineInterface;
import com.kk4vcz.codeplug.Radio;
import com.kk4vcz.codeplug.RadioAPI;
import com.kk4vcz.codeplug.radios.other.ChirpCSV;
import com.kk4vcz.codeplug.radios.other.CSVChannel;

public class RepeaterBook implements RadioAPI {
	//Test method, to fetch a given query.
	public static void main(String[] args) {
		System.out.println("Testing a simple RepeaterBook query:");
		RepeaterBook rb=new RepeaterBook();
		Radio res;
		long freq;
		
		try {
			/*
			System.out.println("33cm test");
			res=rb.queryProximity("Knoxville, TN", 25, 915*1000000);  //Grab stations within 25 miles of Knoxville.
			CommandLineInterface.dump(res);                 //Print them.
			freq=res.readChannel(0).getRXFrequency();
			assert freq>900*1000000: "33cm freq is too low.";
			assert freq<1000*1000000: "33cm freq is too high.";
			
			System.out.println("70cm test");
			res=rb.queryProximity("Knoxville, TN", 25, 433*1000000);  //Grab stations within 25 miles of Knoxville.
			CommandLineInterface.dump(res);                 //Print them.
			freq=res.readChannel(0).getRXFrequency();
			assert freq>400*1000000: "70cm freq is too low.";
			assert freq<500*1000000: "70cm freq is too high.";
			
			System.out.println("220MHz test");
			res=rb.queryProximity("Knoxville, TN", 25, 220*1000000);  //Grab stations within 25 miles of Knoxville.
			CommandLineInterface.dump(res);                 //Print them.
			freq=res.readChannel(0).getRXFrequency();
			assert freq>200*1000000: "220MHz freq is too low.";
			assert freq<300*1000000: "220MHz freq is too high.";
			
			System.out.println("2m test");
			res=rb.queryProximity("Knoxville, TN", 25, 145*1000000);  //Grab stations within 25 miles of Knoxville.
			CommandLineInterface.dump(res);                 //Print them.
			freq=res.readChannel(0).getRXFrequency();
			assert freq>100*1000000: "2m freq is too low.";
			assert freq<200*1000000: "2m freq is too high.";
			
			System.out.println("4m test");
			res=rb.queryProximity("Manchester", 200, 70*1000000);  //No 4m band in America, so we use Manchester.
			CommandLineInterface.dump(res);                 //Print them.
			freq=res.readChannel(0).getRXFrequency();
			assert freq>65*1000000: "4m freq is too low.";
			assert freq<75*1000000: "4m freq is too high.";
						
			System.out.println("6m test");
			res=rb.queryProximity("Knoxville, TN", 25, 55*1000000);  //Grab stations within 25 miles of Knoxville.
			CommandLineInterface.dump(res);                 //Print them.
			freq=res.readChannel(0).getRXFrequency();
			assert freq>70*1000000: "6m freq is too low.";
			assert freq<50*1000000: "6m freq is too high.";
			*/
			
			System.out.println("10m test");
			res=rb.queryProximity("Knoxville, TN", 250, 29*1000000);  //Grab stations within 25 miles of Knoxville.
			CommandLineInterface.dump(res);                 //Print them.
			freq=res.readChannel(0).getRXFrequency();
			assert freq>30*1000000: "10m freq is too low.";
			assert freq<28*1000000: "10m freq is too high.";
			
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private String freq2band(long freq) {
		if(freq >= 10000000000L) //10GHz band
			return "10";
		if(freq >= 1200000000) //1.2GHz band
			return "12";
		if(freq >= 900000000) //900MHz
			return "9"; //33cm
		if(freq >= 400000000)
			return "4"; //400MHz
		if(freq >= 300000000)
			return "3"; //300MHz, pretty much empty.
		if(freq >= 200000000)
			return "2"; //220MHz  
		if(freq >= 140000000)
			return "1"; //2 meters
		//13 is the 136.125 repeater in Khon Kean.
		if(freq >=  70000000)
			return "7"; //4 meters  FIXME
		if(freq >=  50000000)
			return "5"; //6 meters
		if(freq >=  25000000)
			return "3"; //10 meters
		
		if(freq>0)
			System.err.println("Frequency "+freq+"is in an unknown repeater band.");
		return null;
	}

	@Override
	public Radio queryProximity(String loc, float distance, long band) throws IOException {
		String bandstr=freq2band(band);
		
		
		try {
			Map<String, String> parameters = new HashMap<>();
			parameters.put("loc", loc);
			parameters.put("dist", ""+(int) distance);
			if(bandstr!=null)
				parameters.put("band", bandstr);
			
			
			// Two servers support this protocol.
			URL url = new URL("https://www.repeaterbook.com/repeaters/downloads/CHIRP/app_direct.php?"+ParameterStringBuilder.getParamsString(parameters));
			//URL url = new URL("http://chirp.danplanet.com/query/rb/1.0/app_direct?"+ParameterStringBuilder.getParamsString(parameters));
			
			
			//Begin with the simple connection.
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			//con.setRequestProperty("User-Agent", "curl/7.68.0");
			con.setRequestProperty("User-Agent", "Python-urllib/2.6");
			con.setDoOutput(true);
			
			int status = con.getResponseCode();
			if(status!=200) {
				System.out.println("Request status="+status);
				return null;
			}
			
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			ChirpCSV csvradio=new ChirpCSV();
			int i=0;
			
			//Toss the first line, which ought to be:
			//Location,Name,Frequency,Duplex,Offset,Tone,rToneFreq,cToneFreq,DtcsCode,DtcsPolarity,Mode,TStep,Comment
			in.readLine();
			
			while ((inputLine = in.readLine()) != null) {
				/* So each line of the response is a line of a CHIRP CSV file, but it's generated by difference
				 * code so there might be differences.  For example, the first entry is missing an index number.
				 * 
				 * We try our best to correct that here.
				 */
			    content.append(inputLine+"\n");
			    try {
			    	CSVChannel ch=new CSVChannel(inputLine);
			    	csvradio.writeChannel(i++,  ch);
			    	//System.out.println(inputLine);
			    }catch(Exception e) {
			    	e.printStackTrace();
			    	System.err.println("Error parsing channel: "+inputLine);
			    }
			    
			}
			in.close();
			
			//System.out.println(content);
			
			return csvradio;
			
		}catch(MalformedURLException e) {
			e.printStackTrace();
		}catch(ProtocolException e) {
			e.printStackTrace();			
		}
			
		//Some serious error, so we return null.
		return null;
	}
	
	private static class ParameterStringBuilder {
	    public static String getParamsString(Map<String, String> params) 
	      throws UnsupportedEncodingException{
	        StringBuilder result = new StringBuilder();
	 
	        for (Map.Entry<String, String> entry : params.entrySet()) {
	          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
	          result.append("=");
	          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	          result.append("&");
	        }
	 
	        String resultString = result.toString();
	        return resultString.length() > 0
	          ? resultString.substring(0, resultString.length() - 1)
	          : resultString;
	    }
	}
}
