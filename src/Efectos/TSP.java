package Efectos;

import Template.Canvas;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TSP extends Canvas {

    private final int CITY_COUNT = 30;
    private final ArrayList<Point2D> cities = new ArrayList<>();
    private ArrayList<Point2D> bestPath;
    private double bestDistance = Double.MAX_VALUE;
    private final Random rand = new Random();
    private boolean end = false;

    public TSP(int fps) {
        super(fps);
        generateCities();
        bestPath = new ArrayList<>(cities);
        bestDistance = calculateTotalDistance(bestPath);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                end = e.getKeyCode() == KeyEvent.VK_ESCAPE;
            }
        });
    }

    private void generateCities() {
        for (int i = 0; i < CITY_COUNT; i++) {
            double x = 100 + rand.nextDouble() * (SIZE.width - 200);
            double y = 100 + rand.nextDouble() * (SIZE.height - 200);
            cities.add(new Point2D.Double(x, y));
        }
    }

    private double calculateTotalDistance(ArrayList<Point2D> path) {
        double dist = 0;
        for (int i = 0; i < path.size(); i++) {
            Point2D p1 = path.get(i);
            Point2D p2 = path.get((i + 1) % path.size());
            dist += p1.distance(p2);
        }
        return dist;
    }

    private ArrayList<Point2D> generateNeighbor(ArrayList<Point2D> path) {
        ArrayList<Point2D> newPath = new ArrayList<>(path);
        int i = rand.nextInt(CITY_COUNT);
        int j = rand.nextInt(CITY_COUNT);
        Collections.swap(newPath, i, j);
        return newPath;
    }

    @Override
    public void update() {
        ArrayList<Point2D> candidate = generateNeighbor(bestPath);
        double candidateDistance = calculateTotalDistance(candidate);

        if (candidateDistance < bestDistance) {
            bestPath = candidate;
            bestDistance = candidateDistance;
        }
    }

    @Override
    public void painting(Graphics2D g2d) {
        g2d.setColor(Color.white);
        g2d.setStroke(new BasicStroke(3));

        for (int i = 0; i < bestPath.size(); i++) {
            Point2D p1 = bestPath.get(i);
            Point2D p2 = bestPath.get((i + 1) % bestPath.size());

            g2d.drawLine((int) p1.getX(), (int) p1.getY(),
                    (int) p2.getX(), (int) p2.getY());
        }

        g2d.setColor(Color.red);
        for (Point2D city : bestPath) {
            g2d.fillOval((int) city.getX() - 10, (int) city.getY() - 10, 20, 20);
        }

        g2d.setColor(Color.green);
        g2d.setFont(new Font("JETBRAINS MONO", Font.BOLD, 30));
        g2d.drawString("Distancia: " + String.format("%.2f", bestDistance), 30, 50);
    }

    @Override
    public boolean isFinished() {
        return end;
    }
}
