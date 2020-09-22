package uk.ac.ed.inf;

import java.io.FileNotFoundException;


public class App 
{
    public static void main( String[] args ) throws FileNotFoundException {
        System.out.println( "Hello World!" );
        GridReader.readGridValues("predictions.txt");
    }
}
