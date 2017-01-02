package smpd;

import Jama.Matrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Maciej on 2016-12-29.
 */
public class NMClassifier extends Classifier{

    int k = 0;

    public NMClassifier(int trainingPartPercent, int[] selectedFeatures) throws FileNotFoundException {
        super(trainingPartPercent, selectedFeatures);
    }

    @Override
    public Sample.ClassName classify(Sample sample) {
        int class1 = 0, class2 = 0;

        return null;
    }

    public static double getMahalonobisDistance(Sample sample) {


        return 0.0;
    }

    public static Matrix getCovarianceMatrix(ClassOfSample classOfSample){
        double[][] doubles = classOfSample.getSampleMatrix().getArray();
        RealMatrix mx = MatrixUtils.createRealMatrix(doubles);
        RealMatrix cov = new Covariance(mx).getCovarianceMatrix();
        Matrix result = new Matrix(cov.getData());
        return result;
    }
}
