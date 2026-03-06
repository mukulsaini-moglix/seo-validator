package com.moglix.seo_validator.service;

import org.springframework.stereotype.Service;

import com.moglix.seo_validator.dto.BlogComparisonResult;
import com.moglix.seo_validator.dto.ContentComparison;
import com.moglix.seo_validator.dto.HeadingComparison;
import com.moglix.seo_validator.dto.ImageComparison;
import com.moglix.seo_validator.dto.LinkComparison;
import com.moglix.seo_validator.dto.MetaComparison;
import com.moglix.seo_validator.dto.SeoData;
import com.moglix.seo_validator.dto.StructuredDataComparison;
import com.moglix.seo_validator.dto.TechnicalComparison;
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

    public BlogComparisonResult compare(UrlPair pair) throws Exception {

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

        BlogComparisonResult result = new BlogComparisonResult();
        result.setSlug(pair.getSlug());

        result.setTechnical(compareTechnical(prod, uat));
        result.setMeta(compareMeta(prod, uat));
        result.setStructured(compareStructured(prod, uat));
        result.setHeading(compareHeading(prod, uat));
        result.setContent(compareContent(prod, uat));
        result.setImage(compareImages(prod, uat));
        result.setLink(compareLinks(prod, uat));

        result.setOverallStatus(calculateOverallStatus(result));

        return result;
    }

    // ===================================================
    // TECHNICAL
    // ===================================================

    private TechnicalComparison compareTechnical(SeoData prod, SeoData uat) {

        TechnicalComparison t = new TechnicalComparison();

        t.setStatusCodeMatch(prod.getStatusCode() == uat.getStatusCode());
        t.setFinalUrlMatch(normalize(prod.getFinalUrl())
                .equals(normalize(uat.getFinalUrl())));

        t.setCanonicalMatch(
                normalize(prod.getCanonicalPathOnly())
                        .equals(normalize(uat.getCanonicalPathOnly()))
        );

        t.setRobotsMatch(
                normalize(prod.getRobotsTag())
                        .equals(normalize(uat.getRobotsTag()))
        );

        t.setOgUrlMatch(
                normalize(prod.getOgUrl())
                        .equals(normalize(uat.getOgUrl()))
        );

        t.setSeverity(
                (!t.isStatusCodeMatch() || !t.isCanonicalMatch())
                        ? "CRITICAL"
                        : "OK"
        );

        return t;
    }

    // ===================================================
    // META
    // ===================================================

    private MetaComparison compareMeta(SeoData prod, SeoData uat) {

        MetaComparison m = new MetaComparison();

        m.setTitleMatch(
                normalize(prod.getSeoTitle())
                        .equals(normalize(uat.getSeoTitle()))
        );

        m.setMetaDescriptionMatch(
                normalize(prod.getMetaDescription())
                        .equals(normalize(uat.getMetaDescription()))
        );

        m.setOgTitleMatch(
                normalize(prod.getOgTitle())
                        .equals(normalize(uat.getOgTitle()))
        );

        m.setOgDescriptionMatch(
                normalize(prod.getOgDescription())
                        .equals(normalize(uat.getOgDescription()))
        );

        m.setOgImageMatch(
                normalize(prod.getOgImage())
                        .equals(normalize(uat.getOgImage()))
        );

        m.setProdTitleLength(length(prod.getSeoTitle()));
        m.setUatTitleLength(length(uat.getSeoTitle()));

        m.setProdDescriptionLength(length(prod.getMetaDescription()));
        m.setUatDescriptionLength(length(uat.getMetaDescription()));

        m.setSeverity(
                (!m.isTitleMatch() || !m.isMetaDescriptionMatch())
                        ? "CRITICAL"
                        : "OK"
        );

        return m;
    }

    // ===================================================
    // STRUCTURED DATA
    // ===================================================

    private StructuredDataComparison compareStructured(SeoData prod, SeoData uat) {

        StructuredDataComparison s = new StructuredDataComparison();

        // Schema existence based on parsed headline
        s.setSchemaExistsInProd(notEmpty(prod.getSchemaHeadline()));
        s.setSchemaExistsInUat(notEmpty(uat.getSchemaHeadline()));

        s.setHeadlineMatch(
                normalize(prod.getSchemaHeadline())
                        .equals(normalize(uat.getSchemaHeadline()))
        );

        s.setDescriptionMatch(
                normalize(prod.getSchemaDescription())
                        .equals(normalize(uat.getSchemaDescription()))
        );

        s.setImageMatch(
                normalize(prod.getSchemaImage())
                        .equals(normalize(uat.getSchemaImage()))
        );

        s.setDatePublishedMatch(
                normalize(prod.getSchemaDatePublished())
                        .equals(normalize(uat.getSchemaDatePublished()))
        );

        s.setDateModifiedMatch(
                normalize(prod.getSchemaDateModified())
                        .equals(normalize(uat.getSchemaDateModified()))
        );

        s.setAuthorMatch(
                normalize(prod.getSchemaAuthor())
                        .equals(normalize(uat.getSchemaAuthor()))
        );

        if (!s.isHeadlineMatch() || !s.isSchemaExistsInUat()) {
            s.setSeverity("CRITICAL");
        } else {
            s.setSeverity("OK");
        }

        return s;
    }
    
    private boolean notEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // ===================================================
    // HEADING
    // ===================================================

    private HeadingComparison compareHeading(SeoData prod, SeoData uat) {

        HeadingComparison h = new HeadingComparison();

        h.setH1TextMatch(
                normalize(prod.getH1Title())
                        .equals(normalize(uat.getH1Title()))
        );

        h.setH1CountValid(uat.getH1Count() == 1);

        h.setProdH2Count(prod.getH2Count());
        h.setUatH2Count(uat.getH2Count());

        h.setProdH3Count(prod.getH3Count());
        h.setUatH3Count(uat.getH3Count());

        h.setSeverity(!h.isH1TextMatch() ? "CRITICAL" : "OK");

        return h;
    }

    // ===================================================
    // CONTENT
    // ===================================================

    private ContentComparison compareContent(SeoData prod, SeoData uat) {

        ContentComparison c = new ContentComparison();

        String prodText = normalize(prod.getCleanedBlogContentOnly());
        String uatText  = normalize(uat.getCleanedBlogContentOnly());

        int prodWords = wordCount(prodText);
        int uatWords  = wordCount(uatText);

        c.setProdWordCount(prodWords);
        c.setUatWordCount(uatWords);

        double diffPercent = Math.abs(prodWords - uatWords) * 100.0 / prodWords;
        c.setWordDifferencePercent(diffPercent);

        double similarity = calculateSimilarity(prodText, uatText);
        c.setSimilarityPercent(similarity);
        c.setSimilarityAbove95(similarity >= 95.0);

        if (similarity < 95 || diffPercent > 10) {
            c.setSeverity("CRITICAL");
        } else {
            c.setSeverity("OK");
        }

        return c;
    }

    // ===================================================
    // IMAGES
    // ===================================================

    private ImageComparison compareImages(SeoData prod, SeoData uat) {

        ImageComparison i = new ImageComparison();

        i.setFeaturedImageMatch(
                normalize(prod.getFeaturedImage())
                        .equals(normalize(uat.getFeaturedImage()))
        );

        i.setProdImageCount(prod.getImageCount());
        i.setUatImageCount(uat.getImageCount());

        i.setProdMissingAltCount(prod.getMissingAltCount());
        i.setUatMissingAltCount(uat.getMissingAltCount());

        i.setSeverity(!i.isFeaturedImageMatch() ? "WARNING" : "OK");

        return i;
    }

    // ===================================================
    // LINKS
    // ===================================================

    private LinkComparison compareLinks(SeoData prod, SeoData uat) {

        LinkComparison l = new LinkComparison();

        l.setProdInternalLinks(prod.getInternalLinks());
        l.setUatInternalLinks(uat.getInternalLinks());

        l.setProdExternalLinks(prod.getExternalLinks());
        l.setUatExternalLinks(uat.getExternalLinks());

        l.setSeverity("OK");

        return l;
    }

    // ===================================================
    // OVERALL STATUS
    // ===================================================

    private String calculateOverallStatus(BlogComparisonResult r) {

        if ("CRITICAL".equals(r.getTechnical().getSeverity()) ||
                "CRITICAL".equals(r.getMeta().getSeverity()) ||
                "CRITICAL".equals(r.getStructured().getSeverity()) ||
                "CRITICAL".equals(r.getHeading().getSeverity()) ||
                "CRITICAL".equals(r.getContent().getSeverity())) {

            return "FAIL";
        }

        return "PASS";
    }

    // ===================================================
    // HELPERS
    // ===================================================

    private String normalize(String value) {
        if (value == null) return "";
        return value.replaceAll("\\s+", " ").trim().toLowerCase();
    }

    private int wordCount(String text) {
        if (text == null || text.isEmpty()) return 0;
        return text.split("\\s+").length;
    }

    private int length(String value) {
        return value == null ? 0 : value.length();
    }

    private double calculateSimilarity(String s1, String s2) {

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