package Template;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import javax.swing.JPanel;

public abstract class Canvas extends JPanel implements Runnable {

    private Thread mainThread;
    private boolean running = false;
    private final int fps;
    public final Dimension SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    public Canvas(int fps) {
        super();
        this.fps = fps;
        this.setBackground(Color.black);
        this.setPreferredSize(SIZE);
        this.setFocusable(true);
        this.setLocale(null);
    }

    public void init() {
        if (!running) {
            running = true;
            mainThread = new Thread(this);
            mainThread.start();
        }
    }

    public void pause() {
        running = false;
        if (mainThread != null)
            try {mainThread.join();} catch (InterruptedException e) {}

    }

    @Override
    public void run() {
        long frameTime = 1000 / fps;
        while (running) {
            long startTime = System.currentTimeMillis();
            update();
            repaint();
            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = frameTime - elapsed;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {}
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        painting((Graphics2D) g);
        g.dispose();
    }

    public abstract void painting(Graphics2D g2d);
    public abstract void update();
    public abstract boolean isFinished();
}
