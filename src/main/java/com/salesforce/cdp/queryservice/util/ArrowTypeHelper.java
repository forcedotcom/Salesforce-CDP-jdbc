package com.salesforce.cdp.queryservice.util;

import lombok.val;
import org.apache.arrow.vector.types.Types;
import org.apache.arrow.vector.types.pojo.ArrowType;

import java.sql.SQLException;

public class ArrowTypeHelper {

  enum JdbcType {
    INTEGER("INTEGER"),
    BIGINT("BIGINT"),
    BIT("BOOLEAN"),
    VARCHAR("VARCHAR"),
    DATE_DAY("DATE"),
    DECIMAL("DECIMAL"),
    FLOAT_8("DOUBLE"),
    INT("INTEGER"),
    FLOAT_4("REAL"),
    SMALL_INT("SMALLINT"),
    TIME_NANO("TIME"),
    TIMESTAMP_NANO_TZ("TIMESTAMP WITH TIME ZONE"),
    TIMESTAMP_NANO("TIMESTAMP"),
    TINY_INT("TINYINT");

    private final String type;

    JdbcType(String value){
      this.type = value;
    }
    public String toString(){
      return type;
    }
  }
  public static String getJdbcType(Types.MinorType minorType) throws SQLException {
    switch (minorType){
      case BIGINT:
        return JdbcType.BIGINT.toString();
      case BIT:
        return JdbcType.BIT.toString();
      case VARCHAR:
        return JdbcType.VARCHAR.toString();
      case DATEDAY:
        return JdbcType.DATE_DAY.toString();
      case DECIMAL:
        return JdbcType.DECIMAL.toString();
      case FLOAT8:
        return JdbcType.FLOAT_8.toString();
      case INT:
        return JdbcType.INTEGER.toString();
      case FLOAT4:
        return JdbcType.FLOAT_4.toString();
      case SMALLINT:
        return JdbcType.SMALL_INT.toString();
      case TIMENANO:
        return JdbcType.TIME_NANO.toString();
      case TIMESTAMPNANOTZ:
        return JdbcType.TIMESTAMP_NANO_TZ.toString();
      case TIMESTAMPNANO:
        return JdbcType.TIMESTAMP_NANO.toString();
      case TINYINT:
        return JdbcType.TINY_INT.toString();
      default:
        throw new SQLException("Invalid type received "+ minorType);
    }
  }
}