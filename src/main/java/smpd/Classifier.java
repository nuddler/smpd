package smpd;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by bdlugosz on 19.12.16.
 */
public abstract class Classifier {

    protected List<Sample> trainingPart = null;
    protected List<Sample> classifyPart = null;
    protected int[] selectedFeatures;

    public double doClassificationOnClassifyPart() {
        double goodClassifiedSamplesCount = 0;

        for (Sample sample : classifyPart) {
            Sample.ClassName className = classify(sample);
            if(className.equals(sample.getClassName())) {
                goodClassifiedSamplesCount++;
            }
        }
        return goodClassifiedSamplesCount/classifyPart.size();
    }

    abstract public Sample.ClassName classify(Sample sample);

    public Classifier(int trainingPartPercent, int[] selectedFeatures) throws FileNotFoundException {
        this.selectedFeatures = selectedFeatures;
        List<Sample> featureMatrixFromFile = DataLoader.getFeatureMatrixFromFile();
        int index = (int) (featureMatrixFromFile.size() * (trainingPartPercent / 100.0));
        if (featureMatrixFromFile.size() > index) {
            trainingPart = featureMatrixFromFile.subList(0, index);
            classifyPart = featureMatrixFromFile.subList(index + 1, featureMatrixFromFile.size());
        }
    }
}
