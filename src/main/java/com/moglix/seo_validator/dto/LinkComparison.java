package com.moglix.seo_validator.dto;

import lombok.Data;

@Data
public class LinkComparison {

    private int prodInternalLinks;
    private int uatInternalLinks;

    private int prodExternalLinks;
    private int uatExternalLinks;

    private String severity;

    // getters & setters
}
