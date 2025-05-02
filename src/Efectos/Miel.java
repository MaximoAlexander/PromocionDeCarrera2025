package Efectos;

import Template.Canvas;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

public class Miel extends Canvas {

    double x, y, vx, vy;
    boolean l, r, u, end;
    final int rds = 20;
    ArrayList<Shape> obstáculos = new ArrayList<>();

    public Miel(int fps) {
        super(fps);
        x = getPreferredSize().width / 2.0;
        y = getPreferredSize().height / 2.0;

        Random rand = new Random();
        int w = getPreferredSize().width;
        int h = getPreferredSize().height;

        obstáculos.add(new Ellipse2D.Double(rand.nextInt(w - 200), rand.nextInt(h - 100), 200, 100));

        obstáculos.add(new Arc2D.Double(rand.nextInt(w - 150), rand.nextInt(h - 150), 150, 150, 30, 300, Arc2D.CHORD));

        int hx = rand.nextInt(w - 200), hy = rand.nextInt(h - 200);
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) 
            hex.addPoint((int) (hx + 80 * Math.cos(i * Math.PI / 3)), (int) (hy + 80 * Math.sin(i * Math.PI / 3)));
        obstáculos.add(hex);

        int px = rand.nextInt(w - 200), py = rand.nextInt(h - 200);
        Polygon pent = new Polygon();
        for (int i = 0; i < 5; i++) 
            pent.addPoint((int) (px + 80 * Math.cos(i * 2 * Math.PI / 5)), (int) (py + 80 * Math.sin(i * 2 * Math.PI / 5)));

        obstáculos.add(pent);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    l = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    r = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    u = true;
                }
                end = e.getKeyCode() == KeyEvent.VK_ESCAPE;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    l = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    r = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    u = false;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                double dx = e.getX() - x, dy = e.getY() - y, d = Math.hypot(dx, dy);
                if (d > 0) {
                    vx = dx / d * 10;
                    vy = dy / d * 10;
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    @Override
    public void update() {
        if (l) {
            vx -= .5;
        }
        if (r) {
            vx += .5;
        }
        if (u) {
            vy -= 1.5;
        }
        vy += .5;
        x += vx;
        y += vy;

        Ellipse2D ball = new Ellipse2D.Double(x - rds, y - rds, rds * 2, rds * 2);

        for (Shape s : obstáculos) {
            Area a = new Area(s);
            a.intersect(new Area(ball));
            if (!a.isEmpty()) {
                vx *= -0.8;
                vy *= -0.8;
                x -= vx;
                y -= vy;
            }
        }

        int w = getWidth(), h = getHeight();
        if (x < rds) {
            x = rds;
            vx *= -.8;
        }
        if (x > w - rds) {
            x = w - rds;
            vx *= -.8;
        }
        if (y < rds) {
            y = rds;
            vy *= -.8;
        }
        if (y > h - rds) {
            y = h - rds;
            vy *= -.8;
            if (Math.abs(vy) < 1) {
                vy = 0;
            }
        }
    }

    @Override
    public boolean isFinished() {
        return end;
    }

    @Override
    public void painting(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.setStroke(new BasicStroke(2));
        g.fill(new Ellipse2D.Double(x - rds, y - rds, rds * 2, rds * 2));
        for (Shape s : obstáculos) {
            g.setColor(new Color(255, 140, 0, 128));
            g.fill(s);
            g.setColor(Color.YELLOW);
            g.draw(s);
        }
    }
}
