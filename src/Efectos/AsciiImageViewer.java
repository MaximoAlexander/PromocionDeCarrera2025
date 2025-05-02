package Efectos;

import Template.Canvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class AsciiImageViewer extends Canvas {

    private BufferedImage asciiImage;
    private BufferedImage displayImage;
    private String[][] asciiArt;
    private final String ASCII_CHARS = "@%#*+=-:. ";
    private int asciiWidth, asciiHeight;
    private int progressRow = 0;
    private boolean finished = false;
    private volatile boolean isLoading = false;

    private final int charW = 10, charH = 18;
    private int visualW, visualH;

    private int step = 0;
    private final String[] paths = {
            "yomero.jpg", "yao.jpg", "AMLOFP.jpg", "AMLOHM.jpeg"
    };

    public AsciiImageViewer(int fps) {
        super(fps);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    step++;
                    loadImage("src/resources/" + paths[step % paths.length]);
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT){
                    step = (step == 0) ? paths.length - 1 : step - 1;
                    loadImage("src/resources/" + paths[step % paths.length]);
                }
                finished = e.getKeyCode() == KeyEvent.VK_ESCAPE;
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                loadImage("src/resources/" + paths[step % paths.length]);
            }
        });

        SwingUtilities.invokeLater(() -> {
            loadImage("src/resources/" + paths[0]);
        });
    }

    private void loadImage(String path) {
        isLoading = true;
        try {
            BufferedImage original = ImageIO.read(new File(path));

            int canvasW = Math.max(getWidth(), 800);
            int canvasH = Math.max(getHeight(), 600);

            visualW = (canvasW - 140) / 2;
            double ratio = (double) original.getHeight() / original.getWidth();
            visualH = (int) (visualW * ratio);

            asciiWidth = visualW / charW;
            asciiHeight = visualH / charH;

            Image tmpAscii = original.getScaledInstance(asciiWidth, asciiHeight, Image.SCALE_SMOOTH);
            asciiImage = new BufferedImage(asciiWidth, asciiHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g1 = asciiImage.createGraphics();
            g1.drawImage(tmpAscii, 0, 0, null);
            g1.dispose();

            Image tmpDisplay = original.getScaledInstance(visualW, visualH, Image.SCALE_SMOOTH);
            displayImage = new BufferedImage(visualW, visualH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = displayImage.createGraphics();
            g2.drawImage(tmpDisplay, 0, 0, null);
            g2.dispose();

            asciiArt = new String[asciiHeight][asciiWidth];
            for (int i = 0; i < asciiHeight; i++) {
                for (int j = 0; j < asciiWidth; j++) {
                    asciiArt[i][j] = " ";
                }
            }

            progressRow = 0;
            finished = false;

        } catch (IOException e) {
            System.err.println("No jala w");
        } finally {
            isLoading = false;
        }
    }

    @Override
    public void update() {
        if (isLoading || asciiImage == null || asciiArt == null || progressRow >= asciiHeight || finished) return;

        for (int col = 0; col < asciiWidth; col++) {
            Color color = new Color(asciiImage.getRGB(col, progressRow));
            int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
            int index = Math.min(gray * (ASCII_CHARS.length() - 1) / 255, ASCII_CHARS.length() - 1);
            asciiArt[progressRow][col] = String.valueOf(ASCII_CHARS.charAt(index));
        }
        progressRow++;
    }

    @Override
    public void painting(Graphics2D g2d) {
        if (isLoading || displayImage == null || asciiArt == null) return;

        int spacing = 40;
        int totalW = visualW * 2 + spacing;
        int x = Math.max(50, (getWidth() - totalW) / 2);
        int y = Math.max(50, (getHeight() - visualH) / 2);

        g2d.drawImage(displayImage, x, y, null);

        g2d.setFont(new Font("Monospaced", Font.PLAIN, charH));
        g2d.setColor(Color.white);

        int asciiX = x + visualW + spacing;
        int asciiY = y + charH;

        for (int row = 0; row < progressRow && row < asciiHeight; row++) {
            for (int col = 0; col < asciiWidth; col++) {
                String ch = asciiArt[row][col];
                if (ch != null) {
                    g2d.drawString(ch, asciiX + col * charW, asciiY + row * charH);
                }
            }
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
