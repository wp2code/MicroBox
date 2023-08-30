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
        Scanner scanner = new Scanner(reader);
        while (!stopMark && scanner.hasNext()) {
//            SwingUtilities.invokeLater(() -> {
            String line = scanner.nextLine();
            if (StrUtil.startWith(line, "MICROLOG")) {
                continue;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            textArea.append(line);
            textArea.append("\n");
            line = null;
            //使垂直滚动条自动向下滚动
//            scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
//            });
        }
    }
}
