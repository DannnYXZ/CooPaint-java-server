package com.epam.coopaint.service.impl;

import com.epam.coopaint.util.PropertyLoader;

import java.util.Properties;

class SecurityData {
    static final boolean ACL_USE_CACHING;

    static {
        try {
            Properties props = PropertyLoader.loadProperties(SecurityData.class.getResource("security.properties").getPath());
            ACL_USE_CACHING = Boolean.parseBoolean(props.getProperty("acl.use.caching"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize security data", e);
        }
    }
}
