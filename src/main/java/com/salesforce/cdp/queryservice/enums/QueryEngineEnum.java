package com.salesforce.cdp.queryservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QueryEngineEnum {
    HYPER("hyper"),
    TRINO("trino");

    private final String value;

    QueryEngineEnum(String value) {
        this.value = value;
    }

    /**
     * get QueryEngineEnum value.
     *
     * @param value String value
     * @return QueryEngineEnum
     */
    @JsonCreator
    public static QueryEngineEnum fromValue(String value) {
        for (QueryEngineEnum queryEngineEnum : QueryEngineEnum.values()) {
            if (queryEngineEnum.value.equals(value)) {
                return queryEngineEnum;
            }
        }

        throw new IllegalArgumentException("Unexpected value for QueryEngineEnum: '" + value + "'");
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(this.value);
    }
}
