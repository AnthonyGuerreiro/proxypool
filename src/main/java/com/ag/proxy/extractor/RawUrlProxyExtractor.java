package com.ag.proxy.extractor;

import com.ag.proxy.pool.ProxyPoolException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class RawUrlProxyExtractor implements ProxyExtractor {

    private final String regexSeparator;

    @Override
    public List<String> extract(final String url) {
        try {
            final Document document = getDocument(url);
            final String body = getBody(document);

            final String[] lines = body.split(this.regexSeparator);
            return Arrays.asList(lines);
        } catch (final IOException e) {
            throw new ProxyPoolException("Error extracting proxies from " + url, e);
        }
    }

    private String getBody(final Document document) {
        return document.body().text();
    }

    private Document getDocument(final String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
