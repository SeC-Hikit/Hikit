# S&C - Sentieri e Cartografia
S&C is an open API that manages high level geo and meta data related to trails.
It supports accessibility-notifications, maintenance planning, generic/specific POIs and other features all linked with trails.

## Build
In order to build, run `mvn install` on the project root.
Requirements:
- JDK 8
- Maven 3

## Test
There are two main types of tests written to address the S&C QA: unit tests and integration tests.
The latter require the dependencies to be up and running as they test the connection with them. In order to run them, execute `mvn install --P it`. 

## Dependencies
S&C needs the following services to run in order to fully operate:
- [MongoDB 4.x](https://www.mongodb.com)
- [OpenElevation](https://open-elevation.com/)

The recommended way to get these up and running is trough [docker](https://hub.docker.com/_/mongo).

### Run
Upon having the dependencies up-and-running, you shall make a MongoDB instance available.
Once you do so, configure the properties file:

```
# matches the open-elevation local port
service.altitude.port=8080  

# db settings
db.name=your_db_name
db.uri=mongodb://localhost:30000

# trail GPX Storage on local filesystem
trail.storage.path=your/trail/to/server
```
### UI
Once the API is running, you could make use of a user interface to make REST calls to it.
[A sample UI for the Bologna Alpine club](https://github.com/loreV/SeC-Frontend) has been developed to visualize and simplify working with trails data.

## Vision and challenges
S&C aims to be a user focused API, that easily allow to gather and save rich data about trails, while connecting this data to a community accessibility notifications, planned maintenance data and POI.
It aims to provide a first point of access for trails management while integrating to central storage via OSM by REST APIs.
