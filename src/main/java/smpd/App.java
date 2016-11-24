package smpd;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Hello World!");
        List<Sample> featureMatrix = DataLoader.getFeatureMatrixFromFile();
    }
}
