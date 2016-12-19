package smpd;


import Jama.Matrix;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuddler on 29.11.16.
 */
public class ClassOfSample {

    private String className;
    private Matrix averageMatrix;
    private Matrix sampleMatrix;

    public ClassOfSample() {
    }

    public ClassOfSample(Matrix sampleMatrixOfClass) {
        sampleMatrix = sampleMatrixOfClass;
        averageMatrix = calculateAverageMatrix(sampleMatrixOfClass);
    }

    public static Matrix createSampleOfClassMatrix(List<Sample> samples, Sample.ClassName className) {
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

    private Matrix calculateAverageMatrix(Matrix sampleMatrixOfClass) {
        double[][] doubles = new double[sampleMatrixOfClass.getColumnDimension()][1];
        for (int i = 0; i < sampleMatrixOfClass.getColumnDimension(); i++) {
            doubles[i][0] = calculateAvg(sampleMatrixOfClass.transpose().getArray()[i]);
        }
        Matrix avgMatrix = new Matrix(doubles);
        return avgMatrix;
    }

    private double calculateAvg(double[] doubles) {
        double avg = 0;
        if (doubles != null) {
            for (double d : doubles) {
                avg += d;
            }
            if (avg != 0) {
                avg /= doubles.length;
            }
        }
        return avg;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Matrix getAverageMatrix() {
        return averageMatrix;
    }

    public void setAverageMatrix(Matrix averageMatrix) {
        this.averageMatrix = averageMatrix;
    }

    public Matrix getSampleMatrix() {
        return sampleMatrix;
    }

    public void setSampleMatrix(Matrix sampleMatrix) {
        this.sampleMatrix = sampleMatrix;
    }
}
