package com.webharmony.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlBuilder {

    private boolean isSSl;
    private final String domain;

    private int port;
    private String path;
    private final Map<String, Object> query;

    private UrlBuilder(String domain) {
        this.domain = domain;
        this.query = new HashMap<>();
        this.isSSl = true;
        this.port = 80;
    }

    public static UrlBuilder ofDomain(String domain) {

        Boolean ssl = null;
        String internalDomain = domain;
        Integer port = null;

        if(domain.contains("//")) {
            final String[] fragments = domain.split("//");
            final String protocolPart = fragments[0];
            ssl = protocolPart.contains("https");

            if(fragments[1].contains(":")) {
                final String[] fragments2 = fragments[1].split(":");
                internalDomain = fragments2[0];
                try {
                    port = Integer.parseInt(fragments2[1]);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }

        final UrlBuilder builder =  new UrlBuilder(internalDomain);
        if(ssl != null) {
            builder.isSSl = ssl;
        }

        if(port != null) {
            builder.port = port;
        }

        return builder;
    }

    public UrlBuilder withSsl(boolean isSSl) {
        this.isSSl = isSSl;
        return this;
    }

    public UrlBuilder withPath(String path) {
        this.path = path;
        return this;
    }

    public UrlBuilder withQuery(String key, Object value) {
        this.query.put(key, value);
        return this;
    }

    public UrlBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    @SuppressWarnings("all")
    public String build() {

        final String protocolFragment = this.isSSl ? "https://" : "http://";
        final String domainFragment = removeFirstAndLastSlash(this.domain).replace("https://", "").replace("http://", "");
        final String portFragmentWithDots = this.port == 80 ? "" : ":" + this.port;
        final String pathFragment = removeFirstAndLastSlash(this.path);
        final String queryFragment = this.query.entrySet().stream().map(e -> String.format("%s=%s", e.getKey(), e.getValue())).collect(Collectors.joining("&"));

        final String mainPart = protocolFragment + domainFragment + portFragmentWithDots;
        final String subpart = pathFragment + (queryFragment.isEmpty() ? "" : "?" + queryFragment);
        return  subpart.isEmpty() ? mainPart : mainPart + "/" + subpart;
    }

    private String removeFirstAndLastSlash(String fragment) {

        String resolvedFragment = fragment;
        if(StringUtils.isNullOrEmpty(fragment))
            return "";

        if(resolvedFragment.startsWith("/")) {
            resolvedFragment = resolvedFragment.substring(1);
        }

        if(resolvedFragment.isEmpty())
            return fragment;

        if(resolvedFragment.endsWith("/")) {
            resolvedFragment = resolvedFragment.substring(0, resolvedFragment.length() - 1);
        }

        return resolvedFragment;
    }


}
