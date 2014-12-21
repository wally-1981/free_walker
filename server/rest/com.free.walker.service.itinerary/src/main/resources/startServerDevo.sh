# Please ensure install and start MySQL before launching this server in Devo mode.

# Start MySQL: (The path to mysqld may vary depending on the install location of MySQL on your system.)
service mysql start

java -jar ./com.free.walker.service.itinerary-0.0.3.jar -Devo