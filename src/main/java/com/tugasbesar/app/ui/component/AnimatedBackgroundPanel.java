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
import java.awt.geom.AffineTransform;

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

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.24f));
        drawSkateTracks(g2, width, height);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.34f));
        drawSkateWheels(g2, width, height);

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

    private void drawSkateTracks(Graphics2D g2, int width, int height) {
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(56, 189, 248, 70));

        Path2D.Double firstTrack = new Path2D.Double();
        firstTrack.moveTo(width * 0.04, height * 0.74);
        firstTrack.curveTo(width * 0.22, height * 0.6, width * 0.44, height * 0.86, width * 0.68, height * 0.72);
        firstTrack.curveTo(width * 0.82, height * 0.64, width * 0.93, height * 0.8, width * 1.03, height * 0.7);
        g2.draw(firstTrack);

        g2.setColor(new Color(45, 212, 191, 62));
        Path2D.Double secondTrack = new Path2D.Double();
        secondTrack.moveTo(width * 0.0, height * 0.36);
        secondTrack.curveTo(width * 0.18, height * 0.48, width * 0.34, height * 0.22, width * 0.55, height * 0.34);
        secondTrack.curveTo(width * 0.74, height * 0.44, width * 0.9, height * 0.24, width * 1.02, height * 0.34);
        g2.draw(secondTrack);
    }

    private void drawSkateWheels(Graphics2D g2, int width, int height) {
        paintWheel(g2, width * 0.14 + Math.sin(phase * 1.4f) * 10, height * 0.78 + Math.cos(phase * 1.1f) * 6, 54,
                new Color(34, 211, 238, 95));
        paintWheel(g2, width * 0.82 + Math.cos(phase * 1.15f) * 12, height * 0.2 + Math.sin(phase * 1.4f) * 8, 48,
                new Color(167, 139, 250, 92));
        paintWheel(g2, width * 0.9 + Math.sin(phase * 0.85f) * 9, height * 0.74 + Math.cos(phase * 1.2f) * 6, 36,
                new Color(103, 232, 249, 86));
    }

    private void paintWheel(Graphics2D g2, double centerX, double centerY, int size, Color color) {
        Graphics2D wheel = (Graphics2D) g2.create();
        wheel.translate(centerX, centerY);
        wheel.rotate(phase * 2.6f);

        wheel.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(130, color.getAlpha() + 30)));
        wheel.fill(new Ellipse2D.Double(-size / 2.0, -size / 2.0, size, size));

        wheel.setColor(new Color(15, 23, 42, 170));
        wheel.fill(new Ellipse2D.Double(-size / 4.0, -size / 4.0, size / 2.0, size / 2.0));

        wheel.setStroke(new BasicStroke(2.1f));
        wheel.setColor(new Color(224, 242, 254, 170));
        for (int i = 0; i < 5; i++) {
            AffineTransform original = wheel.getTransform();
            wheel.rotate((Math.PI * 2 / 5) * i);
            wheel.drawLine(0, 0, size / 2 - 4, 0);
            wheel.setTransform(original);
        }
        wheel.dispose();
    }
}
