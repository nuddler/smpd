package smpd;


import Jama.Matrix;

/**
 * Created by nuddler on 29.11.16.
 */
public class ClassOfSample {

    private String className;
    private Matrix averageMatrix;
    private Matrix sampelMatrix;

    public ClassOfSample() {
    }

    public ClassOfSample(Matrix v2) {
        sampelMatrix = v2;
        averageMatrix = calculateAverageMatrix(v2);
    }

    private Matrix calculateAverageMatrix(Matrix v2) {
        double[][] doubles = new double[v2.getColumnDimension()][1];
        for (int i = 0; i < v2.getColumnDimension(); i++) {
            doubles[i][0] = calculateAvg(v2.getArrayCopy()[i]);
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
}
