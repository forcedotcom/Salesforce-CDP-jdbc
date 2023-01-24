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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@RunWith(MockitoJUnitRunner.class)
public class QueryServicePreparedStatementTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private QueryServicePreparedStatement preparedStatement;

    @Mock
    private QueryServiceConnection connection;

    @Before
    public void init() {
        preparedStatement = new QueryServicePreparedStatement(connection, "select FirstName__c, BirthDate__c, YearlyIncome__c from Individual__dlm where FirstName__c = ? and YearlyIncome__c > ?", ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
    }

    @Test
    public void testWildCardReplacementForPreparedStatement() throws SQLException {
        preparedStatement.setString(1, "Angella");
        preparedStatement.setInt(2, 1000);
        String sql = preparedStatement.createSqlQuery();
        Assert.assertEquals(sql, "select FirstName__c, BirthDate__c, YearlyIncome__c from Individual__dlm where FirstName__c = 'Angella' and YearlyIncome__c > 1000");
    }

    @Test
    public void testSqlWithoutEnoughParameters() throws SQLException {
        preparedStatement.setString(1, "Angella");
        exceptionRule.expect(SQLException.class);
        exceptionRule.expectMessage("Not enough parameters");
        preparedStatement.createSqlQuery();
    }

    @Test
    public void testSqlWithDateParameter() throws SQLException {
        Date date = new Date(System.currentTimeMillis());
        preparedStatement = new QueryServicePreparedStatement(connection, "select FirstName__c, BirthDate__c from Individual__dlm where BirthDate__c > ?", ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
        preparedStatement.setDate(1, date);
        String sql = preparedStatement.createSqlQuery();
        Assert.assertEquals(sql, "select FirstName__c, BirthDate__c from Individual__dlm where BirthDate__c > " + date.toString());
    }
}
