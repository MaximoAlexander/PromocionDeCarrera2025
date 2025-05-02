package Efectos;


import Template.Canvas;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.awt.event.KeyAdapter;

public class Clock extends Canvas {

    private boolean finished = false;

    public Clock(int fps) {
        super(fps);
    }

    @Override
    public void update() {}

    @Override
    public void painting(Graphics2D g) {
        int centerX = SIZE.width / 2;
        int centerY = SIZE.height / 2;
        int radius = Math.min(centerX, centerY) - 100;

        g.setColor(Color.white);
        g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        for (int i = 0; i < 60; i++) {
            double angle = Math.toRadians(i * 6);
            int inner = radius - 10;
            int outer = (i % 5 == 0) ? radius - 20 : radius - 15;

            int x1 = (int)(centerX + Math.sin(angle) * outer);
            int y1 = (int)(centerY - Math.cos(angle) * outer);
            int x2 = (int)(centerX + Math.sin(angle) * inner);
            int y2 = (int)(centerY - Math.cos(angle) * inner);

            g.drawLine(x1, y1, x2, y2);
        }

        LocalTime time = LocalTime.now();
        int seconds = time.getSecond();
        int minutes = time.getMinute();
        int hours = time.getHour() % 12;
        drawHand(g, centerX, centerY, radius - 30, seconds * 6, Color.red, 2);
        drawHand(g, centerX, centerY, radius - 60, minutes * 6 + seconds / 10.0, Color.white, 4);
        drawHand(g, centerX, centerY, radius - 90, hours * 30 + minutes / 2.0, Color.white, 6);

        g.setColor(Color.white);
        g.fillOval(centerX - 5, centerY - 5, 10, 10);
    }

    private void drawHand(Graphics2D g, int cx, int cy, int length, double angleDeg, Color color, int thickness) {
        double angleRad = Math.toRadians(angleDeg);

        int x = (int)(cx + Math.sin(angleRad) * length);
        int y = (int)(cy - Math.cos(angleRad) * length);

        g.setColor(color);
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(thickness));
        g.drawLine(cx, cy, x, y);
        g.setStroke(oldStroke);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    public void stopClock() {
        finished = true;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stopClock();
                }
            }
        });
    }
}

