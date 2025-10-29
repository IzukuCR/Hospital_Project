package front.presentation;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Highlighter extends MouseAdapter implements MouseListener{
    private Color color;
    private Color original;

    public Highlighter(Color color) {
        this.color = color;
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
        Component source = (Component ) evt.getSource();
        original = source.getBackground();
        source.setBackground(color);
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        Component source = (Component ) evt.getSource();
        source.setBackground(original);
    }
}
