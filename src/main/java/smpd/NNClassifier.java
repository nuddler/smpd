package smpd;

import java.io.FileNotFoundException;

/**
 * Created by bdlugosz on 19.12.16.
 */
public class NNClassifier extends KNNClassifier {
    public NNClassifier(int trainingPartPercent, int[] selectedFeatures) throws FileNotFoundException {
        super(trainingPartPercent, selectedFeatures, 1);
    }
}
