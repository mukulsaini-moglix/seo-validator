package com.moglix.seo_validator.service;

import java.nio.file.Files;
import java.nio.file.Path;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.moglix.seo_validator.dto.ComparisonResult;
import com.moglix.seo_validator.dto.SeoData;
import com.moglix.seo_validator.dto.UrlPair;
import com.moglix.seo_validator.dto.response.PageResponse;

@Service
public class SeoComparator {

    private final PageFetcher pageFetcher;
    private final SeoExtractor seoExtractor;

    public SeoComparator(PageFetcher pageFetcher,
                         SeoExtractor seoExtractor) {
        this.pageFetcher = pageFetcher;
        this.seoExtractor = seoExtractor;
    }

    public ComparisonResult compare(UrlPair pair) throws Exception {

        PageResponse prodRes = pageFetcher.fetch(pair.getProdUrl());
        PageResponse uatRes  = pageFetcher.fetch(pair.getUatUrl());

        SeoData prod = seoExtractor.extract(
                prodRes.getStatusCode(),
                prodRes.getFinalUrl(),
                prodRes.getDocument()
        );

        SeoData uat = seoExtractor.extract(
                uatRes.getStatusCode(),
                uatRes.getFinalUrl(),
                uatRes.getDocument()
        );

        // 🔥 Extract actual visible H1 from page
        Element prodH1 = prodRes.getDocument().selectFirst("main h1, h1");
        Element uatH1  = uatRes.getDocument().selectFirst("main h1, h1");

        String prodPageTitle = prodH1 != null ? prodH1.text().trim() : "";
        String uatPageTitle  = uatH1 != null ? uatH1.text().trim() : "";

        // DEBUG ONLY FOR SPECIFIC SLUG
        if ("common-types-of-safety-audits-what-are-they"
                .equals(pair.getSlug())) {

            System.out.println("===== DEBUG FOR SLUG =====");

            System.out.println("PROD FINAL URL: " + prod.getFinalUrl());
            System.out.println("UAT FINAL URL:  " + uat.getFinalUrl());

            // Save complete HTML to files
            Files.writeString(
                    Path.of("prod-common-types.html"),
                    prodRes.getDocument().outerHtml()
            );

            Files.writeString(
                    Path.of("uat-common-types.html"),
                    uatRes.getDocument().outerHtml()
            );

            System.out.println("HTML files saved:");
            System.out.println("prod-common-types.html");
            System.out.println("uat-common-types.html");

            System.out.println("===== END DEBUG =====");
        }

        return new ComparisonResult(
                pair.getSlug(),
                prod.getStatusCode() == uat.getStatusCode(),
                prod.getFinalUrl().equals(uat.getFinalUrl()),
                prodPageTitle.equals(uatPageTitle),   // ✅ Changed Here
                prod.getMetaDescription().equals(uat.getMetaDescription()),
                prod.getCanonical().equals(uat.getCanonical()),
                prod.isBreadcrumbExists() == uat.isBreadcrumbExists(),
                prod.isDateExists() == uat.isDateExists(),
                prod.getPublishedDate().equals(uat.getPublishedDate()),
                prod.getH1Count() == uat.getH1Count()
                        && prod.getH2Count() == uat.getH2Count()
                        && prod.getH3Count() == uat.getH3Count(),
                prod.getImageFileNames().size() == uat.getImageFileNames().size(),
                prod.getImageFileNames().equals(uat.getImageFileNames()),
                prod.getImageAltTexts().equals(uat.getImageAltTexts()),
                prod.getParagraphCount() == uat.getParagraphCount(),
                prod.getListItemCount() == uat.getListItemCount(),
                prod.getContentText().equals(uat.getContentText()),
                prod.isHasSchema() == uat.isHasSchema()
        );
    }
}