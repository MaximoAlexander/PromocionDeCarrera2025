package Efectos;

import Template.Canvas;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class BirdFlock extends Canvas {

    private final int NUM_BIRDS = 100;
    private final ArrayList<Bird> birds = new ArrayList<>();
    private final Random random = new Random();
    private boolean finished = false;

    public BirdFlock(int fps) {
        super(fps);

        generateFlock();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                finished = e.getKeyChar() == KeyEvent.VK_ESCAPE;
                if (e.getKeyCode() == KeyEvent.VK_R) generateFlock();
            }
        });
    }

    private void generateFlock() {
        birds.clear();
        for (int i = 0; i < NUM_BIRDS; i++) {
            birds.add(new Bird(
                    random.nextInt(SIZE.width),
                    random.nextInt(SIZE.height),
                    random.nextDouble() * 2 * Math.PI
            ));
        }
    }

    @Override
    public void update() {
        for (Bird bird : birds) {
            bird.alignWithNeighbors();
            bird.move();
            bird.bounce();
        }
    }

    @Override
    public void painting(Graphics2D g2d) {
        birds.forEach(i -> i.draw(g2d));
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    class Bird {
        double x, y;
        double angle;
        final double speed = 2.0;
        final double turnRate = 0.05;
        final int radius = 16;
        final double perception = 50.0;

        public Bird(double x, double y, double angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }

        public void move() {
            x += Math.cos(angle) * speed;
            y += Math.sin(angle) * speed;
        }

        public void bounce() {
            if (x < 0 || x > SIZE.width) {
                angle = Math.PI - angle;
            }
            if (y < 0 || y > SIZE.height) {
                angle = -angle;
            }
        }

        public void alignWithNeighbors() {
            double avgAngle = 0;
            int count = 0;

            for (Bird other : birds) {
                if (other == this) continue;
                double dx = other.x - this.x;
                double dy = other.y - this.y;
                if (dx * dx + dy * dy < perception * perception) {
                    avgAngle += Math.atan2(Math.sin(other.angle), Math.cos(other.angle));
                    count++;
                }
            }

            if (count > 0) {
                avgAngle /= count;
                double diff = normalizeAngle(avgAngle - this.angle);
                angle += clamp(diff, -turnRate, turnRate);
            }
        }

        public void draw(Graphics2D g2d) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));

            int r = 10;
            g2d.fill(new Polygon(IntStream.range(0, 3).map(i -> (int) (x + r * Math.cos(2 * i * Math.PI / 3 + angle))).toArray(),
                    IntStream.range(0, 3).map(i -> (int) (y - r * Math.sin(2 * i * Math.PI / 3 + angle))).toArray(), 3));

            g2d.setColor(Color.RED);
            g2d.draw(new Line2D.Double(x, y, x + (r + 2) * Math.cos(angle), y + (r + 2) * Math.sin(angle)));
        }

        private double normalizeAngle(double a) {
            while (a < -Math.PI) a += 2 * Math.PI;
            while (a > Math.PI) a -= 2 * Math.PI;
            return a;
        }

        private double clamp(double val, double min, double max) {
            return Math.max(min, Math.min(max, val));
        }
    }
}
