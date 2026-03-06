package com.moglix.seo_validator.dto;

import lombok.Data;

@Data
public class ContentComparison {

    private int prodWordCount;
    private int uatWordCount;

    private double wordDifferencePercent;

    private double similarityPercent;

    private boolean similarityAbove95;

    private String severity;

    // getters & setters
}