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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.sql.*;
import java.util.List;

import static com.salesforce.cdp.queryservice.util.Messages.QUERY_EXCEPTION;

/**
 * This ResultSet is used in case of `CursorBasedPagination`.
 * NextBatchId is the extra attribute present here which is used to get next page.
 * Also, the response data payload is different than v1.
 */
@Slf4j
public class QueryServiceResultSetV2 extends QueryServiceResultSet {

    private String nextBatchId;

    public QueryServiceResultSetV2(List<Object> data,
                                   ResultSetMetaData resultSetMetaData,
                                   QueryServiceAbstractStatement statement,
                                   String nextBatchId) {
        super(data, resultSetMetaData, statement);
        this.nextBatchId = nextBatchId;
    }

    @Override
    protected ResultSet getNextPageData() throws SQLException {
        if(StringUtils.isEmpty(nextBatchId)) {
            return null;
        }

        return statement.getNextPageFromBatchId(nextBatchId);
    }

    @Override
    protected void updateState(ResultSet resultSet) throws SQLException {
        super.updateState(resultSet);
        this.nextBatchId = ((QueryServiceResultSetV2)resultSet).nextBatchId;
    }

    @Override
    protected Object getValue(Object row, String columnLabel) throws SQLException {
        Integer placeInOrder = ((QueryServiceResultSetMetaData)resultSetMetaData).getColumnNameToPosition().get(columnLabel);
        if(placeInOrder==null)
            throw new SQLException(QUERY_EXCEPTION);
        return ((List)row).get(placeInOrder);
    }
}
