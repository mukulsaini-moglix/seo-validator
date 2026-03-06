package com.moglix.seo_validator.dto;

import lombok.Data;

@Data
public class BlogComparisonResult {

    private String slug;

    private TechnicalComparison technical;
    private MetaComparison meta;
    private StructuredDataComparison structured;
    private HeadingComparison heading;
    private ContentComparison content;
    private ImageComparison image;
    private LinkComparison link;

    private String overallStatus;

    // getters & setters
}