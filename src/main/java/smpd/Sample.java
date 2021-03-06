package smpd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdlugosz on 24.11.16.
 */
public class Sample {

    public enum ClassName {
        ACER,QUERCUS,A,B;

        public static ClassName fromString(String className) {
            if(className == null) {
                return ACER;
            }

//            if(className.contains("Acer")) {
//                return ACER;
//            }
//
//            if(className.contains("Quercus")) {
//                return QUERCUS;
//            }

            if(className.equals("a") || className.contains("Acer")) {
                return A;
            }

            if(className.equals("b") || className.contains("Quercus")) {
                return B;
            }
            return null;
        }
    }

    private String classNameString;
    private ClassName className;

    private List<Double> featureList = new ArrayList<Double>();


    public List<Double> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<Double> featureList) {
        this.featureList = featureList;
    }

    public ClassName getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.classNameString = className;
        this.className = ClassName.fromString(className);
    }

    @Override
    public String toString() {
        return "Sample{" +
                "featureList=" + featureList +
                ", classNameString='" + classNameString + '\'' +
                ", className=" + className +
                '}';
    }
}
