package com.moglix.seo_validator.dto;


import java.util.List;

public class MatchResult {

    private List<UrlPair> matched;
    private List<String> missingInUat;
    private List<String> extraInUat;

    public MatchResult(List<UrlPair> matched,
                       List<String> missingInUat,
                       List<String> extraInUat) {
        this.matched = matched;
        this.missingInUat = missingInUat;
        this.extraInUat = extraInUat;
    }

    public List<UrlPair> getMatched() {
        return matched;
    }

    public List<String> getMissingInUat() {
        return missingInUat;
    }

    public List<String> getExtraInUat() {
        return extraInUat;
    }
}