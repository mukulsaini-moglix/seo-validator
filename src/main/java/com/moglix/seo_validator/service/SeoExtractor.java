package com.moglix.seo_validator.service;


import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.moglix.seo_validator.dto.SeoData;

@Service
public class SeoExtractor {

    public SeoData extract(int statusCode, String finalUrl, Document doc) {

        Element blog = doc.selectFirst("main");

        if (blog == null) {
            blog = doc.body();
        }

        blog.select("script, style, nav, footer").remove();

        String title = doc.title().trim();
        String metaDescription = doc.select("meta[name=description]").attr("content").trim();
        String canonical = doc.select("link[rel=canonical]").attr("abs:href").trim();
        String robots = doc.select("meta[name=robots]").attr("content").trim();

        boolean breadcrumbExists = !blog.select(".breadcrumb, nav[aria-label=breadcrumb]").isEmpty();

        Element dateElement = blog.selectFirst("time, .date, .published-date");
        boolean dateExists = dateElement != null;
        String publishedDate = dateExists ? dateElement.text().trim().toLowerCase() : "";

        int h1Count = blog.select("h1").size();
        int h2Count = blog.select("h2").size();
        int h3Count = blog.select("h3").size();

        int paragraphCount = blog.select("p").size();
        int ulCount = blog.select("ul").size();
        int olCount = blog.select("ol").size();
        int liCount = blog.select("li").size();

        Elements images = blog.select("img");
        List<String> imageFileNames = new ArrayList<>();
        List<String> imageAltTexts = new ArrayList<>();

        for (Element img : images) {
            String src = img.attr("abs:src");
            imageFileNames.add(getFileName(src));
            imageAltTexts.add(img.attr("alt").toLowerCase().trim());
        }

        int internalLinks = blog.select("a[href^=/]").size();

        boolean hasSchema = !doc.select("script[type=application/ld+json]").isEmpty();

        String contentText = blog.text()
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();

        return new SeoData(
                statusCode,
                finalUrl,
                title,
                metaDescription,
                canonical,
                robots,
                breadcrumbExists,
                dateExists,
                publishedDate,
                h1Count,
                h2Count,
                h3Count,
                paragraphCount,
                ulCount,
                olCount,
                liCount,
                imageFileNames,
                imageAltTexts,
                internalLinks,
                hasSchema,
                contentText
        );
    }

    private String getFileName(String url) {
        if (url == null || url.isEmpty()) return "";
        int index = url.lastIndexOf("/");
        return index != -1 ? url.substring(index + 1).toLowerCase() : url.toLowerCase();
    }
}
