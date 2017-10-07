package com.gmail.theposhogamer.Util;

public class Timer {
    public long startTime;
    public long endTime;

    public long getDuration() {
        return endTime - startTime;
    }
    public void start() {
        startTime = System.currentTimeMillis();
    }
    public void stop() {
         endTime = System.currentTimeMillis();
    }
}
