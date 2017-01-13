package smpd.dto;

import lombok.Data;

/**
 * Created by Maciej on 2017-01-13.
 */
@Data
public class ClassifierDTO {
    private int classifierNo;
    private int learningPerct;
    private int bestFeaturesCount;
    private int k;
}
