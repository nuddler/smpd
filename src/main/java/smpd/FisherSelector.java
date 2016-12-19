package smpd;

import Jama.Matrix;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.io.FileNotFoundException;
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

        Matrix sampleOfClassAcerMatrix = ClassOfSample.createSampleOfClassMatrix(wholeMatrixOfSample, Sample.ClassName.ACER);
        Matrix sampleOfClassQuercusMatrix = ClassOfSample.createSampleOfClassMatrix(wholeMatrixOfSample, Sample.ClassName.QUERCUS);

        classA = new ClassOfSample(sampleOfClassAcerMatrix);
        classB = new ClassOfSample(sampleOfClassQuercusMatrix);
    }


    public int[] getBestFeatures(int selectedFeatureCount) {
        int featuresCount = classA.getAverageMatrix().getRowDimension();
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(featuresCount, selectedFeatureCount);
        int[] bestFeatures = new int[featuresCount];
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
