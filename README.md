# Salesforce-CDP-jdbc
Enables a jdbc connection to Salesforce CDP.

# How to use:
The JDBC driver can be used with the tools like Tableau or independently with any Java JDBC client to connect to Salesforce CDP and retreive the data.

Get the Driver from this repo.

# Connected App
To use JDBC driver, admins will have to create a connected app in the Salesforce org. Please follow the steps mentioned in Salesforce CDP setup in below link to create a connected app. 
If you create a connected app using below link, you need not pass client Id and secret mentioned in the code examples below. Or else please pass client Id and secret.

https://extensiongallery.tableau.com/connectors/270

# Java Code
Load the Driver into your Java classPath

Create Connection with oAuth tokens
```
   Class.forName("com.salesforce.cdp.queryservice.QueryServiceDriver");
   Properties properties = new Properties();
   properties.put("coreToken", <Core oAuth token>);
   properties.put("refreshToken", <Refresh Token>);
   properties.put("clientId", <Client Id of the connected App>);
   properties.put("clientSecret", <Client Secret of the connected App>);

   Connection connection =  DriverManager.getConnection("jdbc:queryService-jdbc:https://login.salesforce.com", properties);
```
Create Connection with UserName and Password

```
Class.forName("com.salesforce.cdp.queryservice.QueryServiceDriver");
   Properties properties = new Properties();
   properties.put("user", <UserName>);
   properties.put("password", <Password>);

   Connection connection =  DriverManager.getConnection("jdbc:queryService-jdbc:https://login.salesforce.com", properties);
```

Create Statements/ Prepared Statements to execute Query and get ResultSet
```
PreparedStatement preparedStatement = connection.prepareStatement("select FirstName__c, BirthDate__c, YearlyIncome__c from Individual__dlm where FirstName__c = ? and YearlyIncome__c > ?");
   preparedStatement.setString(0, "Angella");
   preparedStatement.setInt(1, 1000);

ResultSet  resultSet = preparedStatement.executeQuery();

while (resultSet.next()) {
       log.info("FirstName : {}, BirthDate__c : {}, YearlyIncome__c : {}", resultSet.getString("FirstName__c"), resultSet.getTimestamp("BirthDate__c"), resultSet.getInt("YearlyIncome__c"));
```


# Python Code
The JDBC driver can also be used with python. We need JaydebeAPI wrapper on top of the JDBC driver to call JDBC methods.

Install JaydebeAPI using PIP
```
pip install JayDeBeApi
```
Sample Python Code

```
import jaydebeapi

properties = {
    'user': "<UserName>",
    'password': "<Password>"
}

conn = jaydebeapi.connect("com.salesforce.cdp.queryservice.QueryServiceDriver", "jdbc:queryService-jdbc:https://login.salesforce.com", properties, "<Complete Path to JDBC driver>")

curs = conn.cursor()
curs.execute('SELECT * FROM ssot__Individual__dlm')
data = curs.fetchall()
```

# Notes:
    
    Add order by clause in the query to fetch the paginated results for V1 API.
