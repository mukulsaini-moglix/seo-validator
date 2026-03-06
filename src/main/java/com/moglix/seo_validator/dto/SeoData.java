package com.moglix.seo_validator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeoData {

    // ===============================
    // Technical
    // ===============================

    private int statusCode;
    private String finalUrl;
    private String canonicalPathOnly;
    private String robotsTag;
    private String ogUrl;

    // ===============================
    // Meta
    // ===============================

    private String seoTitle;
    private String metaDescription;

    private String ogTitle;
    private String ogDescription;
    private String ogImage;

    // ===============================
    // Headings
    // ===============================

    private String h1Title;
    private int h1Count;
    private int h2Count;
    private int h3Count;

    // ===============================
    // Structured Data (Parsed)
    // ===============================

    private String schemaHeadline;
    private String schemaDescription;
    private String schemaImage;
    private String schemaDatePublished;
    private String schemaDateModified;
    private String schemaAuthor;

    // ===============================
    // Content
    // ===============================

    private String cleanedBlogContentOnly;

    // ===============================
    // Images
    // ===============================

    private String featuredImage;
    private int imageCount;
    private int missingAltCount;

    // ===============================
    // Links
    // ===============================

    private int internalLinks;
    private int externalLinks;
}