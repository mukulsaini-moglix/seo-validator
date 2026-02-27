package com.moglix.seo_validator.dto;


import java.util.List;

import lombok.Data;

@Data
public class SeoData {

    private int statusCode;
    private String finalUrl;

    private String title;
    private String metaDescription;
    private String canonical;
    private String robots;

    private boolean breadcrumbExists;
    private boolean dateExists;
    private String publishedDate;

    private int h1Count;
    private int h2Count;
    private int h3Count;

    private int paragraphCount;
    private int ulCount;
    private int olCount;
    private int listItemCount;

    private List<String> imageFileNames;
    private List<String> imageAltTexts;

    private int internalLinkCount;
    private boolean hasSchema;

    private String contentText;

    public SeoData(
            int statusCode,
            String finalUrl,
            String title,
            String metaDescription,
            String canonical,
            String robots,
            boolean breadcrumbExists,
            boolean dateExists,
            String publishedDate,
            int h1Count,
            int h2Count,
            int h3Count,
            int paragraphCount,
            int ulCount,
            int olCount,
            int listItemCount,
            List<String> imageFileNames,
            List<String> imageAltTexts,
            int internalLinkCount,
            boolean hasSchema,
            String contentText
    ) {
        this.statusCode = statusCode;
        this.finalUrl = finalUrl;
        this.title = title;
        this.metaDescription = metaDescription;
        this.canonical = canonical;
        this.robots = robots;
        this.breadcrumbExists = breadcrumbExists;
        this.dateExists = dateExists;
        this.publishedDate = publishedDate;
        this.h1Count = h1Count;
        this.h2Count = h2Count;
        this.h3Count = h3Count;
        this.paragraphCount = paragraphCount;
        this.ulCount = ulCount;
        this.olCount = olCount;
        this.listItemCount = listItemCount;
        this.imageFileNames = imageFileNames;
        this.imageAltTexts = imageAltTexts;
        this.internalLinkCount = internalLinkCount;
        this.hasSchema = hasSchema;
        this.contentText = contentText;
    }

    public int getStatusCode() { return statusCode; }
    public String getFinalUrl() { return finalUrl; }
    public String getTitle() { return title; }
    public String getMetaDescription() { return metaDescription; }
    public String getCanonical() { return canonical; }
    public String getRobots() { return robots; }
    public boolean isBreadcrumbExists() { return breadcrumbExists; }
    public boolean isDateExists() { return dateExists; }
    public String getPublishedDate() { return publishedDate; }
    public int getH1Count() { return h1Count; }
    public int getH2Count() { return h2Count; }
    public int getH3Count() { return h3Count; }
    public int getParagraphCount() { return paragraphCount; }
    public int getUlCount() { return ulCount; }
    public int getOlCount() { return olCount; }
    public int getListItemCount() { return listItemCount; }
    public List<String> getImageFileNames() { return imageFileNames; }
    public List<String> getImageAltTexts() { return imageAltTexts; }
    public int getInternalLinkCount() { return internalLinkCount; }
    public boolean isHasSchema() { return hasSchema; }
    public String getContentText() { return contentText; }
}