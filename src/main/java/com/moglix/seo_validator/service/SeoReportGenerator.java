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
                    "Status Match",
                    "Final URL Match",
                    "Title Match",
                    "Meta Match",
                    "Canonical Match",
                    "Breadcrumb Match",
                    "Date Exists Match",
                    "Date Match",
                    "Heading Count Match",
                    "Image Count Match",
                    "Image File Match",
                    "Image ALT Match",
                    "Paragraph Match",
                    "List Match",
                    "Content Match",
                    "Schema Match",
                    "Overall Match"
            };

            writer.writeNext(header);

            for (ComparisonResult r : results) {

                boolean overallMatch =
                        r.isStatusMatch() &&
                                r.isFinalUrlMatch() &&
                                r.isTitleMatch() &&
                                r.isMetaMatch() &&
                                r.isCanonicalMatch() &&
                                r.isBreadcrumbMatch() &&
                                r.isDateExistsMatch() &&
                                r.isDateMatch() &&
                                r.isHeadingCountMatch() &&
                                r.isImageCountMatch() &&
                                r.isImageFileMatch() &&
                                r.isImageAltMatch() &&
                                r.isParagraphMatch() &&
                                r.isListMatch() &&
                                r.isContentMatch() &&
                                r.isSchemaMatch();

                writer.writeNext(new String[]{
                        r.getSlug(),
                        String.valueOf(r.isStatusMatch()),
                        String.valueOf(r.isFinalUrlMatch()),
                        String.valueOf(r.isTitleMatch()),
                        String.valueOf(r.isMetaMatch()),
                        String.valueOf(r.isCanonicalMatch()),
                        String.valueOf(r.isBreadcrumbMatch()),
                        String.valueOf(r.isDateExistsMatch()),
                        String.valueOf(r.isDateMatch()),
                        String.valueOf(r.isHeadingCountMatch()),
                        String.valueOf(r.isImageCountMatch()),
                        String.valueOf(r.isImageFileMatch()),
                        String.valueOf(r.isImageAltMatch()),
                        String.valueOf(r.isParagraphMatch()),
                        String.valueOf(r.isListMatch()),
                        String.valueOf(r.isContentMatch()),
                        String.valueOf(r.isSchemaMatch()),
                        String.valueOf(overallMatch)
                });
            }
        }

        System.out.println("SEO Comparison Report Generated Successfully.");
    }
}