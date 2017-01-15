package smpd.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import smpd.*;
import smpd.dto.ClassifierDTO;
import smpd.dto.FisherDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Created by Maciej on 2016-12-27.
 */
@Controller
@Slf4j
public class HomeController {

    private FisherDTO fisherDTO = new FisherDTO();
    private String fisherMessage;

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {
        String name = request.getParameter("name");
        model.addAttribute("user", name != null ? name : "world");
        model.addAttribute("title", "Home page");
        return "home";
    }

    @RequestMapping(value = "/classifier", method = RequestMethod.GET)
    public String get(Model model) {
        fisherDTO = new FisherDTO();
        fisherMessage = null;

        return classifierGet(model);
    }

    public String classifierGet(Model model) {
        model.addAttribute("classifierDTO", new ClassifierDTO());
        model.addAttribute("fisherDTO", fisherDTO);
        model.addAttribute("classyficationEnabled", fisherDTO != null ? fisherDTO.getBestFeaturesIndexes() != null : false);
        model.addAttribute("message", fisherMessage);
        return "classifier";
    }

    @RequestMapping(value = "/crossvalidation", method = RequestMethod.POST)
    public String postCrossValidation(Model model, ClassifierDTO classifierDTO) throws Exception {
        Classifier classifier = null;
        long start = System.currentTimeMillis();
        switch (classifierDTO.getClassifierNo()) {
            case 1:
                log.warn("NN");
                classifier = new NNClassifier(classifierDTO.getLearningPerct(), fisherDTO.getBestFeaturesIndexes());
                break;
            case 2:
                log.warn("KNN");
                classifier = new KNNClassifier(classifierDTO.getLearningPerct(), fisherDTO.getBestFeaturesIndexes(), classifierDTO.getK());
                break;
            case 3:
                log.warn("NM");
                classifier = new NMClassifier(classifierDTO.getLearningPerct(), fisherDTO.getBestFeaturesIndexes());
                break;
        }
        double result = classifier.doCrossValidation(4);
        long stop = System.currentTimeMillis() - start;
        model.addAttribute("crossvalidationResult", "Result: "+String.valueOf(result*100) + "%. Time: "+stop+" ms");
        return classifierGet(model);
    }

    @RequestMapping(value = "/classifier", method = RequestMethod.POST)
    public String classifierPost(Model model, ClassifierDTO classifierDTO, FisherDTO fisherDTO) {
        if (this.fisherDTO.getBestFeaturesIndexes() == null || this.fisherDTO.getBestFeaturesIndexes().length == 0) {
            String sfsString = "";
            int[] bestFeatures = null;
            long stop = 0;
            try {
                FisherSelector fisherSelector = new FisherSelector();
                if (fisherDTO.isSfs()) {
                    long start = System.currentTimeMillis();
                    bestFeatures = fisherSelector.getBestFeaturesWithSFS(fisherDTO.getBestFeaturesCountFisher());
                    stop = System.currentTimeMillis() - start;
                    sfsString = " with SFS ";
                    log.warn("SFS");
                } else {
                    long start = System.currentTimeMillis();
                    bestFeatures = fisherSelector.getBestFeatures(fisherDTO.getBestFeaturesCountFisher());
                    stop = System.currentTimeMillis() - start;
                    log.warn("No SFS");
                }
            } catch (Exception e) {

            }
            fisherMessage = "Indexes of best features selected by Fisher: " + Arrays.toString(bestFeatures) + sfsString + " in time: " + stop + " millisecond";
            this.fisherDTO.setBestFeaturesIndexes(bestFeatures);
            this.fisherDTO.setBestFeaturesCountFisher(fisherDTO.getBestFeaturesCountFisher());
            return classifierGet(model);
        }

        String result = null;
        String error = paramsCheck(classifierDTO);
        if (error != null) {
            model.addAttribute("error", error);
            return classifierGet(model);
        }

        paramsCheck(classifierDTO);
        try {
            String k = "";
            Classifier classifier = null;
            switch (classifierDTO.getClassifierNo()) {
                case 1:
                    classifier = new NNClassifier(classifierDTO.getLearningPerct(), this.fisherDTO.getBestFeaturesIndexes());
                    log.warn("NN");
                    break;
                case 2:
                    classifier = new KNNClassifier(classifierDTO.getLearningPerct(), this.fisherDTO.getBestFeaturesIndexes(), classifierDTO.getK());
                    k = " with k=" + classifierDTO.getK();
                    log.warn("KNN");
                    break;
                case 3:
                    classifier = new NMClassifier(classifierDTO.getLearningPerct(), this.fisherDTO.getBestFeaturesIndexes());
                    log.warn("NM");
                    break;
            }
            long start = System.currentTimeMillis();
            double pertence = classifier.doClassificationOnClassifyPart() * 100;
            long stop = System.currentTimeMillis() - start;
            result = classifier.getClass().getSimpleName() + k + " result = " + String.valueOf(pertence) + "% in time " + stop + " millisecond";
        } catch (Exception e) {
            error = e.getMessage();
            e.printStackTrace();
        }
        if (result == null) {
            model.addAttribute("error", error);
        } else {
            model.addAttribute("success", result);
        }
        return classifierGet(model);
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
