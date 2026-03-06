package com.moglix.seo_validator.dto;

import lombok.Data;

@Data
public class ImageComparison {

    private boolean featuredImageMatch;

    private int prodImageCount;
    private int uatImageCount;

    private int prodMissingAltCount;
    private int uatMissingAltCount;

    private String severity;

    // getters & setters
}