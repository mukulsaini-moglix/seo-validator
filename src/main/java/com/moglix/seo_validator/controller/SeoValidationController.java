package com.moglix.seo_validator.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.seo_validator.dto.ComparisonResult;
import com.moglix.seo_validator.dto.MatchResult;
import com.moglix.seo_validator.dto.UrlPair;
import com.moglix.seo_validator.service.MatchReportGenerator;
import com.moglix.seo_validator.service.SeoComparator;
import com.moglix.seo_validator.service.SeoReportGenerator;
import com.moglix.seo_validator.service.SitemapParser;
import com.moglix.seo_validator.service.UrlMatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/seo")
@RequiredArgsConstructor
@Slf4j
public class SeoValidationController {

    private final SitemapParser sitemapParser;
    private final UrlMatcher urlMatcher;
    private final SeoComparator seoComparator;
    private final MatchReportGenerator matchReportGenerator;
    private final SeoReportGenerator seoReportGenerator;

    @GetMapping("/run")
    public String run() throws Exception {

        String prodSitemap = "https://business.moglix.com/sitemap_blogs.xml";
        String uatSitemap  = "https://uat.business.moglilabs.com/sitemap_blogs.xml";

        List<String> prodUrls = sitemapParser.parse(prodSitemap);
        List<String> uatUrls  = sitemapParser.parse(uatSitemap);

        MatchResult result = urlMatcher.match(prodUrls, uatUrls);

        System.out.println("Matched URLs: " + result.getMatched().size());
        System.out.println("Missing in UAT: " + result.getMissingInUat().size());
        System.out.println("Extra in UAT: " + result.getExtraInUat().size());

        matchReportGenerator.generate(result);

        List<ComparisonResult> seoResults = new ArrayList<>();

        for (UrlPair pair : result.getMatched()) {

            System.out.println("Comparing: " + pair.getSlug());

            ComparisonResult comparison = seoComparator.compare(pair);

            seoResults.add(comparison);
        }

        seoReportGenerator.generate(seoResults);

        return "SEO Validation Completed Successfully.";
    }
}