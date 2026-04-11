package com.tugasbesar.app.ui.component;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RoundedButton extends JButton {
    private final Color backgroundColor;
    private final Color borderColor;
    private static final int ARC_SIZE = 0;

    public RoundedButton(String text, Color backgroundColor, Color foregroundColor, Color borderColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;

        setForeground(foregroundColor);
        setFont(new Font("SansSerif", Font.BOLD, 18));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        setPreferredSize(new Dimension(360, 56));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = getModel().isPressed() ? backgroundColor.darker()
                : getModel().isRollover() ? backgroundColor.brighter() : backgroundColor;

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_SIZE, ARC_SIZE);

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC_SIZE, ARC_SIZE);
        }

        g2.dispose();
        super.paintComponent(graphics);
    }
}
