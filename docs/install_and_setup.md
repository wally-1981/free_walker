#Installation

----------

**MySQL**

Please refer to [MySQL][1] official site for the general installation guide.

Please refer to [config.properties][2] for the server port and account settings required by this application server.

Ensure the server was started before proceeding:

**Windows**: `c:\MySQL\MySQL Server 5.6\bin\mysqld`

**Mac OS**: `sudo /usr/local/mysql/support-files/mysql.server start`

----------

**MongoDB**

Please refer to [MongoDB][3] official site for the general installation guide.

Please refer to [config.properties][4] for the server port settings required by this application server.

**Windows**: `c:\mongo_server\bin\mongod -dbpath c:\mongo_server\data`

**Mac OS**: `sudo /Applications/mongo_server/bin/mongod -dbpath /Applications/mongo_server/data`

----------

**ElasticSearch**

Please refer to [ElasticSearch][5] official site for the general installation guide.

Please refer to [config.properties][6] for the server port settings required by this application server.

**Windows**: `c:\elastic_search_server\bin\elasticsearch`

**Mac OS:** `sudo /Applications/elastic_search_server/bin/elasticsearch`

----------

**curl**

The *curl* command is required for the later setup process, please ensure it was install in your operation system.

----------

#Setup

**MySQL Initialization**

Please execute [this sql][7] to setup DB schema and initialization data for MySQL.

----------

**ElasticSearch Initialization**

Please execute [this][8] or [this][9] script to register the query templates.

----------

#Startup

For **Windows**, please refer to [this script][10] to start this application server.

For **Mac**, please refer to [this script][11] to start this application server.

**Reminder**
These two sripts can not be executed directly to start the server. Please read the scripts line by line and execute those commands one by one.

  [1]: http://www.mysql.com
  [2]: https://github.com/wally-1981/free_walker/blob/master/server/rest/com.free.walker.service.itinerary/src/main/resources/com/free/walker/service/itinerary/dao/config.properties
  [3]: http://www.mongodb.org
  [4]: https://github.com/wally-1981/free_walker/blob/master/server/rest/com.free.walker.service.itinerary/src/main/resources/com/free/walker/service/itinerary/dao/config.properties
  [5]: https://www.elastic.co/products/elasticsearch
  [6]: https://github.com/wally-1981/free_walker/blob/master/server/rest/com.free.walker.service.itinerary/src/main/resources/com/free/walker/service/itinerary/dao/config.properties
  [7]: https://github.com/wally-1981/free_walker/blob/master/server/rest/com.free.walker.service.itinerary/src/main/resources/db/schema/basic.sql
  [8]: https://github.com/wally-1981/free_walker/blob/master/server/rest/com.free.walker.service.itinerary/src/main/resources/db/setup.sh
  [9]: https://github.com/wally-1981/free_walker/blob/master/server/rest/com.free.walker.service.itinerary/src/main/resources/db/setup.bat
  [10]: https://github.com/wally-1981/free_walker/blob/master/server/rest/com.free.walker.service.itinerary/src/main/resources/startServer.bat
  [11]: https://github.com/wally-1981/free_walker/blob/master/server/rest/com.free.walker.service.itinerary/src/main/resources/startServer.sh
