package com.tugasbesar.app.ui.component;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Point2D;

public class AnimatedBackgroundPanel extends JPanel {
    private float phase;

    public AnimatedBackgroundPanel() {
        setOpaque(true);

        Timer timer = new Timer(40, event -> {
            phase += 0.0125f;
            repaint();
        });
        timer.start();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(960, 640);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        GradientPaint backgroundPaint = new GradientPaint(
                0, 0, new Color(8, 47, 73),
                width, height, new Color(18, 63, 82)
        );
        g2.setPaint(backgroundPaint);
        g2.fillRect(0, 0, width, height);

        drawGlow(g2, width * 0.12 + Math.sin(phase * 0.8f) * 30, height * 0.2 + Math.cos(phase) * 20,
                360f, new Color(56, 189, 248, 120), new Color(56, 189, 248, 0));
        drawGlow(g2, width * 0.88 + Math.cos(phase * 0.95f) * 36, height * 0.18 + Math.sin(phase * 1.2f) * 18,
                320f, new Color(45, 212, 191, 95), new Color(45, 212, 191, 0));
        drawGlow(g2, width * 0.78 + Math.sin(phase * 0.75f) * 28, height * 0.78 + Math.cos(phase) * 24,
                420f, new Color(110, 231, 183, 80), new Color(110, 231, 183, 0));
        drawGlow(g2, width * 0.26 + Math.cos(phase * 1.3f) * 22, height * 0.82 + Math.sin(phase * 1.15f) * 16,
                280f, new Color(125, 211, 252, 72), new Color(125, 211, 252, 0));

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
        for (int i = 0; i < 4; i++) {
            double offset = phase * (8 + i);
            double ribbonY = height * (0.16 + (i * 0.18)) + Math.sin(offset) * 12;
            drawRibbon(g2, width, height, ribbonY, 48 + (i * 8), i);
        }

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        g2.setColor(new Color(255, 255, 255, 150));
        g2.setStroke(new BasicStroke(1.2f));
        drawGridLines(g2, width, height);

        g2.dispose();
    }

    private void drawGlow(Graphics2D g2, double centerX, double centerY, float radius, Color innerColor, Color outerColor) {
        RadialGradientPaint paint = new RadialGradientPaint(
                new Point2D.Double(centerX, centerY),
                radius,
                new float[]{0f, 1f},
                new Color[]{innerColor, outerColor}
        );
        g2.setPaint(paint);
        g2.fill(new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2, radius * 2));
    }

    private void drawRibbon(Graphics2D g2, int width, int height, double baseY, double thickness, int index) {
        Path2D.Double path = new Path2D.Double();
        double startX = -width * 0.1;
        double endX = width * 1.05;
        double wave = 26 + (index * 8);

        path.moveTo(startX, baseY);
        for (double x = startX; x <= endX; x += 24) {
            double y = baseY + Math.sin((x / width * 4.6) + phase + index) * wave;
            path.lineTo(x, y);
        }
        for (double x = endX; x >= startX; x -= 24) {
            double y = baseY + thickness + Math.sin((x / width * 4.6) + phase + index + 0.75) * wave;
            path.lineTo(x, y);
        }
        path.closePath();

        GradientPaint ribbonPaint = new GradientPaint(
                0, 0, new Color(255, 255, 255, 120),
                width, height, new Color(103, 232, 249, 36)
        );
        g2.setPaint(ribbonPaint);
        g2.fill(path);
    }

    private void drawGridLines(Graphics2D g2, int width, int height) {
        int step = 120;
        for (int x = step; x < width; x += step) {
            g2.drawLine(x, 0, x, height);
        }
        for (int y = step; y < height; y += step) {
            g2.drawLine(0, y, width, y);
        }

        g2.setColor(new Color(255, 255, 255, 70));
        g2.fill(new RoundRectangle2D.Double(width * 0.08, height * 0.1, width * 0.18, 8, 8, 8));
        g2.fill(new RoundRectangle2D.Double(width * 0.68, height * 0.82, width * 0.14, 8, 8, 8));
    }
}
