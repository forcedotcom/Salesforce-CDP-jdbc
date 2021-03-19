package com.salesforce.cdp.queryservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnsiQueryRequest {
    private String sql;
}
