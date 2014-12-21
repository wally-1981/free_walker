# Please ensure install and start MongoDB and MySQL before launching this server in Prod mode.

# Start MySQL: (The path to mysqld may vary depending on the install location of MySQL on your system.)
service mysql start

# Start MongoDB: (The path to mongod may vary depending on the install location of MongoDB on your system.)
service mongod start

java -jar ./com.free.walker.service.itinerary-0.0.3.jar -Prod