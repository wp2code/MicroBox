package com.wb2code.microbox.meta.label;

import cn.hutool.core.util.StrUtil;
import com.wb2code.microbox.utils.SystemUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author lwp
 * @date 2022-07-28
 */
@Getter
public class LinkLabel extends JLabel {

    private Object data;

    /**
     * @param text
     * @param consumer
     */
    public LinkLabel(String text, Color color, String iconName, Object data, Consumer<LinkLabel> consumer) {
//        super(text);
        this.setToolTipText(text);
        this.data = data;
        this.setForeground(color);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setFont(new Font("Helvetica", Font.PLAIN, 12));
        if (StrUtil.isNotBlank(iconName)) {
            this.setIcon(new ImageIcon(Objects.requireNonNull(SystemUtil.getSystemResource(String.format("images/%s.png", iconName)))));
        }
        LinkLabel that = this;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    if (consumer != null) {
                        consumer.accept(that);
                    }
                }
            }
        });
    }

    public void setIconName(String iconName) {
        this.setIcon(new ImageIcon(Objects.requireNonNull(SystemUtil.getSystemResource(String.format("images/%s.png", iconName)))));
        this.updateUI();
    }
}
