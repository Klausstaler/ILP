package uk.ac.ed.inf;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Utility class to read the air pollution values from a text file.
 */
public class GridReader {

    /**
     * @param filePath Path where the text file is located
     * @return 2D array of ints where each value represents the air pollution of the tile at this
     * position.
     * @throws FileNotFoundException
     */
    public static int[][] readGridValues(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));

        List<List<Integer>> grid = new ArrayList<>();
        while(scanner.hasNextLine()) {

            String line = scanner.nextLine().replace(" ",""); // remove spaces

            List<Integer> gridRow = new ArrayList<>();
            for(String val : line.split(","))
                gridRow.add(Integer.parseInt(val));
            grid.add(gridRow);
        }

        return GridReader.listToArray(grid);
    }

    /**
     * @param grid 2D ArrayList of Integer objects
     * @return 2D Array of ints
     */
    private static int[][] listToArray(List<List<Integer>> grid) {
        int[][] res = new int[grid.size()][grid.get(0).size()];
        for (int i = 0; i < grid.size(); i++) {
            res[i] = Arrays.stream(grid.get(i).toArray())
                    .mapToInt(integer -> (int) integer).toArray(); // small utility to convert
            // Integer objects to primitive int
        }
        return res;
    }
}
