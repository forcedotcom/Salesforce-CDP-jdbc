package com.salesforce.cdp.queryservice.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.Map;

@Data
public class DataSpaceAttributes {
    Map<String,Object> attributes;
    @JsonAlias({ "Name" })
    String name;
}
