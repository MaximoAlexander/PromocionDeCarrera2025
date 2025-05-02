package Efectos;

import Template.Canvas;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import static java.lang.Math.*;

public class RTX extends Canvas {

    int w = SIZE.width, h = SIZE.height;
    List<Integer> Ls = IntStream.rangeClosed(1, findGCD(w, h)).filter(i -> findGCD(w, h) % i == 0).boxed().toList();
    int L = Ls.getLast();
    private boolean end = false;

    List<Shape> obstacles = IntStream.range(0, w * h / (int) pow(L, 2)).filter(i -> i % 2 == 0 && i % 5 == 0).mapToObj(index -> {
        int i = index % (w / L);
        int j = index / (w / L);
        return regularPolygon(new Point2D.Double(i * L, j * L), L / 2.0, new Random().nextInt(3, 8));
    }).toList();

    private final int n = 100;
    List<Ray> rays = new ArrayList<>();
    private final Point2D mouse = new Point2D.Double(0, 0);

    public RTX(int fps) {
        super(fps);
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                mouse.setLocation(e.getPoint());
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                end = e.getKeyCode() == KeyEvent.VK_ESCAPE;
            }
        });
    }

    @Override
    public void painting(Graphics2D g2d) {
        g2d.setColor(new Color(255, 0, 255));

        Color[] colors = {
                new Color(0, 255, 0),
                new Color(0, 255, 255),
                new Color(255, 0, 255)
        };


        rays.clear();
        rays.addAll(IntStream.range(0, n).mapToObj(i -> new Ray(mouse, 2 * i * PI / n)).toList());
        rays.forEach(ray -> {
            ray.update(obstacles);
            g2d.setColor(new Color(255, 255, 255));
            g2d.draw(new Line2D.Double(ray.O, ray.H));
        });

        for (int i = 0; i < obstacles.size(); i++) {
            g2d.setColor(colors[i % colors.length]);
            g2d.fill(obstacles.get(i));
        }
    }

    @Override
    public void update() {
    }

    @Override
    public boolean isFinished() {
        return end;
    }

    // Origen, radio, lados
    private Shape regularPolygon(Point2D O, double r, int n) {
        return new Polygon(IntStream.range(0, n).map(i -> (int) (O.getX() + r * (1 - cos(2 * i * PI / n)))).toArray(),
                IntStream.range(0, n).map(i -> (int) (O.getY() + r * (1 - sin(2 * i * PI / n)))).toArray(), n);
    }

    private static int findGCD(int a, int b) {
        return b == 0 ? a : findGCD(b, a % b);
    }

    static class Ray {

        private final Point2D O;
        private final Point2D H;
        private final double θ;

        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        private final Rectangle2D screen = new Rectangle2D.Double(0, 0, s.width, s.height);

        public Ray(Point2D O, double θ) {
            this.O = O;
            this.H = new Point2D.Double(O.getX(), O.getY());
            this.θ = θ;
        }

        public void update(List<Shape> obj) {
            while (obj.stream().noneMatch(i -> i.contains(H)) && screen.contains(H))  H.setLocation(H.getX() + cos(θ), H.getY() + sin(θ));
        }
    }

}
