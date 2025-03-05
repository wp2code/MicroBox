package com.wb2code.microbox.metadata;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;

/**
 * @author lwp
 * @date 2022-10-14
 */
public abstract class LogAppender extends Thread {
    
    protected PipedReader reader;
    
    protected PipedWriter writer;
    
    protected volatile boolean stopMark;
    
    protected LogPrintHeartbeat logPrintHeartbeat;
    
    public LogAppender(String appenderName) throws IOException {
        reader = new PipedReader();
        writer = new PipedWriter(reader);
        this.setWriterAppender(writer, appenderName);
        logPrintHeartbeat = new LogPrintHeartbeat(writer);
        logPrintHeartbeat.startHeartbeat();
    }
    
    public void setWriterAppender(final Writer writer, final String writerName) {
        final LoggerContext context = LoggerContext.getContext(false);
        final Configuration config = context.getConfiguration();
        //        final PatternLayout layout = PatternLayout.newBuilder()
        //                .withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %level [%t] [%c] [%M] [%l] - %msg%n").build();
        final PatternLayout layout = PatternLayout.createDefaultLayout(config);
        WriterAppender writerAppender = WriterAppender.newBuilder().setName(writerName).setTarget(writer).setLayout(layout).build();
        writerAppender.start();
        config.addAppender(writerAppender);
        config.getRootLogger().addAppender(writerAppender, null, null);
        context.updateLoggers(config);
    }
    
    @Override
    public void start() {
        this.stopMark = false;
        if (!this.isAlive()) {
            super.start();
        }
    }
    
    /**
     * @param stopMark
     */
    public void stop(boolean stopMark) {
        this.stopMark = stopMark;
    }
}
