package com.salesforce.cdp.queryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationShip {

    private String fromEntity;

    private String toEntity;

    private String fromEntityAttribute;

    private String toEntityAttribute;

    private String cardinality;
}
