package com.moglix.seo_validator.dto;

import lombok.Data;

@Data
public class UrlPair {

    private String slug;
    private String prodUrl;
    private String uatUrl;

    public UrlPair(String slug, String prodUrl, String uatUrl) {
        this.slug = slug;
        this.prodUrl = prodUrl;
        this.uatUrl = uatUrl;
    }

    public String getSlug() {
        return slug;
    }

    public String getProdUrl() {
        return prodUrl;
    }

    public String getUatUrl() {
        return uatUrl;
    }
}