package com.moglix.seo_validator.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeoData {

    private String seoTitle;
    private String h1Title;
    private String metaDescription;
    private String canonicalPathOnly;
    private String robotsTag;              // <-- THIS ONE
    private String publishedDate;
    private String articleSchemaJson;
    private String featureImage;
    private String cleanedBlogContentOnly;
}