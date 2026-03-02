package com.moglix.seo_validator.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.moglix.seo_validator.dto.ComparisonResult;
import com.opencsv.CSVWriter;

@Service
public class SeoReportGenerator {

    public void generate(List<ComparisonResult> results) throws IOException {

        try (CSVWriter writer = new CSVWriter(new FileWriter("seo-comparison-report.csv"))) {

            String[] header = {
                    "Slug",
                    "Title Match",
                    "Meta Match",
                    "Canonical Match",
                    "Robots Match",
                    "H1 Match",
                    "Schema Match",
                    "Content Similarity > 95%",
                    "Overall Match"
            };

            writer.writeNext(header);

            for (ComparisonResult r : results) {

                boolean overallMatch =
                        r.isTitleMatch() &&
                        r.isMetaMatch() &&
                        r.isCanonicalMatch() &&
                        r.isRobotsMatch() &&
                        r.isH1Match() &&
                        r.isSchemaMatch() &&
                        r.isContentSimilarityAbove95Percent();

                writer.writeNext(new String[]{
                        r.getSlug(),
                        String.valueOf(r.isTitleMatch()),
                        String.valueOf(r.isMetaMatch()),
                        String.valueOf(r.isCanonicalMatch()),
                        String.valueOf(r.isRobotsMatch()),
                        String.valueOf(r.isH1Match()),
                        String.valueOf(r.isSchemaMatch()),
                        String.valueOf(r.isContentSimilarityAbove95Percent()),
                        String.valueOf(overallMatch)
                });
            }
        }

        System.out.println("SEO Comparison Report Generated Successfully.");
    }
}