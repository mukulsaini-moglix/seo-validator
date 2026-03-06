package com.moglix.seo_validator.dto;

import lombok.Data;

@Data
public class TechnicalComparison {

    private boolean statusCodeMatch;
    private boolean finalUrlMatch;
    private boolean canonicalMatch;
    private boolean robotsMatch;
    private boolean ogUrlMatch;

    private String severity;

    // getters & setters
}