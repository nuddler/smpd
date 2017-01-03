package smpd;

import Jama.Matrix;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Maciej on 2016-12-29.
 */
public class NMClassifier extends Classifier {

    private final ClassOfSample classA;
    private final ClassOfSample classB;

    public NMClassifier(int trainingPartPercent, int[] selectedFeatures) throws FileNotFoundException {

        super(trainingPartPercent, selectedFeatures);
        List<Sample> wholeMatrixOfSample = DataLoader.getFeatureMatrixFromFile();

        Matrix sampleOfClassAcerMatrix = ClassOfSample.createSampleOfClassMatrix(wholeMatrixOfSample, Sample.ClassName.ACER);
        Matrix sampleOfClassQuercusMatrix = ClassOfSample.createSampleOfClassMatrix(wholeMatrixOfSample, Sample.ClassName.QUERCUS);

        classA = new ClassOfSample(sampleOfClassAcerMatrix);
        classB = new ClassOfSample(sampleOfClassQuercusMatrix);

    }

    @Override
    public Sample.ClassName classify(Sample sample) {
        double class1 = 0, class2 = 0;
        class1 = getMahalonobisDistance(sample);
        class2 = getMahalonobisDistance(sample);
        return class1 < class2 ? Sample.ClassName.ACER : Sample.ClassName.QUERCUS;
    }

    public double getMahalonobisDistance(Sample sample) {
        ClassOfSample classOfSample = sample.getClassName() == Sample.ClassName.ACER ? classA : classB;
        Matrix covarianceMatrix = getCovarianceMatrix(classOfSample);
        Matrix averageMatrix = classOfSample.getAverageMatrix();
        double[] featuresArray = getFeaturesArray(sample);
        double[][] doubles = replicateVectorToMatrix(featuresArray, 1);
        Matrix X = new Matrix(doubles);

        Matrix cz1 = X.minus(averageMatrix).transpose();
        Matrix cz2 = covarianceMatrix.inverse();
        Matrix cz3 = X.minus(averageMatrix);

        double result = doMagic(cz1,cz2,cz3);

//        X.minus(averageMatrix).times(
//        covarianceMatrix.inverse()).times
//                (X.minus(averageMatrix));

        return result;
    }

    private double doMagic(Matrix cz1, Matrix cz2, Matrix cz3) {

        return 0.0;
    }

    public static Matrix getCovarianceMatrix(ClassOfSample classOfSample) {
        double[][] doubles = classOfSample.getSampleMatrix().getArray();
        RealMatrix mx = MatrixUtils.createRealMatrix(doubles);
        RealMatrix cov = new Covariance(mx).getCovarianceMatrix();
        Matrix result = new Matrix(cov.getData());
        return result;
    }

    private double[] getFeaturesArray(Sample sample) {
        double[] featureArray = new double[selectedFeatures.length];
        for (int i = 0, j = 0; i < sample.getFeatureList().size(); i++) {
            if (ArrayUtils.contains(selectedFeatures, i)) {
                featureArray[j] = sample.getFeatureList().get(i);
                j++;
            }
        }
        return featureArray;
    }


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
