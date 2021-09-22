# S&C - Sentieri e Cartografia
S&C is a community effort to create a service that manages high level geo and meta data related to trails.
It is intended to provide trail support teams (like the [CAI](https://www.cai.it/) ones) with a rich API to update and maintain a trail library, while making this data available to users.

Currently, the library supports accessibility-notifications, user reports, maintenance planning, generic/specific POIs and other features all linked with trails.

## Build
### Prerequisites
- JDK 8
- Maven 3

### Development build
After cloning the repo, simply run `mvn install -f root/pom.xml`.

### Deployment Build
To incude all dependencies in a uber-jar, run `mvn install -P package`.

### Test
There are two main types of tests written to address the S&C QA: unit test and integration test.
The latter requires the [dependencies](#Dependencies) to be up and running as it tests application operations connecting to them.
Once the dependencies are made available, to run the integration tests, simply execute `mvn install -P it`. 

## Run

### Dependencies
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

### S&C on Docker
In case you want to test run S&C in combination with all its dependencies:
- Run a deployment build (see [Deployment build](#Deployment-build))
- Run `sh docker/build.sh`
- Run `docker compose -f docker-compose.yml -f docker-compose.cluster.yml up`
The service will be available on `http://localhost:8990/api/v1`

### Production
As for production, S&C takes advantage of a Keycloak instance, as it does not implement user and roles natively.
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