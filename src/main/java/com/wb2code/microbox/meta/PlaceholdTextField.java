package com.wb2code.microbox.meta;

import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @author lwp
 * @date 2022-11-16
 */
public class PlaceholdTextField extends JTextField {
    private String placeholdTextStr;
    private boolean defaultFlag;

    public PlaceholdTextField(String placeholdText, String text) {
        super(StrUtil.isNotBlank(text) ? text : null);
        if (StrUtil.isNotBlank(placeholdText)) {
            placeholdTextStr = placeholdText;
            if (StrUtil.isBlank(text)) {
                init();
            }
            PlaceholdTextField that = this;
            this.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (StrUtil.isBlank(that.getText()) || placeholdTextStr.equals(that.getText())) {
                        that.setText("");
                        that.setForeground(Color.BLACK);
                        that.defaultFlag = false;
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if ("".equals(that.getText())) {
                        that.setForeground(Color.GRAY);
                        that.defaultFlag = true;
                        that.setText(placeholdTextStr);
                    }
                }
            });
        }
    }

    public void init() {
        this.setText(placeholdTextStr);
        this.setForeground(Color.GRAY);
        this.defaultFlag = true;
    }

    @Override
    public String getText() {
        final String text = super.getText();
        if (StrUtil.isNotBlank(placeholdTextStr) && defaultFlag) {
            return StrUtil.equals(text, placeholdTextStr) ? null : text;
        }
        return text;
    }
}
