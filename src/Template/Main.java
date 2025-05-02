package Template;

import Efectos.*;
import javax.swing.*;
import java.util.function.Supplier;

public class Main {

    private static final Supplier<Canvas>[] simulations = new Supplier[] {
            () -> new RTX(60),
            () -> new SortingVisualizer(30),
            () -> new Maze(30),
            () -> new Conway(30),
            () -> new TSP(120),
            () -> new Koch(1),
            () -> new Miel(60),
            () -> new Clock(60),
            () -> new Tela(60),
            () -> new AsciiImageViewer(20),
            () -> new EightQueens(5),
            () -> new BirdFlock(60),
    };

    private static int currentIndex = 0;
    private static JFrame f;
    private static Canvas currentSim;

    public static void main(String[] args) {
        f = new JFrame("Simulacion");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showSimulation(currentIndex);

        Timer timer = new Timer(1000, e -> checkSimulationStatus());
        timer.start();

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private static void showSimulation(int index) {
        currentSim = simulations[index].get();
        f.add(currentSim);
        f.pack();
        currentSim.requestFocusInWindow();
        currentSim.init();
    }

    private static void checkSimulationStatus() {
        if (currentSim.isFinished()) {
            currentSim.pause();
            f.remove(currentSim);
            currentIndex = (currentIndex + 1) % simulations.length;
            showSimulation(currentIndex);
        }
    }
}
