package com.cosd.greenbuild.calwin.utils;

import CalWINClient.CalWINClient;

public class CalwinWSConfig {
	
	public CalWINClient CalwinWSInfo()
	{
		return new CalWINClient("usplscosd191", //"ustlscosd300",
	            "1525", //"1526",
	            "CISPROD", //"clwncisp2",
	            "calwin_cis_iface",
	            "blahblahblah123", //"imed_abc_48785",
	            "integrationserver.production.calwin.org",
	            "7004",
	            "sdg01",
	            "be3che89",
	            "1234567890AB",
	            "3C",
	            "37");
	}

}
