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

    private int[] getBestFeatures(int selectedFeatureCount, int featuresCount, Matrix avgA, Matrix avgB, Matrix xa, Matrix xb) {
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(featuresCount, selectedFeatureCount);
        int[] bestFeatures = new int[featuresCount];
        double fisher = Double.MIN_VALUE;
        while (iterator.hasNext()) {
            int[] next = iterator.next();
            double currentFisher = calculateFisher(next, xa, xb, avgA, avgB);
            if (currentFisher > fisher) {
                bestFeatures = next;
                fisher = currentFisher;
            }
        }
        return bestFeatures;
    }

    private double calculateFisher(int[] features, Matrix xa, Matrix xb, Matrix avgA, Matrix avgB) {
        Matrix pXa = xa.getMatrix(0, xa.getRowDimension() - 1, features);
        Matrix pXb = xb.getMatrix(0, xb.getRowDimension() - 1, features);
        Matrix pAvgA = avgA.getMatrix(features, 0, 0);
        Matrix pAvgB = avgB.getMatrix(features, 0, 0);

        Matrix sA = getSMatrix(pXa, pAvgA);
        Matrix sB = getSMatrix(pXb, pAvgB);

        double minus = pAvgA.minus(pAvgB).normF();

        double sum = sA.det() + sB.det();

        double fisher = minus / sum;

        return fisher;
    }

    private Matrix getSMatrix(Matrix x, Matrix avg) {
        Matrix minus = x.minus(avg);
        Matrix times = minus.times(minus.transpose());
        return times;
    }
}
