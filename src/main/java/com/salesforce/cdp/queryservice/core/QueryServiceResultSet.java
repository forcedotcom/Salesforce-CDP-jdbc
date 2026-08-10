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
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class QueryServiceResultSet implements ResultSet {

    private final AtomicBoolean closed = new AtomicBoolean();
    protected ResultSetMetaData resultSetMetaData;

    //To fix the Invalid Date format issue
    private final String dateSimple = "yyyy-MM-dd";
    private final String dateISOStandard = "yyyy-MM-dd'T'HH:mm:ss";
    private final String dateWithSeconds = "yyyy-MM-dd HH:mm:ss";
    private final String dateWithMsTz = "yyyy-MM-dd HH:mm:ss.SSS Z";
    private final String dateIn12HourFormat = "MMM d, yyyy, HH:mm:ss a";

    protected List<Object> data;
    protected int currentRow = -1;
    protected int currentPageNum = 1;
    protected final AtomicBoolean wasNull = new AtomicBoolean();

    protected QueryServiceAbstractStatement statement;

    public QueryServiceResultSet() {}

    // NOTE: This constructor is used for metadata table, hence only data and resultSetMetadata is set.
    public QueryServiceResultSet(List<Object> data,
                                 ResultSetMetaData resultSetMetaData) {
        this(data, resultSetMetaData, null);
    }

    public QueryServiceResultSet(List<Object> data,
                                 ResultSetMetaData resultSetMetaData,
                                 QueryServiceAbstractStatement statement) {
        this.data = data == null ? new ArrayList<>(): data;
        this.resultSetMetaData = resultSetMetaData;
        this.statement = statement;
    }

    @Override
    public boolean next() throws SQLException {
        errorOutIfClosed();

        currentRow++;
        if (currentRow < data.size()) {
            return true;
        }

        if(isPaginationRequired()) {
            getMoreData();
            if(data!=null && data.size()>0)
                return true;
        }

        // Closing as this is move forward only cursor.
        log.info("Resultset {} does not have any more rows. Total {} pages retrieved", this, currentPageNum);
        return false;
    }

    @Override
    public void close() throws SQLException {
        log.info("Closing the resultset {}", this);
        closed.set(true);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return wasNull.get();
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getString(columnName);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getBoolean(columnName);
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getByte(columnName);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getShort(columnName);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getInt(columnName);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getLong(columnName);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getFloat(columnName);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getDouble(columnName);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getBigDecimal(columnName);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getBytes(columnName);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getDate(columnName);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getTime(columnName);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getTimestamp(columnName);
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getAsciiStream(columnName);
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getUnicodeStream(columnName);
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getBinaryStream(columnName);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        errorOutIfClosed();
        Object value = getObject(columnLabel);
        wasNull.set(value == null);
        if (wasNull()) {
            return null;
        }
        return value.toString();
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        errorOutIfClosed();
        Object value = getObject(columnLabel);
        if (wasNull() || StringUtils.isBlank(value.toString())) {
            wasNull.set(true);
            return false;
        }
        return BooleanUtils.toBoolean(value.toString());
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        errorOutIfClosed();
        String str = getString(columnLabel);
        String s1 = new String(Base64.decodeBase64(str.getBytes()));
        return (byte) s1.charAt(0);
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("Short not supported currently");
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        errorOutIfClosed();
        Object value = getObject(columnLabel);
        if (wasNull() || StringUtils.isBlank(value.toString())) {
            wasNull.set(true);
            return 0;
        }
        return Integer.parseInt(value.toString());
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        errorOutIfClosed();
        Object value = getObject(columnLabel);
        if (wasNull() || StringUtils.isBlank(value.toString())) {
            wasNull.set(true);
            return 0L;
        }
        return Long.parseLong(value.toString());
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        errorOutIfClosed();
        Object value = getObject(columnLabel);
        if (wasNull() || StringUtils.isBlank(value.toString())) {
            wasNull.set(true);
            return 0;
        }
        return Float.parseFloat(value.toString());
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        errorOutIfClosed();
        Object value = getObject(columnLabel);
        if (wasNull() || StringUtils.isBlank(value.toString())) {
            wasNull.set(true);
            return 0;
        }
        return Double.parseDouble(value.toString());
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        errorOutIfClosed();
        return BigDecimal.valueOf(getDouble(columnLabel));
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        errorOutIfClosed();
        String value = getString(columnLabel);
        if (wasNull()){
            return new byte[0];
        }
        return value.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        errorOutIfClosed();
        return getDate(columnLabel, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        errorOutIfClosed();
        return getTime(columnLabel, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        errorOutIfClosed();
        return getTimestamp(columnLabel, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        //NOOP
    }

    @Override
    public String getCursorName() throws SQLException {
        return null;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        errorOutIfClosed();
        return resultSetMetaData;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnName = getColumnNameByIndex(columnIndex);
        return getObject(columnName);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        errorOutIfClosed();
        Object row = data.get(currentRow);
        Object value = getValue(row, columnLabel);
        wasNull.set(value == null);
        return value;
    }

    protected Object getValue(Object row, String columnLabel) throws SQLException {
        return ((Map)row).get(columnLabel);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        //TODO Get columns from metadata and return
        return 0;
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        errorOutIfClosed();
        String columnNameByIndex = getColumnNameByIndex(columnIndex);
        return getBigDecimal(columnNameByIndex);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        errorOutIfClosed();
        String value = getString(columnLabel);
        if (StringUtils.isBlank(value)) {
            wasNull.set(true);
            return null;
        }
        return new BigDecimal(value);
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return currentRow == -1;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return !isPaginationRequired() && currentRow >= data.size();
    }

    @Override
    public boolean isFirst() throws SQLException {
        return currentPageNum == 1 && currentRow == 0;
    }

    @Override
    public boolean isLast() throws SQLException {
        return !isPaginationRequired() && currentRow == data.size() - 1;
    }

    @Override
    public void beforeFirst() throws SQLException {
        //NOOP
    }

    @Override
    public void afterLast() throws SQLException {
        //NOOP
    }

    @Override
    public boolean first() throws SQLException {
        throw new SQLException("Result set is of type TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean last() throws SQLException {
        throw new SQLException("Result set is of type TYPE_FORWARD_ONLY");
    }

    @Override
    public int getRow() throws SQLException {
        errorOutIfClosed();
        if (currentRow < 0) {
            throw new SQLException("Cursor is not pointing to any row. Use next()");
        }
        return currentRow;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        throw new SQLException("Result set is of type TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        throw new SQLException("Result set is of type TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean previous() throws SQLException {
        throw new SQLException("Result set is of type TYPE_FORWARD_ONLY");
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        //NOOP
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        //NOOP
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void insertRow() throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateRow() throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void refreshRow() throws SQLException {
        //TODO: Support during live
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public Statement getStatement() throws SQLException {
        return null;
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        errorOutIfClosed();
        String columnNameByIndex = getColumnNameByIndex(columnIndex);
        return getDate(columnNameByIndex, cal);
    }

    //Handle multiple date formats
    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        errorOutIfClosed();
        Object value = getObject(columnLabel);
        if (wasNull() || value== null || StringUtils.EMPTY.equals(value)) {
            wasNull.set(true);
            return null;
        }

        // TODO: optimize date parsing. Can we avoid doing this for each row?
        String[] formats = new String[] {dateWithMsTz, dateISOStandard, dateWithSeconds, dateSimple, dateIn12HourFormat};
        try {
            String valueString = value.toString();
            java.util.Date date = DateUtils.parseDate(valueString, formats);
            if(date== null) {
                throw new SQLException("Invalid date from server: " + value);
            }
            return new java.sql.Date(date.getTime());
        }
        catch (IllegalArgumentException e) {
            throw new SQLException("Invalid date from server: " + value, e);
        }
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        errorOutIfClosed();
        String columnNameByIndex = getColumnNameByIndex(columnIndex);
        return getTime(columnNameByIndex, cal);
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        errorOutIfClosed();
        Date date = getDate(columnLabel, cal);
        if (wasNull()){
            return null;
        }
        return new Time(date.getTime());
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        errorOutIfClosed();
        String columnNameByIndex = getColumnNameByIndex(columnIndex);
        return getTimestamp(columnNameByIndex, cal);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        errorOutIfClosed();
        Date date = getDate(columnLabel, cal);
        if (wasNull()){
            return null;
        }
        return new Timestamp(date.getTime());
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        // Not supported for now.
        return null;
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        // Not supported for now.
        return null;
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed.get();
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException("Not supported as resultset is read only");
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return null;
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    private String getColumnNameByIndex(int columnIndex) throws SQLException {
        return resultSetMetaData.getColumnName(columnIndex);
    }

    private void getMoreData() throws SQLException {
        log.trace("Fetching page with number {} for resultset {}", ++currentPageNum, this);
        ResultSet resultSet = getNextPageData();
        if(resultSet==null)
            return;

        updateState(resultSet);
    }

    protected ResultSet getNextPageData() throws SQLException {
        return statement.getNextPage();
    }

    protected void updateState(ResultSet resultSet) throws SQLException {
        try {
            Field field = QueryServiceResultSet.class.getDeclaredField("data");
            field.setAccessible(true);
            List<Object> nextPageData =  (List<Object>) field.get(resultSet);
            field.setAccessible(false);
            this.data = nextPageData;
            this.currentRow = 0;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error while getting the data from resultset {}", this, e);
            throw new SQLException(e.getMessage());
        }
    }

    private boolean isPaginationRequired() {
        return statement != null && statement.isPaginationRequired();
    }

    protected void errorOutIfClosed() throws SQLException {
        if (isClosed()) {
            throw new SQLException("Resultset is already closed");
        }
    }
}
