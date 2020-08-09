package com.kk4vcz.codeplug;

import java.io.IOException;

/* This interface is used to query data sources,
 * such as RepeaterBook and RadioReference.
 * 
 * Generally, you first call setBand() to choose the band, then
 * call one of the query() functions to perform the actual call.
 */

public interface RadioAPI {
	// Perform a proximity query.
	Radio queryProximity(String loc, float distance, long band) throws IOException;
}
