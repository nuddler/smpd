package smpd;

import org.apache.commons.lang3.ArrayUtils;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

/**
 * Created by bdlugosz on 19.12.16.
 */
public class KNNClassifier extends Classifier {
    private int k;

    @Override
    public Sample.ClassName classify(Sample sample) {
        List<Sample> nearestNeighborsOfSample = getNearestNeighborsOfSample(sample);
        int class1 = 0, class2 = 0;

        for (Sample sample1 : nearestNeighborsOfSample) {
            if(sample1.getClassName().equals(Sample.ClassName.A)) {
                class1++;
            } else if (sample1.getClassName().equals(Sample.ClassName.B)) {
                class2++;
            }
        }

        return class1 > class2 ? Sample.ClassName.A : Sample.ClassName.B;
    }

    private List<Sample> getNearestNeighborsOfSample(Sample sample) {
        Collections.sort(trainingPart, new EuclideanComparator(sample, selectedFeatures));
        return trainingPart.subList(0, k);
    }

    public KNNClassifier(int trainingPartPercent, int[] selectedFeatures, int k) throws FileNotFoundException {
        super(trainingPartPercent, selectedFeatures);
        this.k = k;
    }

    private class EuclideanComparator implements java.util.Comparator<Sample> {
        private Sample sample;
        private int[] selectedFeatures;

        public EuclideanComparator(Sample sample, int[] selectedFeatures) {

            this.sample = sample;
            this.selectedFeatures = selectedFeatures;
        }

        public int compare(Sample o1, Sample o2) {
            double euclideanDistanceO1 = getEuclideanDistance(o1);
            double euclideanDistanceO2 = getEuclideanDistance(o2);
            if (euclideanDistanceO1 == euclideanDistanceO2) {
                return 0;
            } else if (euclideanDistanceO1 > euclideanDistanceO2) {
                return 1;
            } else {
                return -1;
            }
        }

        private double getEuclideanDistance(Sample trainingSample) {

            double[] featuresArraySample = getFeaturesArray(sample);
            double[] featuresArrayO1 = getFeaturesArray(trainingSample);

            return FisherSelector.calculateEuclidesDistance(featuresArraySample, featuresArrayO1);
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
    }
}
