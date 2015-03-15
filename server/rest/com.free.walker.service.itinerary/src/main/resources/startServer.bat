:: Please ensure install and start MongoDB and MySQL before launching this server in Prod mode.
::
:: Start MySQL: (The path to mysqld may vary depending on the install location of MySQL on your system.)
:: c:\MySQL\MySQL Server 5.6\bin\mysqld

:: Start MongoDB: (The path to mongod may vary depending on the install location of MongoDB on your system.)
c:\mongo_server\bin\mongod -dbpath c:\mongo_server\data

:: Start ElasticSearch: (the path to elasticsearch may vary depending on the install location of ElasticSearch on your system.)
c:\elastic_search_server\bin\elasticsearch

:: Create the combination keystore/truststore for the client and service.
:: Note you can create separate keystores/truststores for both if desired
keytool -genkeypair -validity 730 -alias myservicekey -keystore ServiceKeystore.jks -dname "cn=<Server Hostname>" -keypass skpass -storepass sspass
keytool -genkeypair -validity 730 -alias myclientkey -keystore ClientKeystore.jks -keypass ckpass -storepass cspass

:: Place server public cert in client key/truststore
keytool -export -rfc -keystore ServiceKeystore.jks -alias myservicekey -file MyService.cer -storepass sspass
keytool -import -noprompt -trustcacerts -file MyService.cer -alias myservicekey -keystore ClientKeystore.jks -storepass cspass

:: Place client public cert in service key/truststore
:: Note this needs to be done only if you're requiring client authentication as configured in resources/ServerConfig.xml
keytool -export -rfc -keystore ClientKeystore.jks -alias myclientkey -file MyClient.cer -storepass cspass
keytool -import -noprompt -trustcacerts -file MyClient.cer -alias myclientkey -keystore ServiceKeystore.jks -storepass sspass

java -jar ./com.free.walker.service.itinerary-0.0.11.jar -Prod