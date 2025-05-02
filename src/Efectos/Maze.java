package Efectos;

import Template.Canvas;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

public class Maze extends Canvas {
    private final int cols = 41, rows = 31; // Debe ser impar para que haya paredes entre celdas
    private final int cellSize = 20;
    private final int[][] maze = new int[cols][rows];
    private final boolean[][] visited = new boolean[cols][rows];

    private final Stack<Point> genStack = new Stack<>();
    private final Stack<Point> solveStack = new Stack<>();

    private boolean generating = true;
    private boolean solving = false;
    private boolean solved = false, finished = false;

    public Maze(int fps) {
        super(fps); // 30 FPS
        initMaze();
        genStack.push(new Point(1, 1));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                finished = e.getKeyCode() == KeyEvent.VK_ESCAPE;
            }
        });
    }

    private void initMaze() {
        // Inicializar todo como muro
        for (int x = 0; x < cols; x++)
            for (int y = 0; y < rows; y++)
                maze[x][y] = 1;
    }

    @Override
    public void update() {
        if (generating) {
            generateStep();
        } else if (solving && !solved) {
            solveStep();
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    private void generateStep() {
        if (!genStack.isEmpty()) {
            Point current = genStack.peek();
            visited[current.x][current.y] = true;
            maze[current.x][current.y] = 0;

            List<Point> neighbors = new ArrayList<>();
            int[][] dirs = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
            for (int[] d : dirs) {
                int nx = current.x + d[0], ny = current.y + d[1];
                if (nx > 0 && ny > 0 && nx < cols - 1 && ny < rows - 1 && !visited[nx][ny]) {
                    neighbors.add(new Point(nx, ny));
                }
            }

            if (!neighbors.isEmpty()) {
                Point next = neighbors.get(new Random().nextInt(neighbors.size()));
                genStack.push(next);
                maze[(current.x + next.x) / 2][(current.y + next.y) / 2] = 0; // romper pared
            } else {
                genStack.pop();
            }
        } else {
            generating = false;
            solving = true;
            for (boolean[] row : visited) Arrays.fill(row, false);
            solveStack.push(new Point(1, 1));
        }
    }

    private void solveStep() {
        if (solveStack.isEmpty()) {
            solved = true;
            return;
        }

        Point current = solveStack.peek();
        int x = current.x, y = current.y;

        if (x == cols - 2 && y == rows - 2) {
            solved = true;
            return;
        }

        visited[x][y] = true;

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            if (nx >= 0 && ny >= 0 && nx < cols && ny < rows &&
                    maze[nx][ny] == 0 && !visited[nx][ny]) {
                solveStack.push(new Point(nx, ny));
                return;
            }
        }

        solveStack.pop(); // retroceder si no hay salida
    }

    @Override
    public void painting(Graphics2D g2d) {
        int mazeWidth = cols * cellSize;
        int mazeHeight = rows * cellSize;
        int offsetX = (SIZE.width - mazeWidth) / 2;
        int offsetY = (SIZE.height - mazeHeight) / 2;

        // Dibujar laberinto
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                if (maze[x][y] == 1) {
                    g2d.setColor(Color.BLACK);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillRect(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
            }
        }

        // Entrada y salida
        g2d.setColor(Color.GREEN);
        g2d.fillRect(offsetX + 1 * cellSize, offsetY + 1 * cellSize, cellSize, cellSize); // inicio
        g2d.setColor(Color.BLUE);
        g2d.fillRect(offsetX + (cols - 2) * cellSize, offsetY + (rows - 2) * cellSize, cellSize, cellSize); // fin

        // Camino actual (resolución)
        g2d.setColor(Color.RED);
        for (Point p : solveStack) {
            g2d.fillRect(offsetX + p.x * cellSize, offsetY + p.y * cellSize, cellSize, cellSize);
        }
    }
}