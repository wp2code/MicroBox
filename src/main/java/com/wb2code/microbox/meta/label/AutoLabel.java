package com.wb2code.microbox.meta.label;

import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author lwp
 * @date 2022-10-17
 */
public class AutoLabel extends JLabel {
    public AutoLabel(String text, String color, int width, int height) {
        super();
        this.setSize(new Dimension(width, height));
        this.setLabelText(this, text, color);
    }

    public void setLabelText(JLabel jLabel, String longString, String color) {
        StringBuilder builder = new StringBuilder("<html>");
        if (StrUtil.isNotBlank(color)) {
            builder.append("<span style=\"color:" + color + ";\">");
        }
        char[] chars = longString.toCharArray();
        FontMetrics fontMetrics = jLabel.getFontMetrics(jLabel.getFont());
        int start = 0;
        int len = 0;
        final int width = jLabel.getWidth();
        while (start + len < longString.length()) {
            while (true) {
                len++;
                if (start + len > longString.length()) break;
                if (fontMetrics.charsWidth(chars, start, len)
                        > width) {
                    break;
                }
            }
            builder.append(chars, start, len - 1).append("<br/>");
            start = start + len - 1;
            len = 0;
        }
        builder.append(chars, start, longString.length() - start);
        if (StrUtil.isNotBlank(color)) {
            builder.append("</span>");
        }
        builder.append("</html>");
        jLabel.setText(builder.toString());
    }
}
