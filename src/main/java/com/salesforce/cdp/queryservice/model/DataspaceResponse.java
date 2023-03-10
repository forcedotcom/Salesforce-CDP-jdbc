package com.salesforce.cdp.queryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataspaceResponse {
    List<DataSpaceAttributes> records;
    Integer totalSize;
    Boolean done;
}