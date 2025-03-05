package com.wb2code.microbox.metadata;

import cn.hutool.core.thread.ThreadUtil;
import lombok.Data;

import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * @author lwp
 * @date 2025-03-15
 **/
@Data
public class LogPrintHeartbeat {
    
    
    private PipedReader reader;
    
    private PipedWriter writer;
    
    private volatile boolean isRunning = true;
    
    public LogPrintHeartbeat(PipedWriter writer) {
        this.writer = writer;
    }
    
    /**
     *
     */
    public void startHeartbeat() {
        Thread writerThread = new Thread(() -> {
            while (isRunning) {
                try {
                    ThreadUtil.safeSleep(5000);
                    String message = "HEARTBEAT-\n";
                    writer.write(message);
                } catch (Exception e) {
                    System.err.println("Writer error: " + e.getMessage());
                }
            }
        });
        writerThread.start();
    }
    
    
}
