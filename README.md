# Hikit Service
Hikit central service manages high level geo and meta-data related to trails.
It is intended to provide organizations (like [CAI](https://www.cai.it/)) with a rich set of features to update and maintain a trail rich format library, while making this data available to users.

Currently, it supports a wide set of features like accessibility-notifications, user trail accidents reports, maintenance planning, generic/specific POIs and other features all linked with trails.

## Build
### Prerequisites
- JDK 11
- Maven 3

### Development build
After cloning the repo, simply run `mvn install -f root/pom.xml`.

### Deployment Build
To include all dependencies in a portable uber-jar, run `mvn install -P package`.

### Testing
There are two main types of tests written to address the Hikit QA: unit and integration tests.
The latter requires the [dependencies](#Dependencies) to be up and running as it runs application operations connecting to them.
Once the dependencies are made available, simply execute `mvn install -P it`. 

## Run

### Dependencies
Hikit requires the following services up and running in order to fully operate:
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

### Hikit on Docker
In case you want to test run Hikit in combination with all its dependencies:
- Run a deployment build (see [Deployment build](#Deployment-build))
- Run `sh docker/build.sh`
- Run `docker-compose -f ./docker/docker-compose.yml -f ./docker/docker-compose.cluster.yml up`
The service will be available at `http://localhost:8990/api/v<N>` (where `<N>` stands for the version number)

### Production
As for production, Hikit takes advantage of a Keycloak instance, as it does not implement user and roles natively.
In order to configure a Keycloak instance, see the Keycloak official documentation.

### Properties
Configure the `application.properties` file to fit the application runtime for your needs.
For more on the system properties, check out the repo wiki pages.

## The API

### OpenAPI
By default, the openAPI v3 specs are exposed at `api/v1/api-docs`.
Alternatively, to interact directly with the REST API, the Swagger UI can be found at `api/v1/api-ui.html`.

### The UI
Once the API is running, you could make use of a user interface to make REST calls to it.
[The official UI](https://github.com/loreV/SeC-Frontend) is being developed to visualize and simplify working with trails data.
