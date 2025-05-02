package Efectos;

import Template.Canvas;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Koch extends Canvas {

    private List<LineSegment> segments;
    private int depth;
    private boolean end = false;

    public Koch(int fps) {
        super(fps);
        segments = new ArrayList<>();
        depth = 0;
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                end = e.getKeyCode() == KeyEvent.VK_ESCAPE;
            }
        });
    }

    public void generateKochFractal(int depth) {
        this.depth = depth;
        segments.clear();

        Point p1 = new Point((int) (SIZE.width / 2.0), (int) (SIZE.height / 2.0 - 300));
        Point p2 = new Point((int) (SIZE.width / 2 - 300 * Math.cos(11.0 * Math.PI / 6.0)), (int) (SIZE.height / 2 - 300 * Math.sin(11.0 * Math.PI / 6.0)));
        Point p3 = new Point((int) (SIZE.width / 2 - 300 * Math.cos(7.0 * Math.PI / 6.0)), (int) (SIZE.height / 2 - 300 * Math.sin(7.0 * Math.PI / 6.0)));

        segments.add(new LineSegment(p1, p2));
        segments.add(new LineSegment(p2, p3));
        segments.add(new LineSegment(p3, p1));

        for (int i = 0; i < depth; i++) {
            List<LineSegment> newSegments = new ArrayList<>();
            for (LineSegment segment : segments) {
                newSegments.addAll(segment.subdivide());
            }
            segments = newSegments;
        }
    }

    @Override
    public void painting(Graphics2D g2d) {
        g2d.setColor(new Color(255, 0, 255));
        g2d.setStroke(new BasicStroke(2));
        for (LineSegment segment : segments) {
            g2d.drawLine(segment.start.x, segment.start.y, segment.end.x, segment.end.y);
        }
    }

    @Override
    public void update() {
        generateKochFractal(depth);
        if (depth < 6) {
            depth++;
        }
    }

    @Override
    public boolean isFinished() {
        return end;
    }

    private static class LineSegment {
        Point start, end;

        LineSegment(Point start, Point end) {
            this.start = start;
            this.end = end;
        }

        public List<LineSegment> subdivide() {
            List<LineSegment> newSegments = new ArrayList<>();

            double angle = Math.atan2(end.y - start.y, end.x - start.x);
            double length = start.distance(end);

            Point p1 = new Point((int) (start.x + length / 3 * Math.cos(angle)),
                    (int) (start.y + length / 3 * Math.sin(angle)));

            Point p2 = new Point((int) (start.x + 2 * length / 3 * Math.cos(angle)),
                    (int) (start.y + 2 * length / 3 * Math.sin(angle)));


            double anglePlus60 = angle + Math.PI / 3;
            Point p3 = new Point((int) (p1.x + (length / 3) * Math.cos(anglePlus60)),
                    (int) (p1.y + (length / 3) * Math.sin(anglePlus60)));


            newSegments.add(new LineSegment(start, p1));
            newSegments.add(new LineSegment(p1, p3));
            newSegments.add(new LineSegment(p3, p2));
            newSegments.add(new LineSegment(p2, end));

            return newSegments;
        }
    }
}
