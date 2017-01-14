package smpd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import smpd.*;
import smpd.dto.ClassifierDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Created by Maciej on 2016-12-27.
 */
@Controller

public class HomeController {

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {
        String name = request.getParameter("name");
        model.addAttribute("user", name != null ? name : "world");
        model.addAttribute("title", "Home page");
        return "home";
    }

    @RequestMapping(value = "/classifier", method = RequestMethod.GET)
    public String classifierGet(HttpServletRequest request, Model model) {

        model.addAttribute("classifierDTO", new ClassifierDTO());
        return "classifier";
    }

    @RequestMapping(value = "/classifier", method = RequestMethod.POST)
    public String classifierPost(Model model, ClassifierDTO classifierDTO) {

        String result = null;
        String error = paramsCheck(classifierDTO);
        if (error != null) {
            model.addAttribute("error", error);
            return "classifier";
        }

        paramsCheck(classifierDTO);
        try {
            FisherSelector fisherSelector = new FisherSelector();
            //int[] bestFeatures = fisherSelector.getBestFeatures(classifierDTO.getBestFeaturesCount());
            int[] bestFeatures = fisherSelector.getBestFeaturesWithSFS(classifierDTO.getBestFeaturesCount());
            for (int i : bestFeatures) {
                System.out.println("index : " + i);
            }
                result="Indexes of best features selected by Fisher: " + Arrays.toString(bestFeatures) + " ";
            Classifier classifier = null;
            switch (classifierDTO.getClassifierNo()) {
                case 1:
                    classifier = new NNClassifier(classifierDTO.getLearningPerct(), bestFeatures);
                case 2:
                    classifier = new KNNClassifier(classifierDTO.getLearningPerct(), bestFeatures, classifierDTO.getK());
                case 3:
                    classifier = new NMClassifier(classifierDTO.getLearningPerct(), bestFeatures);
            }
            double pertence = classifier.doClassificationOnClassifyPart() * 100;
            result += "Result = " + String.valueOf(pertence) + "%";
        } catch (Exception e) {
            error = e.getMessage();
            e.printStackTrace();
        }
        if (result == null) {
            model.addAttribute("error", error);
        } else {
            model.addAttribute("success", result);
        }
        return "classifier";
    }

    private String paramsCheck(ClassifierDTO classifierDTO) {
        if (classifierDTO.getLearningPerct() > 99 || classifierDTO.getLearningPerct() < 1) {
            return "Learning part in percentages should be between 1 and 100";
        }
        int k = classifierDTO.getK();
        double temp = k % 2;
        if (temp == 0 && classifierDTO.getClassifierNo() == 2) {
            return "K should be odd.";
        }
        if (classifierDTO.getBestFeaturesCount() < 0) {
            return "Best features count should be at least 1.";
        }
        return null;
    }
}
