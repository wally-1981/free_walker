# Please ensure install and start MongoDB and MySQL before launching this server in Prod mode.
#
# Start MongoDB: (The path to mongod may vary depending on the install location of MongoDB on your system.)
# /opt/mongo_server/bin/mongod -dbpath /opt/mongo_server/data
#
# Start MySQL: (The path to mysqld may vary depending on the install location of MySQL on your system.)
# /opt/MySQL/MySQL Server 5.6/bin/mysqld
#

java -jar ./com.free.walker.service.itinerary-0.0.1.jar -Prod