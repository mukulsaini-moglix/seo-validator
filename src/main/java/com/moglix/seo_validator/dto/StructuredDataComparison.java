package com.moglix.seo_validator.dto;

import lombok.Data;

@Data
public class StructuredDataComparison {

    private boolean schemaExistsInProd;
    private boolean schemaExistsInUat;

    private boolean headlineMatch;
    private boolean descriptionMatch;
    private boolean imageMatch;
    private boolean datePublishedMatch;
    private boolean dateModifiedMatch;
    private boolean authorMatch;

    private String severity;

    // getters & setters
}