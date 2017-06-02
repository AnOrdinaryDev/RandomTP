package com.gmail.theposhogamer;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
	
	public static String calculateTime(long seconds) {
		seconds = seconds+1;
	    int day = (int) TimeUnit.SECONDS.toDays(seconds);
	    long hours = TimeUnit.SECONDS.toHours(seconds) -
	                 TimeUnit.DAYS.toHours(day);
	    long minute = TimeUnit.SECONDS.toMinutes(seconds) - 
	                  TimeUnit.DAYS.toMinutes(day) -
	                  TimeUnit.HOURS.toMinutes(hours);
	    long second = TimeUnit.SECONDS.toSeconds(seconds) -
	                  TimeUnit.DAYS.toSeconds(day) -
	                  TimeUnit.HOURS.toSeconds(hours) - 
	                  TimeUnit.MINUTES.toSeconds(minute);
	    return day +"d " + hours + "h " + minute + "m " + second + "s";
	}
	
}
