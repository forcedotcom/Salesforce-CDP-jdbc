package com.salesforce.cdp.queryservice.core;

import java.sql.SQLType;
import java.sql.Types;

public enum JavaType implements SQLType {

    STRING(Types.VARCHAR, "STRING"),

    DATE_TIME(Types.TIMESTAMP, "DATE_TIME"),

    NUMBER(Types.NUMERIC, "NUMBER");

    private Integer type;

    private String name;

    JavaType(final Integer type, final String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getVendor() {
        return "";
    }

    @Override
    public Integer getVendorTypeNumber() {
        return type;
    }

    public static JavaType getTypeByName(String name) {
        for (JavaType javaType : JavaType.class.getEnumConstants()) {
            if (name.equals(javaType.name))
                return javaType;
        }
        throw new IllegalArgumentException("Type:" + name + " is not a valid "
                + "Types.java value.");
    }
}
