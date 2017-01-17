package smpd;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by nuddler on 23.11.16.
 */
public class DataLoader {

    private static List<Sample> samples;

    public static List<Sample> getFeatureMatrixFromFile() throws FileNotFoundException {
        if(samples != null) {
            return samples;
        }

        File file = new File("resource/input.txt");
        Scanner br = null;

        ArrayList<ArrayList<String>> arrayLists = new ArrayList<ArrayList<String>>();

        br = new Scanner(new FileReader(file));
        for (int i = 0; br.hasNextLine(); i++) {
            String line = br.nextLine();
            String[] strings = line.split(",");

            arrayLists.add(new ArrayList<String>());
            for (String s : strings) {
                arrayLists.get(i).add(s);
            }
        }

        samples = new ArrayList<Sample>();
        for (ArrayList<String> arrayList : arrayLists) {
            Sample sample = new Sample();
            sample.setClassName(arrayList.get(0));
            for (int j = 0; j < arrayLists.get(0).size() - 1; j++) {
                sample.getFeatureList().add(Double.valueOf(arrayList.get(j + 1)));
            }
            samples.add(sample);
        }
        Collections.shuffle(samples);
        return samples;
    }
}
