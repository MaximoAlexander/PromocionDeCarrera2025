package Efectos;


import Template.Canvas;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Conway extends Canvas {

    private final int cellSize = 10;
    private final int cols;
    private final int rows;
    private boolean[][] grid;
    private boolean[][] nextGrid;
    private boolean finished = false;
    private boolean mousePressed = false;

    public Conway(int fps) {
        super(fps);

        cols = SIZE.width / cellSize;
        rows = SIZE.height / cellSize;

        grid = new boolean[cols][rows];
        nextGrid = new boolean[cols][rows];

        initGrid();

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    finished = true;
                    pause();
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                toggleCell(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mousePressed) {
                    toggleCell(e.getX(), e.getY());
                }
            }
        });
    }

    private void toggleCell(int x, int y) {
        int col = x / cellSize;
        int row = y / cellSize;
        if (col >= 0 && col < cols && row >= 0 && row < rows) {
            grid[col][row] = !grid[col][row];
        }
    }

    private void initGrid() {
        Random rand = new Random();
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                grid[x][y] = rand.nextBoolean();
            }
        }
    }

    @Override
    public void update() {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                int neighbors = countNeighbors(x, y);

                if (grid[x][y]) {
                    nextGrid[x][y] = neighbors == 2 || neighbors == 3;
                } else {
                    nextGrid[x][y] = neighbors == 3;
                }
            }
        }

        boolean[][] temp = grid;
        grid = nextGrid;
        nextGrid = temp;
    }

    private int countNeighbors(int x, int y) {
        int count = 0;

        for (int i = -1; i <= 1; i++) {
            int nx = x + i;
            if (nx < 0 || nx >= cols) continue;

            for (int j = -1; j <= 1; j++) {
                int ny = y + j;
                if (ny < 0 || ny >= rows) continue;
                if (i == 0 && j == 0) continue;

                if (grid[nx][ny]) count++;
            }
        }

        return count;
    }

    @Override
    public void painting(Graphics2D g2d) {

        g2d.setColor(Color.white);
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                if (grid[x][y]) {
                    g2d.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
