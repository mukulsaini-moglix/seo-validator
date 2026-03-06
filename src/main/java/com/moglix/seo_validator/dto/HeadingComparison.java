package com.moglix.seo_validator.dto;

import lombok.Data;

@Data
public class HeadingComparison {

    private boolean h1TextMatch;
    private boolean h1CountValid;

    private int prodH2Count;
    private int uatH2Count;

    private int prodH3Count;
    private int uatH3Count;

    private String severity;

    // getters & setters
}