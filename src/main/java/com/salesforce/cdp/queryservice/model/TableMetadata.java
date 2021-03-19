package com.salesforce.cdp.queryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TableMetadata {
    private List<Map<String, String>> fields;

    private String category;

    private String name;

    private List<Map<String, String>> measures;

    private List<Map<String, String>> dimensions;
}
