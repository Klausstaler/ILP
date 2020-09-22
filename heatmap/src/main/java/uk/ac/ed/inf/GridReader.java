package uk.ac.ed.inf;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Utility class to read the air pollution values from a text file.
 */
public class GridReader {

    /**
     *
     * @param filePath Path where the text file is located
     * @return 2D array of ints where each value represents the air pollution of the tile at this
     * position.
     * @throws FileNotFoundException
     */
    public static int[][] readGridValues(String filePath) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(filePath));
        scanner.useDelimiter(", |\\r|\\n"); // pattern matching for right delimiting
        ArrayList<ArrayList<Integer>> grid = new ArrayList<>();
        ArrayList<Integer> currRow = new ArrayList<>();
        while (scanner.hasNext()) {
            if (!scanner.hasNextInt()) { // evaluates to false if new line is reached
                grid.add(currRow);
                currRow = new ArrayList<>();
                scanner.next();
            }
            currRow.add(Integer.parseInt(scanner.next()));
        }
        grid.add(currRow);
        return GridReader.listToArray(grid);
    }

    /**
     * @param grid 2D ArrayList of Integer objects
     * @return 2D Array of ints
     */
    private static int[][] listToArray(ArrayList<ArrayList<Integer>> grid) {
        int[][] res = new int[grid.size()][grid.get(0).size()];
        for (int i = 0; i < grid.size(); i++) {
            res[i] = Arrays.stream(grid.get(i).toArray())
                    .mapToInt(integer -> (int) integer).toArray(); // small utility to convert
            // Integer objects to primitive int
        }
        return res;
    }
}
