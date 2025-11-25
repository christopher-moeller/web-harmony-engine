package com.webharmony.core.utils.log;

public class StopWatch {
    private long startTime;
    private long stopTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }
    public void stop() {
        stopTime = System.currentTimeMillis();
    }

    public long getDurationMillis() {
        return stopTime - startTime;
    }

    public String getDurationAsReadableString() {

        long milliseconds = getDurationMillis();

        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        StringBuilder readableTime = new StringBuilder();

        if (hours > 0) {
            readableTime.append(hours).append(hours == 1 ? " hour " : " hours ");
        }
        if (minutes > 0) {
            readableTime.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
        }
        if (seconds > 0 || milliseconds < 1000) {
            readableTime.append(seconds).append(seconds == 1 ? " second" : " seconds");
        }

        return readableTime.toString().trim();
    }
}
