package com.wb2code.microbox.meta;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author lwp
 * @date 2022-10-19
 */
public class LogJTextArea extends JTextArea implements MouseListener {
    private JPopupMenu pop;
    private JMenuItem cut;
    @Getter
    private String idName;

    public LogJTextArea(String idName) {
        init(idName);
    }

    private void init(String idName) {
        this.idName = idName;
        this.addMouseListener(this);

        pop = new JPopupMenu();
        pop.add(cut = new JMenuItem("清空"));
        cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
        cut.addActionListener(e -> {
            clear();
        });

        this.add(pop);

    }

    public void clear() {
        this.setText(null);
        this.updateUI();
    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            cut.setEnabled(true);
            pop.show(this, e.getX(), e.getY());
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }
}
