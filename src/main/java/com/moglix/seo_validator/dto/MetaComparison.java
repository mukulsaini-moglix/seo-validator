package com.moglix.seo_validator.dto;

import lombok.Data;

@Data
public class MetaComparison {

    private boolean titleMatch;
    private boolean metaDescriptionMatch;
    private boolean ogTitleMatch;
    private boolean ogDescriptionMatch;
    private boolean ogImageMatch;

    private int prodTitleLength;
    private int uatTitleLength;

    private int prodDescriptionLength;
    private int uatDescriptionLength;

    private String severity;

    // getters & setters
}