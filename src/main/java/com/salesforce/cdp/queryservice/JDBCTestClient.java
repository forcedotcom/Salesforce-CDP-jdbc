package com.salesforce.cdp.queryservice;

import com.salesforce.cdp.queryservice.core.QueryServiceResultSetMetaData;
import com.salesforce.cdp.queryservice.util.ArrowUtil;

import java.sql.*;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCTestClient {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {
        System.out.println("Welcome to JDBC Test Client");
        Class.forName("com.salesforce.cdp.queryservice.QueryServiceDriver");
//mvn install:install-file  -Dfile=<path-to-file>  -DgroupId=com.queryService -DartifactId=Salesforce-CDP-jdbc  -Dversion=1.11.0   -Dpackaging=<packaging>  -DgeneratePom=true
        java.util.Properties props = new java.util.Properties();
        props.put("userName","ironman_236@cdpuser.com");
        props.put("password","test12345");
        props.put("clientId","3MVG9sA57VMGPDfcrHMiwHMsiY7VTySB.OwD8mtN1k05.phraecey3QcunUW53aqhKf.xLMYAKpCgwNEIQBqf");
        props.put("clientSecret","2E4F744CAE70B44D1DC84F7EFCE53E50632F2ED935ACF81C7B96AC279E961740");
        props.put("enable-stream-flow","true");
        // props.put("Rainbow-Client","true");
//        props.put("userName","palani@c360a.org");
//        props.put("password","welcome123!");
//        props.put("enable-arrow-stream","true");

//        props.put("userName","skostrzewski@sfcdptod.demo");
//        props.put("password","cdpdemo2");

//        String connectionString = "jdbc:queryService-jdbc:https://na46.test1.pc-rnd.salesforce.com";
//        props.put("user","ironman_236@cdpuser.com");
//        props.put("password","123456");


//        props.put("userName","flash_232@cdpuser.com");
//        props.put("password","cdp123456");
//        props.put("enable-arrow-stream","false");
     //   props.put("clientId","3MVG9XjhiDAzhaqaLC9xp.9uezGGvij5SgRdag6tpEA3S9X0Cr3dX.EtP76fFMxfNfSdcMldlg5WX2MwLsQ_m");
      //  props.put("clientSecret","2EBA21E8254B5D9109052689A34D6B190ACA97A2040B919BD6657A6FB2C92E43");



        String connectionString = "jdbc:queryService-jdbc:https://ironman236cdpusercom.test1.my.pc-rnd.salesforce.com";
       // props.put("user","admin@trialorgsforu132.com");
       // props.put("password","test1234");

        //String connectionString = "jdbc:queryService-jdbc:https://c360a3org-dev-ed.my.salesforce.com";
        //String connectionString = "jdbc:queryService-jdbc:https://login.stmpa.stm.salesforce.com";
        Connection con = DriverManager.getConnection(connectionString,props);
        //Connection con = DriverManager.getConnection(connectionString,"flash_232@cdpuser.com","cdp_123456");
        System.out.println("Connection established......");
            getRecordsViaRainbow("1",con);


        //String query = "select * from tpcds_1gb_store_sales__dll where ss_item_ticket_sk__c = '16318-188184'";
//        String query = "select Id__c, BirthDate__c, date(BirthDate__c) as date, cast('2020-06-10 15:55:23.383345' as timestamp(1)), cast(123.123 as real) as realvalue, cast(121231233.123 as double) as doublevalue, YEAR(BirthDate__c) > 1950, cast(112 as tinyint), cast(12345 as smallint), cast(12345123 as int) from Individual__dlm where BirthDate__c is not null limit 10";
//        Statement st = con.createStatement();
//        ResultSet rs = st.executeQuery(query);
//        System.out.println("\n");
//        while (rs.next()) {
//            int colCount = rs.getMetaData().getColumnCount();
//            Map<String,Object> row = new HashMap<>();
//            for(int i=1;i<=colCount;++i) {
//                row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
//            }
//            System.out.println(row);
//        }
//
//        query = "select * from tpcds_1gb_store_sales__dll where ss_item_ticket_sk__c = '16318-188184'";
//        st = con.createStatement();
//        rs = st.executeQuery(query);
//        System.out.println("\n");
//        while (rs.next()) {
//            int colCount = rs.getMetaData().getColumnCount();
//            Map<String,Object> row = new HashMap<>();
//            for(int i=1;i<=colCount;++i) {
//                row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
//            }
//            System.out.println(row);
//        }
        Thread.sleep(1000);
    }

  /*public static  void main(String[] args){
      ArrowUtil util = new ArrowUtil();
      try {
          List<Object > rows = util.getResults("/////9gBAAAQAAAAAAAKAA4ABgANAAgACgAAAAAABAAQAAAAAAEKAAwAAAAIAAQACgAAAAgAAAAIAAAAAAAAAAYAAABYAQAACAEAAMgAAACEAAAARAAAAAQAAADS/v//FAAAABQAAAAUAAAAAAAHARgAAAAAAAAAAAAAAMD+//8SAAAAJgAAAAgAAABwdl9pZF9fYwAAAAAO////FAAAABQAAAAUAAAAAAAHARgAAAAAAAAAAAAAAPz+//8SAAAAJgAAAAkAAABudW1iZXJfX2MAAABK////FAAAABQAAAAUAAAAAAAFARAAAAAAAAAAAAAAAIT///8XAAAASW50ZXJuYWxPcmdhbml6YXRpb25fX2MAiv///xQAAAAUAAAAFAAAAAAABQEQAAAAAAAAAAAAAADE////EwAAAERhdGFTb3VyY2VPYmplY3RfX2MAxv///xQAAAAUAAAAGAAAAAAABQEUAAAAAAAAAAAAAAAEAAQABAAAAA0AAABEYXRhU291cmNlX19jABIAGAAUABMAEgAMAAAACAAEABIAAAAUAAAAFAAAABwAAAAAAAcBIAAAAAAAAAAAAAAACAAMAAgABAAIAAAAEgAAACYAAAAKAAAAY29udGFjdF9fYwAA/////6gBAAAUAAAAAAAAAAwAFgAOABUAEAAEAAwAAAAIBgAAAAAAAAAABAAQAAAAAAMKABgADAAIAAQACgAAABQAAAAIAQAACgAAAAAAAAAAAAAADwAAAAAAAAAAAAAAAgAAAAAAAAAIAAAAAAAAAKAAAAAAAAAAqAAAAAAAAAACAAAAAAAAALAAAAAAAAAALAAAAAAAAADgAAAAAAAAAHgAAAAAAAAAWAEAAAAAAAACAAAAAAAAAGABAAAAAAAALAAAAAAAAACQAQAAAAAAAO4CAAAAAAAAgAQAAAAAAAACAAAAAAAAAIgEAAAAAAAALAAAAAAAAAC4BAAAAAAAAAAAAAAAAAAAuAQAAAAAAAACAAAAAAAAAMAEAAAAAAAAoAAAAAAAAABgBQAAAAAAAAIAAAAAAAAAaAUAAAAAAACgAAAAAAAAAAAAAAAGAAAACgAAAAAAAAAAAAAAAAAAAAoAAAAAAAAAAAAAAAAAAAAKAAAAAAAAAAAAAAAAAAAACgAAAAAAAAAKAAAAAAAAAAoAAAAAAAAAAgAAAAAAAAAKAAAAAAAAAAAAAAAAAAAA/wMAAAAAAAAAAAC05d/J4HDSMAMAAAAAAAAAtOXfyeBw0jADAAAAAAAAALTl38ngcNIwAwAAAAAAAAC05d/J4HDSMAMAAAAAAAAAtOXfyeBw0jADAAAAAAAAALTl38ngcNIwAwAAAAAAAAC05d/J4HDSMAMAAAAAAAAAtOXfyeBw0jADAAAAAAAAALTl38ngcNIwAwAAAAAAAAC05d/J4HDSMAMAAAAA/wMAAAAAAAAAAAAADAAAABgAAAAkAAAAMAAAADwAAABIAAAAVAAAAGAAAABsAAAAeAAAAAAAAABBbmFoaXRhX3Rlc3RBbmFoaXRhX3Rlc3RBbmFoaXRhX3Rlc3RBbmFoaXRhX3Rlc3RBbmFoaXRhX3Rlc3RBbmFoaXRhX3Rlc3RBbmFoaXRhX3Rlc3RBbmFoaXRhX3Rlc3RBbmFoaXRhX3Rlc3RBbmFoaXRhX3Rlc3T/AwAAAAAAAAAAAABLAAAAlgAAAOEAAAAsAQAAdwEAAMIBAAANAgAAWAIAAKMCAADuAgAAAAAAAHMzOi8vYnVja2V0ZWVyLTFmOWIzNDAzLTQ1NDMtNDg1MS1hOTc4LTRjOGRlN2Q3MTEyOS9yYWplc2gtdGVzdC9lcnJvcmVkLmNzdnMzOi8vYnVja2V0ZWVyLTFmOWIzNDAzLTQ1NDMtNDg1MS1hOTc4LTRjOGRlN2Q3MTEyOS9yYWplc2gtdGVzdC9lcnJvcmVkLmNzdnMzOi8vYnVja2V0ZWVyLTFmOWIzNDAzLTQ1NDMtNDg1MS1hOTc4LTRjOGRlN2Q3MTEyOS9yYWplc2gtdGVzdC9lcnJvcmVkLmNzdnMzOi8vYnVja2V0ZWVyLTFmOWIzNDAzLTQ1NDMtNDg1MS1hOTc4LTRjOGRlN2Q3MTEyOS9yYWplc2gtdGVzdC9lcnJvcmVkLmNzdnMzOi8vYnVja2V0ZWVyLTFmOWIzNDAzLTQ1NDMtNDg1MS1hOTc4LTRjOGRlN2Q3MTEyOS9yYWplc2gtdGVzdC9lcnJvcmVkLmNzdnMzOi8vYnVja2V0ZWVyLTFmOWIzNDAzLTQ1NDMtNDg1MS1hOTc4LTRjOGRlN2Q3MTEyOS9yYWplc2gtdGVzdC9lcnJvcmVkLmNzdnMzOi8vYnVja2V0ZWVyLTFmOWIzNDAzLTQ1NDMtNDg1MS1hOTc4LTRjOGRlN2Q3MTEyOS9yYWplc2gtdGVzdC9lcnJvcmVkLmNzdnMzOi8vYnVja2V0ZWVyLTFmOWIzNDAzLTQ1NDMtNDg1MS1hOTc4LTRjOGRlN2Q3MTEyOS9yYWplc2gtdGVzdC9lcnJvcmVkLmNzdnMzOi8vYnVja2V0ZWVyLTFmOWIzNDAzLTQ1NDMtNDg1MS1hOTc4LTRjOGRlN2Q3MTEyOS9yYWplc2gtdGVzdC9lcnJvcmVkLmNzdnMzOi8vYnVja2V0ZWVyLTFmOWIzNDAzLTQ1NDMtNDg1MS1hOTc4LTRjOGRlN2Q3MTEyOS9yYWplc2gtdGVzdC9lcnJvcmVkLmNzdgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADvAgAAAAAAAAAAvJPp/iRhAAAAAAAAAAAAAGSns7bgDQAAAAAAAAAAAAAs9hokoikAAAAAAAAAAAAA9ESCkWNFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQnc7agjcAAAAAAAAAAAAAvJPp/iRhAAAAAAAAAAAAAMhOZ23BGwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkJ3O2oI3AAAAAAAAAAD/AwAAAAAAAOsmcS93LbuQ8q6fy774HwAAAJdBb7ScOk3sxvMUAAAAAAC/t1DKKUBN7MbzFAAAAIB0X+VdMDlNCBS2hdEAAABQ/STgSHKeVggUtoXRAAAAIDdIhbv5V1gIFLaF0QAAAAAA2C/KY+ctZIVuGHf4HwAAAPIKAfBbPk3sxvMUAAAAAADGUI3b5UwIFLaF0QAAAAAAjN/XwZxWCBS2hdEAAAD/////AAAAAA==");
          for(Object row:rows){
              System.out.println(row);
          }
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }
    public static void parseRainbowResults(){
        byte[] data = Data.getData();
        ArrowUtil util = new ArrowUtil();
       List<Object> rows= util.getRows(data);
        for(Object row : rows)
            System.out.println(row);

    }
    public static void parseRainbowResultsFromString() throws SQLException {
        byte[] data = Data.getData();
        ArrowUtil util = new ArrowUtil();
        List<Object> rows= util.getResults(Data.getStringData());
        for(Object row : rows)
            System.out.println(row);

    }*/
    private static void getRecordsViaV2(String tableName, Connection con) throws SQLException {
        String query = "select * from " + tableName;
        System.out.println(query);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int recordCount = 0;
        while (rs.next()) {
            int colCount = rs.getMetaData().getColumnCount();
            Map<String,Object> row = new HashMap<>();
            for(int i=1;i<=colCount;++i) {
                row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
            }
            //System.out.println(row);
            recordCount++;
            if(recordCount%1000 == 0) {
                System.out.println("got " + recordCount + " records");
            }

        }
    }


    private static void getRecordsViaRainbow(String tableName, Connection con) throws SQLException {
        String query ="SELECT *  FROM football_players__dll LIMIT 50";
        //String query ="SELECT player_name__c, height__c  FROM football_players__dll LIMIT 50";
        System.out.println(query);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        int recordCount = 0;
        while (rs.next()) {
            QueryServiceResultSetMetaData resultSetMetaData = (QueryServiceResultSetMetaData) rs.getMetaData();
            int colCount = resultSetMetaData.getColumnCount();
            Map<String,Object> row = new HashMap<>();
            for(int i=0;i<colCount;++i) {
                String type =resultSetMetaData.getColumnTypeName(i);
                String columnName = resultSetMetaData.getColumnName(i);
                int position = resultSetMetaData.getColumnNameToPosition().get(columnName);
                if(type.equals("TIMESTAMP WITH TIME ZONE"))
                    try {
                        row.put(columnName, rs.getTimestamp(i));
                    } catch (Exception e) {
                        row.put(columnName, new Date());
                    }
                else
                    row.put(columnName, rs.getObject(position));
            }
            System.out.println(row);
            recordCount++;
            if(recordCount%1000 == 0) {
                System.out.println("got " + recordCount + " records");
            }
        }
    }
    private static void getRecordsViaV1Repeatedly(String tableName, Connection con) throws SQLException {


        String query = "select * from " + tableName + "  order by 1 asc";
        System.out.println(query);
        //while(true) {
//            Instant startTime = Instant.now();
//            try{
                Statement st = con.createStatement();
                st.executeQuery(query);
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//            Instant endTime = Instant.now();
//            System.out.println("Time taken in seconds:" + Duration.between(startTime, endTime).toMillis()/1000);
        //}
    }

    private static void getRecordsViaV1(String tableName, Connection con) throws SQLException {
        long countOfRecords=150000;
        int maxLimit = 4999;
        for(int offset=0;offset<countOfRecords;offset=offset+maxLimit) {
            String query = "select * from " + tableName + "  order by 1 limit " + maxLimit + " offset " + offset;
            System.out.println(query);
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(query);
            int recordCount = 0;
            while (rs.next()) {
                int colCount = rs.getMetaData().getColumnCount();
                Map<String,Object> row = new HashMap<>();
                for(int i=1;i<=colCount;++i) {
                    row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                //System.out.println(row);
                recordCount++;
            }
            System.out.println("got " + recordCount + " records");
        }
    }
}
