# Please ensure install and start MongoDB, ElasticSearch and MySQL before launching this server in Prod mode.
#
# Start MySQL:
# sudo /usr/local/mysql/support-files/mysql.server start
# Start MongoDB: (The path to mongod may vary depending on the install location of MongoDB on your system. You may need to create the data directory manually.)
# sudo /Applications/mongo_server/bin/mongod -dbpath /Applications/mongo_server/data
# Start ElasticSearch: (The path to elasticsearch may vary depending on the install location of ElasticSearch on your system.)
# sudo /Applications/elastic_search_server/bin/elasticsearch

# Create the combination keystore/truststore for the client and service.
# Note you can create separate keystores/truststores for both if desired
keytool -genkeypair -validity 365 -alias myservicekey -keystore ServiceKeystore.jks -dname "cn=127.0.0.1" -keypass skpass -storepass sspass
keytool -genkeypair -validity 365 -alias myclientkey -keystore ClientKeystore.jks -keypass ckpass -storepass cspass

# Place server public cert in client key/truststore
keytool -export -rfc -keystore ServiceKeystore.jks -alias myservicekey -file MyService.cer -storepass sspass
keytool -import -noprompt -trustcacerts -file MyService.cer -alias myservicekey -keystore ClientKeystore.jks -storepass cspass

# Place client public cert in service key/truststore
# Note this needs to be done only if you're requiring client authentication as configured in resources/ServerConfig.xml
keytool -export -rfc -keystore ClientKeystore.jks -alias myclientkey -file MyClient.cer -storepass cspass
keytool -import -noprompt -trustcacerts -file MyClient.cer -alias myclientkey -keystore ServiceKeystore.jks -storepass sspass

# Place the keystore and cert files before starting the server.
if [ ! -d keystore ]; then
    mkdir keystore
fi

mv ServiceKeystore.jks ./keystore
mv ClientKeystore.jks ./keystore
mv MyService.cer ./keystore
mv MyClient.cer ./keystore

# Launch the server
java -jar ./com.free.walker.service.itinerary-0.0.13.jar -Prod
