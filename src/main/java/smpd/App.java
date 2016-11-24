package smpd;

import java.io.FileNotFoundException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Hello World!");
        Double[][] featureMatrix = DataLoader.getFeatureMatrixFromFile();

    }
}
