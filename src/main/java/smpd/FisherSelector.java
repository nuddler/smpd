package smpd;

import Jama.Matrix;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bdlugosz on 19.12.16.
 */
public class FisherSelector {
    private final ClassOfSample classA;
    private final ClassOfSample classB;

    public FisherSelector() throws FileNotFoundException {
        List<Sample> wholeMatrixOfSample = DataLoader.getFeatureMatrixFromFile();

        Matrix sampleOfClassAcerMatrix = ClassOfSample.createSampleOfClassMatrix(wholeMatrixOfSample, Sample.ClassName.A);
        Matrix sampleOfClassQuercusMatrix = ClassOfSample.createSampleOfClassMatrix(wholeMatrixOfSample, Sample.ClassName.B);

        classA = new ClassOfSample(sampleOfClassAcerMatrix);
        classB = new ClassOfSample(sampleOfClassQuercusMatrix);
    }


    public int[] getBestFeatures(int selectedFeatureCount) {
        int featuresCount = classA.getAverageMatrix().getRowDimension();
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(featuresCount, selectedFeatureCount);
        int[] bestFeatures = new int[selectedFeatureCount];
        double fisher = Double.NEGATIVE_INFINITY;
        while (iterator.hasNext()) {
            int[] next = iterator.next();
            double currentFisher = calculateFisher(next);
            if (currentFisher > fisher) {
                bestFeatures = next;
                fisher = currentFisher;
            }
        }
        return bestFeatures;
    }

    private List<Integer> getBestFeatures(List<Integer> features) {
        int featuresCount = classA.getAverageMatrix().getRowDimension();
        double fisher = Double.NEGATIVE_INFINITY;
        List<Integer> bestFeatures = new ArrayList<Integer>(features);
        bestFeatures.add(new Integer(0));

        int size = features.size();
        for (int i = 0; i < featuresCount; i++) {
            if (!features.contains(new Integer(i))) {
                setOrAddInList(features, size, i);
                Integer[] integers = features.toArray(new Integer[features.size()]);

                double currentFisher = calculateFisher(ArrayUtils.toPrimitive(integers));
                if (currentFisher > fisher) {
                    Collections.copy(bestFeatures,features);
                    fisher = currentFisher;
                }
            }
        }
        return bestFeatures;
    }

    private void setOrAddInList(List<Integer> features, int size, int i) {
        if(features.size() > size) {
            features.set(size, new Integer(i));
        } else {
            features.add(size, new Integer(i));
        }
    }

    public int[] getBestFeaturesWithSFS(int selectedFeatureCount) {
        List<Integer> bestFeatures = new ArrayList<Integer>();

        for (int i = 0; i < selectedFeatureCount; i++) {
            bestFeatures = getBestFeatures(bestFeatures);
        }

        int[] bestFeaturesArray = new int[bestFeatures.size()];
        for (int i = 0; i < bestFeatures.size(); i++) {
            bestFeaturesArray[i] = bestFeatures.get(i); // Watch out for NullPointerExceptions!
        }

        return bestFeaturesArray;
    }

    private double calculateFisher(int[] features) {
        Matrix xa = classA.getSampleMatrix();
        Matrix xb = classB.getSampleMatrix();

        Matrix avgA = classA.getAverageMatrix();
        Matrix avgB = classB.getAverageMatrix();

        Matrix pXa = xa.getMatrix(0, xa.getRowDimension() - 1, features);
        Matrix pXb = xb.getMatrix(0, xb.getRowDimension() - 1, features);
        Matrix pAvgA = avgA.getMatrix(features, 0, 0);
        Matrix pAvgB = avgB.getMatrix(features, 0, 0);

        Matrix sA = getSMatrix(pXa, pAvgA);
        Matrix sB = getSMatrix(pXb, pAvgB);

        double minus = calculateEuclidesDistance(pAvgA.getRowPackedCopy(), pAvgB.getRowPackedCopy());

        double sum = sA.det() + sB.det();

        double fisher = minus / sum;

        return fisher;
    }

    /**
     * Oblicza odległość Euklidesową.
     *
     * @param avgA wektor srednich cech klasy A
     * @param avgB wektor srednich cech klasy B
     * @return odleglosc
     */
    public static double calculateEuclidesDistance(double[] avgA, double[] avgB) {
        int size = avgA.length;

        double avgSumPoweredSubstraction = 0;

        for (int i = 0; i < size; i++) {
            avgSumPoweredSubstraction += Math.pow(avgA[i] - avgB[i], 2);
        }

        return Math.sqrt(avgSumPoweredSubstraction);
    }

    private static Matrix getSMatrix(Matrix x, Matrix avg) {
        // Matrix minus = getMinusElement(x, avg);
        double[][] doubles = replicateVectorToMatrix(avg.getRowPackedCopy(), x.getRowDimension());
        Matrix avgNew = new Matrix(doubles);
        Matrix minus1 = x.transpose().minus(avgNew);

        Matrix times = minus1.times(minus1.transpose());
        return times;
    }

    /**
     * Metoda replikuje wektory w celu utworzenia macierzy odpowiedniej dlugosci.
     *
     * @param vector       wektor
     * @param matrixLength ilosc wektorow w macierzy
     * @return macierz
     */
    public static double[][] replicateVectorToMatrix(double[] vector, int matrixLength) {
        double[][] matrix = new double[vector.length][matrixLength];

        for (int i = 0; i < vector.length; i++) {
            for (int j = 0; j < matrixLength; j++) {
                matrix[i][j] = vector[i];
            }
        }

        return matrix;
    }
}
