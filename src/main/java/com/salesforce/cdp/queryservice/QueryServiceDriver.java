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

package com.salesforce.cdp.queryservice;

import com.salesforce.cdp.queryservice.core.QueryServiceConnection;
import com.salesforce.cdp.queryservice.util.Constants;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

@Slf4j
public class QueryServiceDriver implements Driver {

    private static Driver registeredDriver;

    static {
        try {
            register();
            log.info("Driver registered");
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void register() throws SQLException {
        if (isRegistered()) {
            throw new IllegalStateException(
                    "Driver is already registered. It can only be registered once.");
        }
        Driver registeredDriver = new QueryServiceDriver();
        DriverManager.registerDriver(registeredDriver);
        QueryServiceDriver.registeredDriver = registeredDriver;
    }

    public static boolean isRegistered() {
        return registeredDriver != null;
    }


    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (url == null) {
            throw new SQLException("URL is null");
        }

        if (!url.startsWith(Constants.DATASOURCE_TYPE)) {
            throw new SQLException("Invalid URL");
        }
        return new QueryServiceConnection(url, info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(Constants.DATASOURCE_TYPE + "https://");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
