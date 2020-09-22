package uk.ac.ed.inf;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GridReader {

    public static int[][] readGridValues(String filePath) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(filePath));
        scanner.useDelimiter(", |\\r|\\n"); // pattern matching for right delimiting
        ArrayList<ArrayList<Integer>> grid = new ArrayList<>();
        ArrayList<Integer> currRow = new ArrayList<>();
        while(scanner.hasNext()) {
            if (!scanner.hasNextInt()) {
                grid.add(currRow);
                currRow = new ArrayList<>();
                scanner.next();
            }
            currRow.add(Integer.parseInt(scanner.next()));
        }
        grid.add(currRow);
        return GridReader.toArray(grid);
    }

    private static int[][] toArray(ArrayList<ArrayList<Integer>> grid) {
        int[][] res = new int[grid.size()][grid.get(0).size()];
        for(int i=0; i < grid.size(); i++) {
            res[i] = Arrays.stream(grid.get(i).toArray())
                    .mapToInt(integer -> (int) integer).toArray();
        }
        return res;
    }
}
