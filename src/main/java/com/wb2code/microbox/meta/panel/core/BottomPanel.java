package com.wb2code.microbox.meta.panel.core;

import com.wb2code.microbox.meta.MicroToolFrame;
import com.wb2code.microbox.meta.panel.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author lwp
 * @date 2022-10-12
 */
public class BottomPanel extends BasePanel {
    public BottomPanel(MicroToolFrame frame, Dimension dimension) {
        super();
        this.setPreferredSize(dimension);
        this.setMaximumSize(dimension);
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        final JLabel version = new JLabel("版本信息：V1");
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 11);
        version.setFont(font);
        this.add(version);
        this.setVisible(true);
    }
}
