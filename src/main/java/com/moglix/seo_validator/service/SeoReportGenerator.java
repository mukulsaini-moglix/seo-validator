package com.moglix.seo_validator.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.moglix.seo_validator.dto.BlogComparisonResult;
import com.moglix.seo_validator.dto.ContentComparison;
import com.moglix.seo_validator.dto.HeadingComparison;
import com.moglix.seo_validator.dto.ImageComparison;
import com.moglix.seo_validator.dto.LinkComparison;
import com.moglix.seo_validator.dto.MetaComparison;
import com.moglix.seo_validator.dto.StructuredDataComparison;
import com.moglix.seo_validator.dto.TechnicalComparison;
import com.opencsv.CSVWriter;

@Service
public class SeoReportGenerator {

    public void generate(List<BlogComparisonResult> results) throws IOException {

        try (CSVWriter writer =
                     new CSVWriter(new FileWriter("seo-comparison-report.csv"))) {

            // ===============================
            // HEADER
            // ===============================

            String[] header = {

                    // Basic
                    "Slug",
                    "Overall Status",

                    // Technical
                    "Status Code Match",
                    "Canonical Match",
                    "Robots Match",
                    "OG URL Match",
                    "Technical Severity",

                    // Meta
                    "Title Match",
                    "Meta Description Match",
                    "OG Title Match",
                    "OG Description Match",
                    "OG Image Match",
                    "Title Length (Prod)",
                    "Title Length (UAT)",
                    "Meta Length (Prod)",
                    "Meta Length (UAT)",
                    "Meta Severity",

                    // Structured
                    "Schema Exists (Prod)",
                    "Schema Exists (UAT)",
                    "Schema Headline Match",
                    "Schema Description Match",
                    "Schema Image Match",
                    "Date Published Match",
                    "Date Modified Match",
                    "Author Match",
                    "Structured Severity",

                    // Headings
                    "H1 Match",
                    "H1 Count Valid",
                    "H2 Count (Prod)",
                    "H2 Count (UAT)",
                    "H3 Count (Prod)",
                    "H3 Count (UAT)",
                    "Heading Severity",

                    // Content
                    "Word Count (Prod)",
                    "Word Count (UAT)",
                    "Word Difference %",
                    "Similarity %",
                    "Similarity >= 95",
                    "Content Severity",

                    // Images
                    "Featured Image Match",
                    "Image Count (Prod)",
                    "Image Count (UAT)",
                    "Missing Alt (Prod)",
                    "Missing Alt (UAT)",
                    "Image Severity",

                    // Links
                    "Internal Links (Prod)",
                    "Internal Links (UAT)",
                    "External Links (Prod)",
                    "External Links (UAT)",
                    "Link Severity"
            };

            writer.writeNext(header);

            // ===============================
            // ROWS
            // ===============================

            for (BlogComparisonResult r : results) {

                TechnicalComparison t = r.getTechnical();
                MetaComparison m = r.getMeta();
                StructuredDataComparison s = r.getStructured();
                HeadingComparison h = r.getHeading();
                ContentComparison c = r.getContent();
                ImageComparison i = r.getImage();
                LinkComparison l = r.getLink();

                writer.writeNext(new String[]{

                        // Basic
                        r.getSlug(),
                        r.getOverallStatus(),

                        // Technical
                        bool(t.isStatusCodeMatch()),
                        bool(t.isCanonicalMatch()),
                        bool(t.isRobotsMatch()),
                        bool(t.isOgUrlMatch()),
                        t.getSeverity(),

                        // Meta
                        bool(m.isTitleMatch()),
                        bool(m.isMetaDescriptionMatch()),
                        bool(m.isOgTitleMatch()),
                        bool(m.isOgDescriptionMatch()),
                        bool(m.isOgImageMatch()),
                        String.valueOf(m.getProdTitleLength()),
                        String.valueOf(m.getUatTitleLength()),
                        String.valueOf(m.getProdDescriptionLength()),
                        String.valueOf(m.getUatDescriptionLength()),
                        m.getSeverity(),

                        // Structured
                        bool(s.isSchemaExistsInProd()),
                        bool(s.isSchemaExistsInUat()),
                        bool(s.isHeadlineMatch()),
                        bool(s.isDescriptionMatch()),
                        bool(s.isImageMatch()),
                        bool(s.isDatePublishedMatch()),
                        bool(s.isDateModifiedMatch()),
                        bool(s.isAuthorMatch()),
                        s.getSeverity(),

                        // Heading
                        bool(h.isH1TextMatch()),
                        bool(h.isH1CountValid()),
                        String.valueOf(h.getProdH2Count()),
                        String.valueOf(h.getUatH2Count()),
                        String.valueOf(h.getProdH3Count()),
                        String.valueOf(h.getUatH3Count()),
                        h.getSeverity(),

                        // Content
                        String.valueOf(c.getProdWordCount()),
                        String.valueOf(c.getUatWordCount()),
                        format(c.getWordDifferencePercent()),
                        format(c.getSimilarityPercent()),
                        bool(c.isSimilarityAbove95()),
                        c.getSeverity(),

                        // Images
                        bool(i.isFeaturedImageMatch()),
                        String.valueOf(i.getProdImageCount()),
                        String.valueOf(i.getUatImageCount()),
                        String.valueOf(i.getProdMissingAltCount()),
                        String.valueOf(i.getUatMissingAltCount()),
                        i.getSeverity(),

                        // Links
                        String.valueOf(l.getProdInternalLinks()),
                        String.valueOf(l.getUatInternalLinks()),
                        String.valueOf(l.getProdExternalLinks()),
                        String.valueOf(l.getUatExternalLinks()),
                        l.getSeverity()
                });
            }
        }

        System.out.println("SEO Comparison Report Generated Successfully.");
    }

    // ===============================
    // Helpers
    // ===============================

    private String bool(boolean value) {
        return value ? "PASS" : "FAIL";
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }
}