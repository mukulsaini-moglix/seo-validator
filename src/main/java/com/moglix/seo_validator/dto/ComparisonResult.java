package com.moglix.seo_validator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComparisonResult {

    private String slug;

    private boolean titleMatch;
    private boolean metaMatch;
    private boolean canonicalMatch;
    private boolean robotsMatch;
    private boolean h1Match;
    private boolean schemaMatch;
    private boolean contentSimilarityAbove95Percent;

}