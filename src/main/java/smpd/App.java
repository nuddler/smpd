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


    private void selectFeatures(int[] flags, int d) {
        // for now: check all individual features using 1D, 2-class Fisher criterion

        if(d==1){
            double FLD=0, tmp;
            int max_ind=-1;
            for(int i=0; i<FeatureCount; i++){
                if((tmp=computeFisherLD(F[i]))>FLD){
                    FLD=tmp;
                    max_ind = i;
                }
            }
            l_FLD_winner.setText(max_ind+"");
            l_FLD_val.setText(FLD+"");
        }
        // to do: compute for higher dimensional spaces, use e.g. SFS for candidate selection
    }

    private double computeFisherLD(double[] vec) {
        // 1D, 2-classes
        double mA=0, mB=0, sA=0, sB=0;
        for(int i=0; i<vec.length; i++){
            if(ClassLabels[i]==0) {
                mA += vec[i];
                sA += vec[i]*vec[i];
            }
            else {
                mB += vec[i];
                sB += vec[i]*vec[i];
            }
        }
        mA /= SampleCount[0];
        mB /= SampleCount[1];
        sA = sA/SampleCount[0] - mA*mA;
        sB = sB/SampleCount[1] - mB*mB;
        return Math.abs(mA-mB)/(Math.sqrt(sA)+Math.sqrt(sB));
    }
}
