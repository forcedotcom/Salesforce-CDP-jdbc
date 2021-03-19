package com.salesforce.cdp.queryservice.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryServiceResponse {

    private List<Map<String, Object>> data;

    private Date startTime;

    private Date endTime;

    private int rowCount;

    private String queryId;

    private boolean done = true;

    private Map<String, Type> metadata;
}
