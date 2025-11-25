package com.webharmony.core.utils.log;

import java.util.ArrayList;
import java.util.List;

public class LogWatcher {

    private static final int CAPACITY = 1000;

    private static LogWatcher instance = null;

    private final String[] ringBuffer = new String[CAPACITY];

    private int writeIndex = 0;
    private LogWatcher() {

    }

    public static LogWatcher getInstance() {
        if(instance == null)
            instance = new LogWatcher();

        return instance;
    }

    public void start() {
        JavaLogInterceptorHolder.getInstance().addInterceptor("log-watcher", this::writeLogToBuffer);
    }

    private synchronized void writeLogToBuffer(String logLine) {
        this.ringBuffer[this.writeIndex] = cleanLine(logLine);
        writeIndex++;
        if(writeIndex >= this.ringBuffer.length) {
            writeIndex = 0;
        }
    }

    private String cleanLine(String logLine) {
        if(logLine.charAt(logLine.length() - 1) == '\n')
            return logLine.substring(0, logLine.length() - 1);
        else
            return logLine;
    }

    public List<String> getLogs() {
        int readIndex = writeIndex - this.ringBuffer.length;
        if(readIndex < 0) {
            readIndex = this.ringBuffer.length + readIndex;
        }

        final List<String> result = new ArrayList<>();
        for(int i=0; i<this.ringBuffer.length; i++) {
            if(readIndex >= ringBuffer.length) {
                readIndex = 0;
            }

            String value = this.ringBuffer[readIndex];
            if(value != null)
                result.add(value);

            readIndex ++;
        }

        return result;
    }

    public List<String> getLastLogEntries(int maxAmountOfEntries) {
        final List<String> allEntries = getLogs();
        if(maxAmountOfEntries >= allEntries.size()) {
            return allEntries;
        } else {
            int starIndex = allEntries.size() - maxAmountOfEntries - 1;
            final List<String> resultList = new ArrayList<>();
            for(int i=starIndex; i<allEntries.size(); i++) {
                resultList.add(allEntries.get(i));
            }
            return resultList;
        }
    }


}
