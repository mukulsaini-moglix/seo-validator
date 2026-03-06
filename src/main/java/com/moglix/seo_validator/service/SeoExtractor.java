package com.moglix.seo_validator.service;

import java.net.URI;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moglix.seo_validator.dto.SeoData;

@Service
public class SeoExtractor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SeoData extract(int statusCode, String finalUrl, Document doc) {

        // ===============================
        // TECHNICAL
        // ===============================

        String canonicalPathOnly = "";
        String canonicalFull = doc.select("link[rel=canonical]").attr("abs:href");

        if (!canonicalFull.isEmpty()) {
            try {
                canonicalPathOnly = new URI(canonicalFull).getPath();
            } catch (Exception ignored) {}
        }

        String robotsTag = doc.select("meta[name=robots]").attr("content");

        String ogfullUrl = doc.select("meta[property=og:url]").attr("content");

        String ogUrl = "";

        try {
            ogUrl = new URI(ogfullUrl).getPath();
        } catch (Exception ignored) {}
        
        // ===============================
        // META
        // ===============================

        String seoTitle = doc.title();

        String metaDescription =
                doc.select("meta[name=description]").attr("content");

        String ogTitle =
                doc.select("meta[property=og:title]").attr("content");

        String ogDescription =
                doc.select("meta[property=og:description]").attr("content");

        String ogImage =
                doc.select("meta[property=og:image]").attr("content");

        // ===============================
        // HEADINGS
        // ===============================

        Elements h1s = doc.select("main h1");
        String h1Title = h1s.isEmpty() ? "" : h1s.first().text();

        int h1Count = h1s.size();
        int h2Count = doc.select("main h2").size();
        int h3Count = doc.select("main h3").size();

        // ===============================
        // STRUCTURED DATA (Article)
        // ===============================

        String schemaHeadline = "";
        String schemaDescription = "";
        String schemaImage = "";
        String schemaDatePublished = "";
        String schemaDateModified = "";
        String schemaAuthor = "";

        Elements scripts = doc.select("script[type=application/ld+json]");

        for (Element script : scripts) {

            String json = script.html();

            if (json.contains("\"Article\"")) {
                try {
                    JsonNode node = objectMapper.readTree(json);

                    if (node.has("@type") &&
                            node.get("@type").asText().contains("Article")) {

                        schemaHeadline = getJson(node, "headline");
                        schemaDescription = getJson(node, "description");
                        schemaImage = extractImage(node);
                        schemaDatePublished = getJson(node, "datePublished");
                        schemaDateModified = getJson(node, "dateModified");

                        if (node.has("author") &&
                                node.get("author").has("name")) {
                            schemaAuthor =
                                    node.get("author").get("name").asText();
                        }

                        break;
                    }

                } catch (Exception ignored) {}
            }
        }

        // ===============================
        // BLOG CONTENT
        // ===============================

        String cleanedBlogContentOnly = "";
        Element blogContent = doc.selectFirst(".blog-content");

        if (blogContent != null) {

            blogContent.select("script, style, nav, footer").remove();

            cleanedBlogContentOnly = blogContent.text()
                    .replaceAll("\\s+", " ")
                    .trim()
                    .toLowerCase();
        }

        // ===============================
        // IMAGES
        // ===============================

        Elements images = doc.select(".blog-content img");

        int imageCount = images.size();

        int missingAltCount = 0;
        for (Element img : images) {
            if (!img.hasAttr("alt") || img.attr("alt").trim().isEmpty()) {
                missingAltCount++;
            }
        }

        String featuredImage = ogImage;

        // ===============================
        // LINKS
        // ===============================

        Elements links = doc.select(".blog-content a[href]");

        int internalLinks = 0;
        int externalLinks = 0;

        for (Element link : links) {

            String href = link.attr("abs:href");

            if (href.isEmpty()) continue;

            if (href.contains(getDomain(finalUrl))) {
                internalLinks++;
            } else {
                externalLinks++;
            }
        }

        // ===============================
        // RETURN UPDATED SeoData
        // ===============================

        return new SeoData(
                statusCode,
                finalUrl,
                canonicalPathOnly,
                robotsTag,
                ogUrl,

                seoTitle,
                metaDescription,
                ogTitle,
                ogDescription,
                ogImage,

                h1Title,
                h1Count,
                h2Count,
                h3Count,

                schemaHeadline,
                schemaDescription,
                schemaImage,
                schemaDatePublished,
                schemaDateModified,
                schemaAuthor,

                cleanedBlogContentOnly,

                featuredImage,
                imageCount,
                missingAltCount,

                internalLinks,
                externalLinks
        );
    }

    // ===================================================
    // HELPERS
    // ===================================================

    private String getJson(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : "";
    }

    private String extractImage(JsonNode node) {

        if (!node.has("image")) return "";

        JsonNode imageNode = node.get("image");

        if (imageNode.isTextual()) {
            return imageNode.asText();
        }

        if (imageNode.has("url")) {
            return imageNode.get("url").asText();
        }

        return "";
    }

    private String getDomain(String url) {
        try {
            return new URI(url).getHost();
        } catch (Exception e) {
            return "";
        }
    }
}