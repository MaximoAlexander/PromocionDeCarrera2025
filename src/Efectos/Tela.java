package Efectos;

import Template.Canvas;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Tela extends Canvas {

    private static final float GRAVITY = 0.3f;
    private static final float SPRING_CONSTANT = 0.098f;
    private static final float DAMPING = 0.9f;
    private static final float PARTICLE_MASS = 1.0f;
    private static final float SPRING_LENGTH = 40f;

    private final List<Particle> particles = new ArrayList<>();
    private final List<Spring> springs = new ArrayList<>();
    private Particle selectedParticle = null;
    private int offsetX, offsetY;

    private final int cols = 10;
    private final int rows = 10;

    boolean end = false;

    public Tela(int fps) {
        super(fps);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                end = e.getKeyCode() == KeyEvent.VK_ESCAPE;
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Particle p : particles) {
                    if (Math.abs(p.x - e.getX()) < 10 && Math.abs(p.y - e.getY()) < 10) {
                        selectedParticle = p;
                        offsetX = e.getX() - (int) p.x;
                        offsetY = e.getY() - (int) p.y;
                        return;
                    }
                }
                if(e.getButton() == MouseEvent.BUTTON3) CreateCloth();
            }


            @Override
            public void mouseReleased(MouseEvent e) {
                selectedParticle = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedParticle != null) {
                    selectedParticle.x = e.getX() - offsetX;
                    selectedParticle.y = e.getY() - offsetY;
                    selectedParticle.vx = 0;
                    selectedParticle.vy = 0;
                }
            }
        });

        CreateCloth();
    }

    private void CreateCloth() {
        particles.clear();
        springs.clear();

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                Particle p = new Particle(300 + i * SPRING_LENGTH, 200 + j * SPRING_LENGTH, this);
                particles.add(p);

                if (i > 0) {
                    springs.add(new Spring(particles.get((i - 1) * rows + j), p));
                }
                if (j > 0) {
                    springs.add(new Spring(particles.get(i * rows + (j - 1)), p));
                }
            }
        }
    }

    @Override
    public void update() {
        for (Particle p : particles) {
            p.applyForce(0, GRAVITY);
        }

        for (Particle p : particles) {
            p.update();
            p.applyDamping();
        }

        for (Spring s : springs) {
            s.update();
        }
    }

    @Override
    public boolean isFinished() {
        return end;
    }

    @Override
    public void painting(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color[] colors = {
                new Color(0, 255, 0),
                new Color(0, 255, 255),
                new Color(255, 0, 255)
        };

        for (int i = 0; i < cols - 1; i++) {
            for (int j = 0; j < rows - 1; j++) {
                int index = (i + j) % colors.length;
                Color edgeColor = colors[index];
                Color fillColor = new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 100);

                Particle p1 = particles.get(i * rows + j);
                Particle p2 = particles.get((i + 1) * rows + j);
                Particle p3 = particles.get((i + 1) * rows + (j + 1));
                Particle p4 = particles.get(i * rows + (j + 1));

                int[] xPoints = {(int) p1.x, (int) p2.x, (int) p3.x, (int) p4.x};
                int[] yPoints = {(int) p1.y, (int) p2.y, (int) p3.y, (int) p4.y};

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2d.setColor(fillColor);
                g2d.fillPolygon(xPoints, yPoints, 4);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setColor(edgeColor);
                g2d.drawPolygon(xPoints, yPoints, 4);
            }
        }

        g2d.setColor(Color.WHITE);
        for (Particle p : particles) {
            g2d.fillOval((int) p.x - 2, (int) p.y - 2, 4, 4);
        }
    }


    static class Particle {
        float x, y, vx = 0, vy = 0, fx = 0, fy = 0;
        final float radius = 4;
        Tela canvas;

        Particle(float x, float y, Tela canvas) {
            this.x = x;
            this.y = y;
            this.canvas = canvas;
        }

        void applyForce(float fx, float fy) {
            this.fx += fx;
            this.fy += fy;
        }

        void update() {
            vx += fx / PARTICLE_MASS;
            vy += fy / PARTICLE_MASS;
            x += vx;
            y += vy;
            fx = 0;
            fy = 0;

            float bounce = -0.7f;
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            if (x < radius) {
                x = radius;
                vx *= bounce;
            }
            if (x > width - radius) {
                x = width - radius;
                vx *= bounce;
            }
            if (y < radius) {
                y = radius;
                vy *= bounce;
            }
            if (y > height - radius) {
                y = height - radius;
                vy *= bounce;
            }
        }

        void applyDamping() {
            vx *= Tela.DAMPING;
            vy *= Tela.DAMPING;
        }
    }

    static class Spring {
        Particle p1, p2;
        float restLength = SPRING_LENGTH;
        float k = SPRING_CONSTANT;

        Spring(Particle p1, Particle p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        void update() {
            float dx = p2.x - p1.x;
            float dy = p2.y - p1.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist == 0) return;

            float force = (dist - restLength) * k;
            float fx = force * dx / dist;
            float fy = force * dy / dist;

            p1.applyForce(fx, fy);
            p2.applyForce(-fx, -fy);
        }
    }
}
