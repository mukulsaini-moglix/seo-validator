package com.moglix.seo_validator.service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import com.moglix.seo_validator.dto.MatchResult;
import com.moglix.seo_validator.dto.UrlPair;
import com.opencsv.CSVWriter;

@Service
public class MatchReportGenerator {
	
	private static final String REPORT_DIR = "reports";

    public void generate(MatchResult result) throws IOException {
    	
    	  ensureReportDir();
    	  
        writeMatched(result);
        writeMissing(result);
        writeExtra(result);

        System.out.println("CSV Reports Generated Successfully.");
    }

    private void writeMatched(MatchResult result) throws IOException {

        try (CSVWriter writer = new CSVWriter(new FileWriter("reports/matched-urls.csv"))) {

            writer.writeNext(new String[]{"Slug", "Prod URL", "UAT URL"});

            for (UrlPair pair : result.getMatched()) {
                writer.writeNext(new String[]{
                        pair.getSlug(),
                        pair.getProdUrl(),
                        pair.getUatUrl()
                });
            }
        }
    }

    private void writeMissing(MatchResult result) throws IOException {

        try (CSVWriter writer = new CSVWriter(new FileWriter("reports/missing-in-uat.csv"))) {

            writer.writeNext(new String[]{"Prod URL Missing in UAT"});

            for (String url : result.getMissingInUat()) {
                writer.writeNext(new String[]{url});
            }
        }
    }

    private void writeExtra(MatchResult result) throws IOException {

        try (CSVWriter writer = new CSVWriter(new FileWriter("reports/extra-in-uat.csv"))) {

            writer.writeNext(new String[]{"Extra URL in UAT"});

            for (String url : result.getExtraInUat()) {
                writer.writeNext(new String[]{url});
            }
        }
    }
    
    private void ensureReportDir() throws IOException {
        Path path = Paths.get(REPORT_DIR);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}