# a360-cdp-jdbc
Enables a jdbc connection to CDP Query Service

# How to use:
The JDBC driver can be used with the tools like Tableau or independently with any Java JDBC client to connect to CDP Query service and retreive the data.

Get the Driver from this repo.

Load the Driver into your Java classPath

Create Connection
```
   Class.forName("com.salesforce.cdp.queryservice.QueryServiceDriver");
   Properties properties = new Properties();
   properties.put("coreToken", <Core oAuth token>);
   properties.put("refreshToken", <Refresh Token>);
   properties.put("clientId", <Client Id of the connected App>);
   properties.put("clientSecret", <Client Secret of the connected App>);

   Connection connection =  DriverManager.getConnection("jdbc:salesforce-cdp-jdbc:<Salesforce Core instance URL>", properties);
```
Create Statements/ Prepared Statements to execute Query and get ResultSet
```
PreparedStatement preparedStatement = connection.prepareStatement("select FirstName__c, BirthDate__c, YearlyIncome__c from Individual__dlm where FirstName__c = ? and YearlyIncome__c > ?");
   preparedStatement.setString(1, "Angella");
   preparedStatement.setInt(1, 1000);

ResultSet  resultSet = preparedStatement.executeQuery();

while (resultSet.next()) {
       log.info("FirstName : {}, BirthDate__c : {}, YearlyIncome__c : {}", resultSet.getString("FirstName__c"), resultSet.getTimestamp("BirthDate__c"), resultSet.getInt("YearlyIncome__c"));
```
