package smpd;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws FileNotFoundException {
        List<Sample> featureMatrix = DataLoader.getFeatureMatrixFromFile();

    }

    private int[] getBestFeatures(int selectedFeatureCount, int featuresCount) {
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(featuresCount,selectedFeatureCount);
        int[] bestFeatures = new int[featuresCount];
        double fisher = Double.MIN_VALUE;
        while(iterator.hasNext()) {
            int[] next = iterator.next();
                double currentFisher = calculateFisher(next);
            if (currentFisher > fisher) {
                bestFeatures = next;
            }
        }
        return bestFeatures;
    }

    private double calculateFisher(int[] features) {
        return 0;
    }

}
