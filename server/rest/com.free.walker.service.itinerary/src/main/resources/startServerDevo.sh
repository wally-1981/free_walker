# Please ensure install and start MySQL before launching this server in Devo mode.
#
# Start MySQL:
# sudo /usr/local/mysql/support-files/mysql.server start

# Create the combination keystore/truststore for the client and service.
# Note you can create separate keystores/truststores for both if desired
keytool -genkeypair -validity 365 -v -alias myservicekey -keystore ServiceKeystore.jks -keyalg RSA -storetype JKS -dname "cn=127.0.0.1,ou=TechDept,o=FreeWalker,l=Haidian,st=Beijing,c=CN" -keypass skpass -storepass sspass
keytool -genkeypair -validity 365 -v -alias myclientkey -keystore ClientKeystore.jks -keyalg RSA -storetype JKS -dname "cn=ServiceClient,ou=TechDept,o=FreeWalker,l=Haidian,st=Beijing,c=CN" -keypass ckpass -storepass cspass

# Place server public cert in client key/truststore
keytool -export -rfc -v -keystore ServiceKeystore.jks -alias myservicekey -file MyService.cer -storepass sspass
keytool -import -noprompt -trustcacerts -file MyService.cer -alias myservicekey -keystore ClientKeystore.jks -storepass cspass

# Place client public cert in service key/truststore
# Note this needs to be done only if you're requiring client authentication as configured in resources/ServerConfig.xml
keytool -export -rfc -v -keystore ClientKeystore.jks -alias myclientkey -file MyClient.cer -storepass cspass
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
java -jar ./com.free.walker.service.itinerary-0.0.15.jar -Devo
