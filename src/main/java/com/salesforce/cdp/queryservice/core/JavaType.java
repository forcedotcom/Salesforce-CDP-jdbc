/*
 * Copyright (c) 2021, salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
