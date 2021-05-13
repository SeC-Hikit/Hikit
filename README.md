# S&C - Sentieri e Cartografia
S&C is an open API that manages high level geo and meta data related to trails.
It supports accessibility-notifications, maintenance planning, generic/specific POIs and other features all linked with trails.

## Build

### Prerequisites
- JDK 8
- Maven 3

### Build
After cloning the repo, simply run `mvn install -f root/pom.xml`.

### Deployment Build
To incude all dependencies in a uber-jar, run `mvn install -P package`.

### Test
There are two main types of tests written to address the S&C QA: unit tests and integration tests.
The latter require the dependencies (see Run->Prerequisites) to be up and running as they test operations connecting to them.
Once the dependencies are made available, simple execute `mvn install -P it`. 

## Run

### Prerequisites
S&C requires the following services up and running in order to fully operate:
- [MongoDB 4.x](https://www.mongodb.com)
- [OpenElevation](https://open-elevation.com/)

To get these up and running in the least time possible, use the included `docker-compose.yml`:
```
cd docker
sh rset_download.sh
docker-compose up
```
Mind that the above set-up is for *test/dev only* as all services are exposed in the network and they do not
use authentication.

### Properties

Configure the `application.properties` file to fit the application runtime for your needs.
For more on the system properties, check out the repo wiki pages.

## The API

### OpenAPI
By default, the openAPI v3 specs are exposed at `api/v<n>/api-docs`.
Alternatively, to interact directly with the REST API, the Swagger UI can be found at `api/v<n>/api-ui.html`. 

### The project UI
Once the API is running, you could make use of a user interface to make REST calls to it.
[A UI for the Bologna Alpine club](https://github.com/loreV/SeC-Frontend) has been developed to visualize and simplify working with trails data.

## Vision and challenges
S&C aims to be a user focused API, that makes easy to gather and save rich data about trails.
It aims to provide a first point of access for trails management while integrating to central storage via OSM by REST APIs.
