package Efectos;

import Template.Canvas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EightQueens extends Canvas {

    private final int SIZE = 8;
    private final int[] queens = new int[SIZE]; // Índice = fila, valor = columna
    private boolean finished = false, end = false;
    private int row = 0;
    private int[] cols = new int[SIZE];
    private BufferedImage queenImage;

    public EightQueens(int fps) {
        super(fps);
        for (int i = 0; i < SIZE; i++) {
            queens[i] = -1;
            cols[i] = 0;
        }

        try {
            queenImage = ImageIO.read(new File("src/resources/queen.png"));
        } catch (IOException e) {
            System.err.println("No jala w");
        }

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                end = e.getKeyCode() == KeyEvent.VK_ESCAPE;
            }
        });
    }

    @Override
    public void update() {
        if (finished) return;

        while (row >= 0 && row < SIZE) {
            boolean placed = false;

            while (cols[row] < SIZE) {
                if (isSafe(row, cols[row])) {
                    queens[row] = cols[row];
                    cols[row]++;
                    row++;
                    placed = true;
                    break;
                } else {
                    cols[row]++;
                }
            }

            if (!placed) {
                queens[row] = -1;
                cols[row] = 0;
                row--;
            }

            return;
        }

        finished = true;
    }

    private boolean isSafe(int r, int c) {
        for (int i = 0; i < r; i++) {
            if (queens[i] == c || Math.abs(i - r) == Math.abs(queens[i] - c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void painting(Graphics2D g2d) {
        int tileSize = getHeight() / 12;
        int boardSize = tileSize * SIZE;
        int offsetX = (getWidth() - boardSize) / 2;
        int offsetY = (getHeight() - boardSize) / 2;

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                g2d.setColor((r + c) % 2 == 0 ? Color.WHITE : Color.GRAY);
                g2d.fillRect(offsetX + c * tileSize, offsetY + r * tileSize, tileSize, tileSize);

                if (queens[r] == c && queenImage != null) {
                    g2d.drawImage(queenImage,
                            offsetX + c * tileSize + tileSize / 10,
                            offsetY + r * tileSize + tileSize / 10,
                            tileSize - tileSize / 5,
                            tileSize - tileSize / 5,
                            null);
                }
            }
        }
    }

    @Override
    public boolean isFinished() {
        return end;
    }
}
