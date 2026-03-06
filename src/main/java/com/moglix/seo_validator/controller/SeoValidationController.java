package com.moglix.seo_validator.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    @Value("${seo.base-url}")
    private String baseUrl;

    @GetMapping("/run")
    public Map<String, Object> run(
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

        Map<String, Object> response = new HashMap<>();

        response.put("status", "success");
        response.put("files", List.of(
                baseUrl + "/downloads/seo-comparison-report.csv",
                baseUrl + "/downloads/matched-urls.csv",
                baseUrl + "/downloads/missing-in-uat.csv",
                baseUrl + "/downloads/extra-in-uat.csv"
        ));

        return response;
    }
    
    @GetMapping("/downloads/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable String fileName) throws Exception {

        Path path = Paths.get("reports").resolve(fileName);
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .body(resource);
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