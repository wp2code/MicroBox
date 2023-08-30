package com.wb2code.microbox.meta.panel;

import com.wb2code.microbox.meta.layout.VerticalFlowLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author liu_wp
 * @date 2020/11/3
 * @see
 */
public class ComPanel extends JPanel {
    public ComPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    public ComPanel(LayoutManager mgr) {
        setLayout(mgr);
    }

    public ComPanel(int axis) {
        setLayout(new BoxLayout(this, axis));
    }

    private BufferedImage image;

    public ComPanel(LayoutManager layoutManager, JComponent... jComponent) {
        super(layoutManager);
        for (int i = 0; i < jComponent.length; i++) {
            add(jComponent[i], i);
        }
    }

    public ComPanel(JComponent... jComponent) {
        for (int i = 0; i < jComponent.length; i++) {
            add(jComponent[i], i);
        }
    }

    public ComPanel(VerticalFlowLayout verticalFlowLayout, JComponent... jComponent) {
        super(verticalFlowLayout);
        for (int i = 0; i < jComponent.length; i++) {
            add(jComponent[i], i);
        }
    }

    public ComPanel(int align, JComponent... jComponent) {
        for (int i = 0; i < jComponent.length; i++) {
            add(jComponent[i], i);
        }
        this.setLayout(new FlowLayout(align));
    }

    @Override

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }
}
