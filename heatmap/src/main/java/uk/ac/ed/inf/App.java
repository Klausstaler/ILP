package uk.ac.ed.inf;

import java.io.IOException;


public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        int[][] grid = GridReader.readGridValues("predictions.txt");
        GridVisualizer visualizer = new GridVisualizer(grid);
    }
}
