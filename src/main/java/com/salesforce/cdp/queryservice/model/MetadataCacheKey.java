package com.salesforce.cdp.queryservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MetadataCacheKey {
    private String tenantUrl;
    private String dataspace;

    public MetadataCacheKey(String tenantUrl, String dataspace) {
        this.tenantUrl = tenantUrl;
        this.dataspace = dataspace;
    }
}