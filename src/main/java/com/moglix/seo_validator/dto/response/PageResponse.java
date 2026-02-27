package com.moglix.seo_validator.dto.response;

import org.jsoup.nodes.Document;

public class PageResponse {

    private int statusCode;
    private String finalUrl;
    private Document document;

    public PageResponse(int statusCode, String finalUrl, Document document) {
        this.statusCode = statusCode;
        this.finalUrl = finalUrl;
        this.document = document;
    }

    public int getStatusCode() { 
        return statusCode; 
    }

    public String getFinalUrl() { 
        return finalUrl; 
    }

    public Document getDocument() { 
        return document; 
    }
}