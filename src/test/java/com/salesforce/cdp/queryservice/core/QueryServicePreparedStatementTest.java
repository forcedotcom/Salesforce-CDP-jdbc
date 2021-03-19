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
        preparedStatement.setString(0, "Angella");
        preparedStatement.setInt(1, 1000);
        String sql = preparedStatement.createSqlQuery();
        Assert.assertEquals(sql, "select FirstName__c, BirthDate__c, YearlyIncome__c from Individual__dlm where FirstName__c = 'Angella' and YearlyIncome__c > 1000");
    }

    @Test
    public void testSqlWithoutEnoughParameters() throws SQLException {
        preparedStatement.setString(0, "Angella");
        exceptionRule.expect(SQLException.class);
        exceptionRule.expectMessage("Not enough parameters");
        preparedStatement.createSqlQuery();
    }

    @Test
    public void testSqlWithDateParameter() throws SQLException {
        Date date = new Date(System.currentTimeMillis());
        preparedStatement = new QueryServicePreparedStatement(connection, "select FirstName__c, BirthDate__c from Individual__dlm where BirthDate__c > ?", ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
        preparedStatement.setDate(0, date);
        String sql = preparedStatement.createSqlQuery();
        Assert.assertEquals(sql, "select FirstName__c, BirthDate__c from Individual__dlm where BirthDate__c > " + date.toString());
    }
}
