package com.wb2code.microbox.meta.panel.core;

import cn.hutool.log.StaticLog;
import com.wb2code.microbox.annotation.entity.ServerConfigEntity;
import com.wb2code.microbox.config.CommonConstants;
import com.wb2code.microbox.meta.LogJTextArea;
import com.wb2code.microbox.meta.MicroToolFrame;
import com.wb2code.microbox.meta.panel.BasePanel;
import com.wb2code.microbox.metadata.LogAppender;
import com.wb2code.microbox.metadata.TextAreaLogAppender;
import javax.swing.*;

import java.awt.*;
import java.io.IOException;

/**
 * @author lwp
 * @date 2022-10-12
 */
public class ContentPanel extends BasePanel {
    
    private volatile LogJTextArea logTextArea;
    
    private LogAppender logAppender;
    
    public ContentPanel(MicroToolFrame frame, Dimension dimension) throws IOException {
        super();
        this.setLayout(new BorderLayout());
        this.setPreferredSize(dimension);
        this.setBorder(BorderFactory.createTitledBorder("日志"));
        this.addLogJTextAreaIfAbsent(CommonConstants.COMM_LOG_TEXT_AREA);
        this.setVisible(true);
    }
    
    /**
     * @param textArea
     * @throws IOException
     */
    public synchronized void addLogJTextAreaIfAbsent(String textArea) throws IOException {
        if (this.logTextArea == null) {
            this.logTextArea = new LogJTextArea(textArea);
            this.logTextArea.setEditable(false);
            JScrollPane logScrollPane = new JScrollPane();
            logScrollPane.setViewportView(logTextArea);
            logAppender = new TextAreaLogAppender(logTextArea, logScrollPane);
            logAppender.start();
            this.add(logScrollPane);
        }
    }
    
    /**
     * 打印日志
     *
     * @param log
     */
    public void printLog(ServerConfigEntity serverConfig, String log) {
        StaticLog.info("【{}】{}", serverConfig.getServerName(), log);
    }
    
    /**
     *
     */
    public void stopPrintLog() {
        logAppender.stop(true);
    }
    
    public void clearLog() {
    
    }
    
    public void start() {
        if (logAppender != null ) {
            logAppender.start();
        }
    }
    
}
