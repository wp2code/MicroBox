package com.wb2code.microbox.meta.panel.core;

import com.wb2code.microbox.meta.MicroToolFrame;
import com.wb2code.microbox.meta.panel.BasePanel;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author lwp
 * @date 2022-10-13
 */
@Deprecated
@Getter
@Setter
public class HidePanel extends BasePanel {

    private final JLabel text;

    /**
     *
     */
    public HidePanel(MicroToolFrame frame, Dimension dimension) {
        super();
        this.setPreferredSize(dimension);
        this.setMaximumSize(dimension);
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder(""));
        text = new JLabel("<html>隐<br>藏<br>日<br>志</html>", JLabel.CENTER);
        this.add(text, BorderLayout.CENTER);
        this.setVisible(true);
        HidePanel that = this;
        this.setBackground(Color.LIGHT_GRAY);
        this.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                final ContentPanel contentPanel = frame.getContentPanel();
                final boolean isVisible = contentPanel.isVisible();
                that.setTestDesc(isVisible);
                frame.getContentPanel().setVisible(!isVisible);
            }

            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Color blue = new Color(203, 200, 200);
                that.setBackground(blue);
            }

            /**
             * {@inheritDoc}
             *
             * @param e
             * @since 1.6
             */
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseMoved(e);
                that.setBackground(Color.LIGHT_GRAY);
            }
        });
    }

    /**
     * @param isVisible
     */
    public void setTestDesc(boolean isVisible) {
        this.text.setText(isVisible ? "<html>显<br>示<br>日<br>志</html>" : "<html>隐<br>藏<br>日<br>志</html>");
    }
}
