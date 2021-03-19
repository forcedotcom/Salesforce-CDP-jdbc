/*
 * Copyright (c) 2021, salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
