package com.salesforce.cdp.queryservice.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class MetadataCacheUtil {

    private MetadataCacheUtil() {
        // NOOP
    }

    private static Cache<String, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(600000, TimeUnit.MILLISECONDS)
            .maximumSize(10).build();

    public static String getMetadata(String url) {
        return cache.getIfPresent(url);
    }

    public static void cacheMetadata(String url, String response) {
        cache.put(url, response);
    }

    public static void invalidateObject(String url) {
        cache.invalidate(url);
    }

}
