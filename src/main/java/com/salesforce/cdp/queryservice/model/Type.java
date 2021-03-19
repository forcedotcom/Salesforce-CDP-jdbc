package com.salesforce.cdp.queryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Type {
    private int placeInOrder;

    private int typeCode;

    private String type;
}
