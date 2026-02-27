package com.moglix.seo_validator.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class SitemapParser {

    public List<String> parse(String sitemapUrl) throws IOException {

        System.out.println("Fetching sitemap: " + sitemapUrl);

        Document doc = Jsoup.connect(sitemapUrl)
                .ignoreContentType(true)
                .get();

        Elements locElements = doc.select("loc");

        List<String> blogUrls = new ArrayList<>();

        for (Element loc : locElements) {
            String url = loc.text();

            if (url.contains("/blogs/") && !url.endsWith("/blogs")) {
                blogUrls.add(url);
            }
        }

        return blogUrls;
    }
}