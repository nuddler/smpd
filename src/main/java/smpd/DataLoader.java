package smpd;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by nuddler on 23.11.16.
 */
public class DataLoader {
    public static Double[][] getFeatureMatrixFromFile() throws FileNotFoundException {
        File file = new File("input.txt");
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

        Double[][] doubles = new Double[arrayLists.get(0).size() - 1][arrayLists.size()];
        for (int i = 0; i < arrayLists.size(); i++) {
            for (int j = 0; j <= arrayLists.get(0).size(); j++) {
                doubles[i][j] = Double.valueOf(arrayLists.get(i).get(j+1));
            }
        }
        return doubles;
    }
}
