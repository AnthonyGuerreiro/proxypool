package com.ag.proxy.extractor;

import com.ag.proxy.pool.ProxyPoolException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ClasspathUrlProxyExtractor implements ProxyExtractor {

    private final ProxyExtractor extractor;

    public ClasspathUrlProxyExtractor(final ProxyExtractor extractor) {
        this.extractor = extractor == null ? new NullProxyExtractor() : extractor;
    }

    @Override
    public List<String> extract(final String url) {
        try {
            final List<String> urls = readUrls(url);
            return urls.stream()
                    .map(this.extractor::extract)
                    .flatMap(Collection::stream)
                    .collect(toList());
        } catch (final IOException | URISyntaxException e) {
            throw new ProxyPoolException("Error extracting proxies from " + url, e);
        }
    }

    private List<String> readUrls(final String url) throws IOException, URISyntaxException {
        final URL resource = ClasspathUrlProxyExtractor.class.getResource(url);
        final Path path = Paths.get(resource.toURI());
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }
}
