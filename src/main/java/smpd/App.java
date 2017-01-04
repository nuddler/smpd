package smpd;

import java.io.FileNotFoundException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws FileNotFoundException {

        FisherSelector fisherSelector = new FisherSelector();
        int[] bestFeatures = fisherSelector.getBestFeatures(3);
        for (int i : bestFeatures) {
            System.out.println("index : " + i);
        }

        //Classifier classifier = new KNNClassifier(50,bestFeatures,50);
        Classifier classifier = new NMClassifier(20,bestFeatures);
        double pertence = classifier.doClassificationOnClassifyPart();
        System.out.println("pertence:" + pertence);
    }
}
