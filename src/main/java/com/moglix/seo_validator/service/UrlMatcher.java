package com.moglix.seo_validator.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.moglix.seo_validator.dto.MatchResult;
import com.moglix.seo_validator.dto.UrlPair;

@Service
public class UrlMatcher {

    public MatchResult match(List<String> prodUrls, List<String> uatUrls) {

        Map<String, String> prodMap = mapBySlug(prodUrls);
        Map<String, String> uatMap  = mapBySlug(uatUrls);

        List<UrlPair> matched = new ArrayList<>();
        List<String> missingInUat = new ArrayList<>();
        List<String> extraInUat = new ArrayList<>();

        for (String slug : prodMap.keySet()) {
            if (uatMap.containsKey(slug)) {
                matched.add(new UrlPair(slug, prodMap.get(slug), uatMap.get(slug)));
            } else {
                missingInUat.add(prodMap.get(slug));
            }
        }

        for (String slug : uatMap.keySet()) {
            if (!prodMap.containsKey(slug)) {
                extraInUat.add(uatMap.get(slug));
            }
        }

        return new MatchResult(matched, missingInUat, extraInUat);
    }

    private Map<String, String> mapBySlug(List<String> urls) {

        Map<String, String> map = new HashMap<>();

        for (String url : urls) {
            String slug = url.substring(url.lastIndexOf("/") + 1);
            map.put(slug, url);
        }

        return map;
    }
}