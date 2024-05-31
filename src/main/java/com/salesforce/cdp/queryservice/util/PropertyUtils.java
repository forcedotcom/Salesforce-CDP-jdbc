package com.salesforce.cdp.queryservice.util;

import java.util.Properties;

public class PropertyUtils {

    public static boolean isPropertyNonEmpty(Properties properties, String key) {
        return properties != null
                && properties.containsKey(key)
                && !properties.getProperty(key).isEmpty();
    }
}
