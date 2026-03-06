package com.moglix.seo_validator.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.seo_validator.dto.BlogComparisonResult;
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
@RequiredArgsConstructor
@Slf4j
public class SeoValidationController {

    private final SitemapParser sitemapParser;
    private final UrlMatcher urlMatcher;
    private final SeoComparator seoComparator;
    private final MatchReportGenerator matchReportGenerator;
    private final SeoReportGenerator seoReportGenerator;

    @GetMapping("/run")
    public String run(
            @RequestParam(required = true) String prodSitemap,
            @RequestParam(required = true) String uatSitemap,
            @RequestParam(required = true) String type
    ) throws Exception {

		validateUrl(prodSitemap);
		validateUrl(uatSitemap);
		log.info("Prod sitemap: {}", prodSitemap);
		log.info("UAT sitemap: {}", uatSitemap);

		// ===============================
		// STEP 1: Parse Sitemaps
		// ===============================
		List<String> prodUrls = sitemapParser.parse(prodSitemap, type);
		List<String> uatUrls = sitemapParser.parse(uatSitemap, type);

		// ===============================
        // STEP 2: Match URLs
        // ===============================

        MatchResult result = urlMatcher.match(prodUrls, uatUrls);

        log.info("Matched URLs: {}", result.getMatched().size());
        log.info("Missing in UAT: {}", result.getMissingInUat().size());
        log.info("Extra in UAT: {}", result.getExtraInUat().size());

        // Generate match report
        matchReportGenerator.generate(result);

        // ===============================
        // STEP 3: SEO Comparison (PARALLEL)
        // ===============================

        List<BlogComparisonResult> seoResults = new ArrayList<>();

        // Thread pool for parallel execution
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<BlogComparisonResult>> futures = new ArrayList<>();

        for (UrlPair pair : result.getMatched()) {

            futures.add(
                executor.submit(() -> {

                    log.info("Thread {} comparing: {}",
                            Thread.currentThread().getName(),
                            pair.getSlug());

                    return seoComparator.compare(pair);
                })
            );
        }

        // Collect results
        for (Future<BlogComparisonResult> future : futures) {
            try {
                seoResults.add(future.get());
            } catch (Exception e) {
                log.error("Error during SEO comparison", e);
            }
        }

        executor.shutdown();

        // ===============================
        // STEP 4: Generate SEO Report
        // ===============================

        seoReportGenerator.generate(seoResults);

        return "SEO Validation Completed Successfully.";
    }
    
    private void validateUrl(String url) {

        try {
            URI uri = new URI(url);

            if (uri.getScheme() == null ||
               !(uri.getScheme().equals("http") || uri.getScheme().equals("https"))) {

                throw new IllegalArgumentException("Only HTTP/HTTPS URLs are allowed");
            }

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }
}