package com.wb2code.microbox.metadata;

import cn.hutool.core.util.StrUtil;
import com.wb2code.microbox.meta.LogJTextArea;
import javax.swing.*;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author lwp
 * @date 2022-10-14
 */
public class TextAreaLogAppender extends LogAppender {
    
    private LogJTextArea textArea;
    
    private JScrollPane scroll;
    
    public TextAreaLogAppender(LogJTextArea textArea, JScrollPane scroll) throws IOException {
        super(textArea.getIdName());
        this.textArea = textArea;
        this.scroll = scroll;
    }
    
    @Override
    public void run() {
        Scanner scanner = null;
        while (true) {
            try {
                if (scanner == null) {
                    scanner = new Scanner(reader);
                }
                //            SwingUtilities.invokeLater(() -> {
                if (!scanner.hasNext()) {
                    continue;
                }
                String line = scanner.nextLine();
                if (StrUtil.startWith(line, "MICROLOG") || StrUtil.contains(line, "HEARTBEAT")) {
                    continue;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                textArea.append(line);
                textArea.append("\n");
                //使垂直滚动条自动向下滚动
                //            scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
                //            });
            } catch (Exception e) {
                System.err.println("Reader error: " + e.getMessage());
            }
        }
    }
}
