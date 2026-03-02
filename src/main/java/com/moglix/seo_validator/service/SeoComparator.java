package com.moglix.seo_validator.service;

import java.util.Objects;

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

        boolean titleMatch = normalize(prod.getSeoTitle())
                .equals(normalize(uat.getSeoTitle()));

        boolean metaMatch = normalize(prod.getMetaDescription())
                .equals(normalize(uat.getMetaDescription()));

        boolean canonicalMatch = Objects.equals(
                prod.getCanonicalPathOnly(),
                uat.getCanonicalPathOnly()
        );

        boolean robotsMatch = normalize(prod.getRobotsTag())
                .equals(normalize(uat.getRobotsTag()));

        boolean h1Match = normalize(prod.getH1Title())
                .equals(normalize(uat.getH1Title()));

        boolean schemaMatch = normalize(prod.getArticleSchemaJson())
                .equals(normalize(uat.getArticleSchemaJson()));

        boolean contentSimilarityAbove95Percent =
                calculateSimilarity(
                        prod.getCleanedBlogContentOnly(),
                        uat.getCleanedBlogContentOnly()
                ) >= 95.0;

        return new ComparisonResult(
                pair.getSlug(),
                titleMatch,
                metaMatch,
                canonicalMatch,
                robotsMatch,
                h1Match,
                schemaMatch,
                contentSimilarityAbove95Percent
        );
    }

    // ===============================
    // Helpers
    // ===============================

    private String normalize(String value) {
        if (value == null) return "";
        return value
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();
    }

    private double calculateSimilarity(String s1, String s2) {

        s1 = normalize(s1);
        s2 = normalize(s2);

        if (s1.isEmpty() && s2.isEmpty()) return 100.0;

        int maxLength = Math.max(s1.length(), s2.length());
        int distance = levenshteinDistance(s1, s2);

        return ((double) (maxLength - distance) / maxLength) * 100;
    }

    private int levenshteinDistance(String s1, String s2) {

        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++)
            dp[i][0] = i;

        for (int j = 0; j <= s2.length(); j++)
            dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {

                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[s1.length()][s2.length()];
    }
}