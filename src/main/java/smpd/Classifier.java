package smpd;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by bdlugosz on 19.12.16.
 */
@Slf4j
public abstract class Classifier {

    private final int trainingPartAmount;
    protected List<Sample> trainingPart = null;
    protected List<Sample> classifyPart = null;
    protected int[] selectedFeatures;
    private final List<Sample> sampleList;

    public double doClassificationOnClassifyPart() {
        int index = (int) (sampleList.size() * (trainingPartAmount / 100.0));
        if (sampleList.size() > index) {
            trainingPart = sampleList.subList(0, index);
            classifyPart = sampleList.subList(index + 1, sampleList.size());
        }

        double goodClassifiedSamplesCount = 0;

        for (Sample sample : classifyPart) {
            Sample.ClassName className = classify(sample);
            if (className.equals(sample.getClassName())) {
                goodClassifiedSamplesCount++;
            }
        }
        return goodClassifiedSamplesCount / classifyPart.size();
    }

    abstract public Sample.ClassName classify(Sample sample);

    public Classifier(int trainingPartPercent, int[] selectedFeatures) throws FileNotFoundException {
        this.selectedFeatures = selectedFeatures;
        sampleList = DataLoader.getFeatureMatrixFromFile();
        Collections.shuffle(sampleList);
        trainingPartAmount   = trainingPartPercent;
    }

    public double doCrossValidation(int k) {
        double pertence = 0;

        int crossValidationPartSize = (int) (sampleList.size() / k);
        int startIndex = 0;

        List<List<Sample>> listOfCrossValidationPartsOfsampleList = new ArrayList<List<Sample>>();
        while (sampleList.size() > startIndex) {
            if (sampleList.size() < startIndex + crossValidationPartSize + crossValidationPartSize) {
                listOfCrossValidationPartsOfsampleList.add(sampleList.subList(startIndex, sampleList.size()));
            } else if (sampleList.size() > startIndex + crossValidationPartSize) {
                listOfCrossValidationPartsOfsampleList.add(sampleList.subList(startIndex, startIndex+crossValidationPartSize));
            }
            startIndex += crossValidationPartSize;
        }

        for (int i = 0; i < k; i++) {
            classifyPart = listOfCrossValidationPartsOfsampleList.get(i);
            List<Sample> tPart = new ArrayList<Sample>();
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    tPart.addAll(listOfCrossValidationPartsOfsampleList.get(j));
                }
            }
            trainingPart = tPart;
            log.warn("trainingPart.size()= " + trainingPart.size());
            double classifyPertence = doClassificationOnClassifyPart();
            log.warn("For k: " + i + " pertece= " + classifyPertence);
            pertence += classifyPertence;
        }

        return pertence / k;
    }
}
