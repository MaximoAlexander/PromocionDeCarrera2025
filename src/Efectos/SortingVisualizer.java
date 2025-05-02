package Efectos;

import Template.Canvas;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SortingVisualizer extends Canvas {

    private int[] array;
    private final int size = 150;
    private final int barWidth;
    private final Random random = new Random();

    private String[] algorithms = {"Bubble Sort", "Selection Sort", "Insertion Sort", "Quick Sort", "Random Sort"};
    private String currentAlgorithm = algorithms[0];

    private boolean sorting = false;
    private boolean finished = false;

    private int i = 0, j = 0;
    private Stack<int[]> stack = new Stack<>();
    private int low = 0, high = size - 1;

    private int activeA = -1, activeB = -1;
    private int sortedIndex = -1;

    // UI
    private Rectangle startButton = new Rectangle(20, 20, 100, 30);
    private Rectangle resetButton = new Rectangle(140, 20, 100, 30);
    private Rectangle algorithmButton = new Rectangle(260, 20, 150, 30);

    public SortingVisualizer(int fps) {
        super(fps);
        array = new int[size];
        barWidth = SIZE.width / size;
        generateArray();

        this.setFocusable(true);
        this.requestFocusInWindow();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();

                if (startButton.contains(p)) {
                    sorting = !sorting;
                } else if (resetButton.contains(p)) {
                    generateArray();
                    sorting = false;
                    finished = false;
                    i = j = 0;
                    low = 0;
                    high = size - 1;
                    sortedIndex = -1;
                    stack.clear();
                } else if (algorithmButton.contains(p)) {
                    int index = (Arrays.asList(algorithms).indexOf(currentAlgorithm) + 1) % algorithms.length;
                    currentAlgorithm = algorithms[index];
                    sorting = false;
                    finished = false;
                    i = j = 0;
                    low = 0;
                    high = size - 1;
                    sortedIndex = -1;
                    stack.clear();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    finished = true;
                    sorting = false;
                }
            }
        });
    }

    private void generateArray() {
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(SIZE.height - 100) + 50;
        }
    }

    @Override
    public void update() {
        if (sorting && !finished) {
            switch (currentAlgorithm) {
                case "Bubble Sort":
                    bubbleSortStep();
                    sortedIndex = array.length - i;
                    break;
                case "Selection Sort":
                    selectionSortStep();
                    sortedIndex = i;
                    break;
                case "Insertion Sort":
                    insertionSortStep();
                    sortedIndex = i;
                    break;
                case "Quick Sort":
                    quickSortStep();
                    sortedIndex = -1;
                    break;
                case "Random Sort":
                    randomSortStep();
                    sortedIndex = -1;
                    break;
            }

            if (!sorting) {
                sortedIndex = array.length;
            }
        }

        if (finished) {
            sorting = false;
        }
    }

    private void bubbleSortStep() {
        if (i < array.length - 1) {
            if (j < array.length - i - 1) {
                activeA = j;
                activeB = j + 1;
                if (array[j] > array[j + 1]) {
                    swap(j, j + 1);
                }
                j++;
            } else {
                j = 0;
                i++;
            }
        } else {
            sorting = false;
        }
    }

    private void selectionSortStep() {
        if (i < array.length - 1) {
            int min = i;
            for (int k = i + 1; k < array.length; k++) {
                if (array[k] < array[min]) {
                    min = k;
                }
            }
            activeA = i;
            activeB = min;
            swap(i, min);
            i++;
        } else {
            sorting = false;
        }
    }

    private void insertionSortStep() {
        if (i < array.length) {
            int key = array[i];
            int k = i - 1;
            while (k >= 0 && array[k] > key) {
                array[k + 1] = array[k];
                activeA = k;
                activeB = k + 1;
                k--;
            }
            array[k + 1] = key;
            i++;
        } else {
            sorting = false;
        }
    }

    private void quickSortStep() {
        if (stack.isEmpty() && i == 0) {
            stack.push(new int[]{0, array.length - 1});
        }

        if (!stack.isEmpty()) {
            int[] range = stack.pop();
            int l = range[0], r = range[1];
            if (l < r) {
                int p = partition(l, r);
                stack.push(new int[]{l, p - 1});
                stack.push(new int[]{p + 1, r});
            }
        } else {
            sorting = false;
        }
    }

    private int partition(int low, int high) {
        int pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            activeA = j;
            activeB = high;
            if (array[j] <= pivot) {
                i++;
                swap(i, j);
            }
        }

        swap(i + 1, high);
        return i + 1;
    }

    private void randomSortStep() {
        int a = random.nextInt(array.length);
        int b = random.nextInt(array.length);
        activeA = a;
        activeB = b;
        swap(a, b);
    }

    private void swap(int a, int b) {
        int temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }

    @Override
    public void painting(Graphics2D g) {
        for (int k = 0; k < array.length; k++) {
            if (k == activeA || k == activeB) {
                g.setColor(Color.red);
            } else if (sortedIndex != -1 && k >= sortedIndex && !currentAlgorithm.equals("Random Sort")) {
                g.setColor(Color.green);
            } else {
                g.setColor(Color.white);
            }
            g.fillRect(k * barWidth, SIZE.height - array[k], barWidth, array[k]);
        }

        drawButton(g, startButton, sorting ? "Pausar" : "Iniciar");
        drawButton(g, resetButton, "Reiniciar");
        drawButton(g, algorithmButton, currentAlgorithm);
    }

    private void drawButton(Graphics2D g, Rectangle rect, String text) {
        g.setColor(Color.gray);
        g.fill(rect);
        g.setColor(Color.white);
        g.draw(rect);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int x = rect.x + (rect.width - textWidth) / 2;
        int y = rect.y + (rect.height + textHeight) / 2 - 4;

        g.drawString(text, x, y);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
