package com.moglix.seo_validator.service;

import java.net.URI;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.moglix.seo_validator.dto.SeoData;

@Service
public class SeoExtractor {

    public SeoData extract(int statusCode, String finalUrl, Document doc) {

        // 1️⃣ SEO TITLE
        String seoTitle = doc.title().trim();

        // 2️⃣ H1
        String h1Title = "";
        Element h1 = doc.selectFirst("main h1");
        if (h1 != null) {
            h1Title = h1.text().trim();
        }

        // 3️⃣ META DESCRIPTION
        String metaDescription =
                doc.select("meta[name=description]")
                        .attr("content")
                        .trim();

        // 4️⃣ CANONICAL PATH ONLY
        String canonicalPathOnly = "";
        String canonicalFull =
                doc.select("link[rel=canonical]")
                        .attr("abs:href")
                        .trim();

        if (!canonicalFull.isEmpty()) {
            try {
                canonicalPathOnly = new URI(canonicalFull).getPath();
            } catch (Exception ignored) {}
        }

        // 5️⃣ ROBOTS
        String robotsTag =
                doc.select("meta[name=robots]")
                        .attr("content")
                        .trim();

        // 6️⃣ PUBLISHED DATE
        String publishedDate = "";
        Element timeTag = doc.selectFirst("time");
        if (timeTag != null) {
            publishedDate = timeTag.text().trim();
        }

        // 7️⃣ ARTICLE SCHEMA
        String articleSchemaJson = doc.select("script[type=application/ld+json]")
                .stream()
                .map(Element::html)
                .filter(json -> json.contains("\"@type\":\"Article\""))
                .findFirst()
                .orElse("")
                .trim();

        // 8️⃣ FEATURE IMAGE
        String featureImage =
                doc.select("meta[property=og:image]")
                        .attr("content")
                        .trim();

        // 9️⃣ CLEANED BLOG CONTENT
        String cleanedBlogContentOnly = "";
        Element blogContent = doc.selectFirst(".blog-content");
        if (blogContent != null) {
            blogContent.select("script, style, nav, footer").remove();
            cleanedBlogContentOnly = blogContent.text()
                    .replaceAll("\\s+", " ")
                    .trim()
                    .toLowerCase();
        }

        // RETURN ALL 9 PARAMETERS
        return new SeoData(
                seoTitle,
                h1Title,
                metaDescription,
                canonicalPathOnly,
                robotsTag,                // ✅ NOW INCLUDED
                publishedDate,
                articleSchemaJson,
                featureImage,
                cleanedBlogContentOnly
        );
    }
}