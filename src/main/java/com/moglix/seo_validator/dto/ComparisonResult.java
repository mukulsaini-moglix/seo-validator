package com.moglix.seo_validator.dto;

import lombok.Data;

@Data
public class ComparisonResult {

    private String slug;
    private boolean statusMatch;
    private boolean finalUrlMatch;
    private boolean titleMatch;
    private boolean metaMatch;
    private boolean canonicalMatch;
    private boolean breadcrumbMatch;
    private boolean dateExistsMatch;
    private boolean dateMatch;
    private boolean headingCountMatch;
    private boolean imageCountMatch;
    private boolean imageFileMatch;
    private boolean imageAltMatch;
    private boolean paragraphMatch;
    private boolean listMatch;
    private boolean contentMatch;
    private boolean schemaMatch;

    public ComparisonResult(
            String slug,
            boolean statusMatch,
            boolean finalUrlMatch,
            boolean titleMatch,
            boolean metaMatch,
            boolean canonicalMatch,
            boolean breadcrumbMatch,
            boolean dateExistsMatch,
            boolean dateMatch,
            boolean headingCountMatch,
            boolean imageCountMatch,
            boolean imageFileMatch,
            boolean imageAltMatch,
            boolean paragraphMatch,
            boolean listMatch,
            boolean contentMatch,
            boolean schemaMatch
    ) {
        this.slug = slug;
        this.statusMatch = statusMatch;
        this.finalUrlMatch = finalUrlMatch;
        this.titleMatch = titleMatch;
        this.metaMatch = metaMatch;
        this.canonicalMatch = canonicalMatch;
        this.breadcrumbMatch = breadcrumbMatch;
        this.dateExistsMatch = dateExistsMatch;
        this.dateMatch = dateMatch;
        this.headingCountMatch = headingCountMatch;
        this.imageCountMatch = imageCountMatch;
        this.imageFileMatch = imageFileMatch;
        this.imageAltMatch = imageAltMatch;
        this.paragraphMatch = paragraphMatch;
        this.listMatch = listMatch;
        this.contentMatch = contentMatch;
        this.schemaMatch = schemaMatch;
    }
}