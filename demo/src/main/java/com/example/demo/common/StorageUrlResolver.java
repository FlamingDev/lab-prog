package com.example.demo.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StorageUrlResolver {

    private static String baseUrl = "https://meu-bucket-s3.s3.amazonaws.com";

    @Value("${api.storage.s3.base-url:https://meu-bucket-s3.s3.amazonaws.com}")
    public void setBaseUrl(String configuredBaseUrl) {
        if (configuredBaseUrl != null && !configuredBaseUrl.isBlank()) {
            StorageUrlResolver.baseUrl = configuredBaseUrl.endsWith("/")
                    ? configuredBaseUrl.substring(0, configuredBaseUrl.length() - 1)
                    : configuredBaseUrl;
        }
    }

    public static String resolve(String relativeKey) {
        if (relativeKey == null || relativeKey.isBlank()) {
            return null;
        }
        if (relativeKey.startsWith("http://") || relativeKey.startsWith("https://")) {
            return relativeKey;
        }
        String cleanKey = relativeKey.startsWith("/") ? relativeKey.substring(1) : relativeKey;
        return baseUrl + "/" + cleanKey;
    }
}
