package uk.ac.ed.inf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class App {
    public static void main(String[] args) throws IOException {

        int[][] grid = GridReader.readGridValues("predictions.txt");
        GridVisualizer visualizer = new GridVisualizer(grid);

        new File("heatmap.geojson").createNewFile();
        FileWriter writer = new FileWriter("heatmap.geojson");

        String res = visualizer.getFeatureCollection().toJson();
        writer.write(res);
        writer.close();
    }
}
