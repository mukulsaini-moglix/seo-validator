package com.moglix.seo_validator.service;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.moglix.seo_validator.dto.response.PageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PageFetcher {

    public PageResponse fetch(String url) throws IOException {

        Connection connection = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(20000)
                .followRedirects(true)
                .ignoreHttpErrors(true);

        Connection.Response response = connection.execute();

        int statusCode = response.statusCode();
        String finalUrl = response.url().toString();
        Document document = response.parse();

        return new PageResponse(statusCode, finalUrl, document);
    }
}