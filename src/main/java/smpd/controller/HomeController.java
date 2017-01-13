package smpd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import smpd.*;
import smpd.dto.ClassifierDTO;

import javax.servlet.http.HttpServletRequest;

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
        String error = null;
        try {
            FisherSelector fisherSelector = new FisherSelector();
            int[] bestFeatures = fisherSelector.getBestFeatures(classifierDTO.getBestFeaturesCount());
            for (int i : bestFeatures) {
                System.out.println("index : " + i);
            }

            Classifier classifier = null;
            switch (classifierDTO.getClassifierNo()) {
                case 1:
                    classifier = new NNClassifier(classifierDTO.getLearningPerct(), bestFeatures);
                case 2:
                    classifier = new KNNClassifier(classifierDTO.getLearningPerct(), bestFeatures, classifierDTO.getK());
                case 3:
                    classifier = new NMClassifier(classifierDTO.getLearningPerct(), bestFeatures);
            }
            double pertence = classifier.doClassificationOnClassifyPart();
            result = String.valueOf(pertence);
        } catch (Exception e) {
            error = e.getMessage();
        }
        if (result == null) {
            model.addAttribute("error", error);
        } else {
            model.addAttribute("success", result);
        }
        return "classifier";
    }
}
