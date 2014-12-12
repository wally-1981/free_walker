:: Please ensure install and start MongoDB and MySQL before launching this server in Prod mode.
::
:: Start MySQL: (The path to mysqld may vary depending on the install location of MySQL on your system.)
:: c:\MySQL\MySQL Server 5.6\bin\mysqld
::

:: Start MongoDB: (The path to mongod may vary depending on the install location of MongoDB on your system.)
c:\mongo_server\bin\mongod -dbpath c:\mongo_server\data

java -jar ./com.free.walker.service.itinerary-0.0.2.jar -Prod