package smpd;

import Jama.Matrix;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws FileNotFoundException {
        List<Sample> featureMatrix = DataLoader.getFeatureMatrixFromFile();
        System.out.println(featureMatrix.size());
        Matrix v = createSampleOfClassMatrix(featureMatrix, Sample.ClassName.ACER);
        Matrix v2 = createSampleOfClassMatrix(featureMatrix, Sample.ClassName.QUERCUS);
        ClassOfSample acerClassOfSample = new ClassOfSample(v);
        ClassOfSample quercusClassOfSample = new ClassOfSample(v2);

        int[] bestFeatures = getBestFeatures(3, acerClassOfSample, quercusClassOfSample);
        for(int i : bestFeatures) {
            System.out.println("index :" + i);
        }
        

    }

    private static Matrix createSampleOfClassMatrix(List<Sample> samples, Sample.ClassName className) {
        List<double[]> sampleFeaturesList = new ArrayList<double[]>();

        for (Sample s : samples) {
            if (s.getClassName().equals(className)) {
                Double[] objects = new Double[s.getFeatureList().size()];
                objects = s.getFeatureList().toArray(objects);

                double[] doubles = ArrayUtils.toPrimitive(objects);
                sampleFeaturesList.add(doubles);
            }
        }
        double[][] arrays = getDoubles(sampleFeaturesList);

        Matrix matrix = new Matrix(arrays);
        return matrix;
    }

    private static double[][] getDoubles(List<double[]> listOfList) {
        double arrays[][] = new double[listOfList.size()][listOfList.get(0).length];
        for (int i = 0; i < listOfList.size(); i++) {
            arrays[i] = listOfList.get(i);
        }
        return arrays;
    }

    private static int[] getBestFeatures(int selectedFeatureCount, ClassOfSample classA, ClassOfSample classB) {
        int featuresCount = classA.getAverageMatrix().getRowDimension();
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(featuresCount, selectedFeatureCount);
        int[] bestFeatures = new int[featuresCount];
        double fisher = Double.NEGATIVE_INFINITY;
        while (iterator.hasNext()) {
            //System.out.println("next");
            int[] next = iterator.next();
            double currentFisher = calculateFisher(next, classA, classB);
            if (currentFisher > fisher) {
                bestFeatures = next;
                fisher = currentFisher;
            }
        }
        return bestFeatures;
    }

    private static double calculateFisher(int[] features, ClassOfSample classA, ClassOfSample classB) {
        Matrix xa = classA.getSampelMatrix();
        Matrix xb = classB.getSampelMatrix();

        Matrix avgA = classA.getAverageMatrix();
        Matrix avgB = classB.getAverageMatrix();

        Matrix pXa = xa.getMatrix(0, xa.getRowDimension() - 1, features);
        Matrix pXb = xb.getMatrix(0, xb.getRowDimension() - 1, features);
        Matrix pAvgA = avgA.getMatrix(features, 0, 0);
        Matrix pAvgB = avgB.getMatrix(features, 0, 0);

        Matrix sA = getSMatrix(pXa, pAvgA);
        Matrix sB = getSMatrix(pXb, pAvgB);

        double minus = calculateEuclidesDistance(pAvgA.getRowPackedCopy(),pAvgB.getRowPackedCopy());

        double sum = sA.det() + sB.det();

        double fisher = minus / sum;

        //System.out.println("fisher: " + fisher);
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
     * @param vector wektor
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
