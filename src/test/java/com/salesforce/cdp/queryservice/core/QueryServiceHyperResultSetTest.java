package com.salesforce.cdp.queryservice.core;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse;
import com.salesforce.cdp.queryservice.util.ExtractArrowUtil;
import com.salesforce.cdp.queryservice.util.QueryExecutor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryServiceHyperResultSetTest {

    private ExtractArrowUtil arrowUtil;

    private QueryServiceHyperResultSet hyperResultSet;

    @Before
    public void init() throws SQLException {
        arrowUtil = Mockito.mock(ExtractArrowUtil.class);
        ResultSetMetaData resultSetMetaData = mockResultSetMetadata();
        hyperResultSet = new QueryServiceHyperResultSet(null, resultSetMetaData, null);
        when(arrowUtil.isNextChunkPresent()).thenReturn(true, false);
        when(arrowUtil.getRowsFromStreamResponse()).thenReturn(mockData());
        Whitebox.setInternalState(hyperResultSet, "arrowUtil", arrowUtil);
    }

    @Test
    public void testGetObject() throws SQLException {
        hyperResultSet.next();
        List<Object> data = (List)mockData().get(0);
        for(int i=1; i<=data.size(); i++) {
            Assert.assertEquals(data.get(i-1), hyperResultSet.getObject(i));
        }
    }

    @Test
    public void testGetDate() throws SQLException {
        hyperResultSet.next();
        Assert.assertEquals(new Date(1678166106000L).toString(), hyperResultSet.getDate(4).toString());
        Assert.assertEquals(new Time(1678166106000L).toString(), hyperResultSet.getTime(4).toString());
        Assert.assertEquals(new Timestamp(1678166106000L).toString(), hyperResultSet.getTimestamp(4).toString());
        Assert.assertEquals(new Timestamp(1678166106000L).toString(), hyperResultSet.getTimestamp(4, Calendar.getInstance(TimeZone.getTimeZone("UTC"))).toString());

        Assert.assertEquals(new Date(1678166106000L).toString(), hyperResultSet.getDate(5).toString());
        Assert.assertEquals(new Time(1678166106000L).toString(), hyperResultSet.getTime(5).toString());
        Assert.assertEquals(new Timestamp(1678166106000L).toString(), hyperResultSet.getTimestamp(5).toString());
        Assert.assertEquals(new Timestamp(1678166106000L).toString(), hyperResultSet.getTimestamp(5, Calendar.getInstance(TimeZone.getTimeZone("UTC"))).toString());

        Assert.assertEquals(new Date(1678166106000L).toString(), hyperResultSet.getDate(6).toString());
        Assert.assertEquals(new Time(1678166106000L).toString(), hyperResultSet.getTime(6).toString());
        Assert.assertEquals(new Timestamp(1678166106000L).toString(), hyperResultSet.getTimestamp(6).toString());
        Assert.assertEquals(new Timestamp(1678166106000L).toString(), hyperResultSet.getTimestamp(6, Calendar.getInstance(TimeZone.getTimeZone("UTC"))).toString());

    }

    private List<Object> mockData() {
        List<Object> data = new ArrayList<>();
        List<Object> row = new ArrayList<>();
        row.add(1);
        row.add("1");
        row.add(1.0);
        row.add(1678166106000L);
        row.add(LocalDateTime.ofEpochSecond(1678166106, 0, ZoneOffset.UTC));
        row.add(new Date(1678166106000L));
        data.add(row);
        return data;
    }

    private ResultSetMetaData mockResultSetMetadata() {
        String[] columnNames = {"col1", "col2", "col3", "col4", "col5", "col6"};
        String[] columnTypes = {"INTEGER", "VARCHAR", "DECIMAL", "TIMESTAMP WITH TIME ZONE", "TIMESTAMP WITH TIME ZONE", "TIMESTAMP WITH TIME ZONE"};
        Integer[] columnTypeIds = {1,2,3,4,5,6};
        Map<String, Integer> columnNameToPosition = new HashMap<>();

        for(int i=0; i<columnNames.length; i++) {
            columnNameToPosition.put(columnNames[i], i);
        }

        ResultSetMetaData resultSetMetaData = new QueryServiceResultSetMetaData(
                Arrays.asList(columnNames),
                Arrays.asList(columnTypes),
                Arrays.asList(columnTypeIds),
                columnNameToPosition);

        return resultSetMetaData;
    }

}
