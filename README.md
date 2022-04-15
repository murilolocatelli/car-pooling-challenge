# Car Pooling Service Challenge

Design/implement a system to manage car pooling.

At Cabify we provide the service of taking people from point A to point B.
So far we have done it without sharing cars with multiple groups of people.
This is an opportunity to optimize the use of resources by introducing car
pooling.

You have been assigned to build the car availability service that will be used
to track the available seats in cars.

Cars have a different amount of seats available, they can accommodate groups of
up to 4, 5 or 6 people.

People requests cars in groups of 1 to 6. People in the same group want to ride
on the same car. You can take any group at any car that has enough empty seats
for them. If it's not possible to accommodate them, they're willing to wait.

Once they get a car assigned, they will journey until the drop off, you cannot
ask them to take another car (i.e. you cannot swap them to another car to
make space for another group). In terms of fairness of trip order: groups are
served in the order they arrive, but they ride opportunistically.

For example: a group of 6 is waiting for a car and there are 4 empty seats at
a car for 6; if a group of 2 requests a car you may take them in the car for
6 but only if you have nowhere else to make them ride. This may mean that the
group of 6 waits a long time, possibly until they become frustrated and
leave.

## API

To simplify the challenge and remove language restrictions, this service must
provide a REST API which will be used to interact with it.

This API must comply with the following contract:

### GET /status

Indicate the service has started up correctly and is ready to accept requests.

Responses:

* **200 OK** When the service is ready to receive requests.

### PUT /cars

Load the list of available cars in the service and remove all previous data
(existing journeys and cars). This method may be called more than once during 
the life cycle of the service.

**Body** _required_ The list of cars to load.

**Content Type** `application/json`

Sample:

```json
[
  {
    "id": 1,
    "seats": 4
  },
  {
    "id": 2,
    "seats": 7
  }
]
```

Responses:

* **200 OK** When the list is registered correctly.
* **400 Bad Request** When there is a failure in the request format, expected
  headers, or the payload can't be unmarshaled.

### POST /journey

A group of people requests to perform a journey.

**Body** _required_ The group of people that wants to perform the journey

**Content Type** `application/json`

Sample:

```json
{
  "id": 1,
  "people": 4
}
```

Responses:

* **200 OK** or **202 Accepted** When the group is registered correctly
* **400 Bad Request** When there is a failure in the request format or the
  payload can't be unmarshaled.

### POST /dropoff

A group of people requests to be dropped off. Wether they traveled or not.

**Body** _required_ A form with the group ID, such that `ID=X`

**Content Type** `application/x-www-form-urlencoded`

Responses:

* **200 OK** or **204 No Content** When the group is unregistered correctly.
* **404 Not Found** When the group is not to be found.
* **400 Bad Request** When there is a failure in the request format or the
  payload can't be unmarshaled.

### POST /locate

Given a group ID such that `ID=X`, return the car the group is traveling
with, or no car if they are still waiting to be served.

**Body** _required_ A url encoded form with the group ID such that `ID=X`

**Content Type** `application/x-www-form-urlencoded`

**Accept** `application/json`

Responses:

* **200 OK** With the car as the payload when the group is assigned to a car.
* **204 No Content** When the group is waiting to be assigned to a car.
* **404 Not Found** When the group is not to be found.
* **400 Bad Request** When there is a failure in the request format or the
  payload can't be unmarshaled.

## Tooling

In this repo you may find a [.gitlab-ci.yml](./.gitlab-ci.yml) file which
contains some tooling that would simplify the setup and testing of the
deliverable. This testing can be enabled by simply uncommenting the final
acceptance stage.

Additionally, you will find a basic Dockerfile which you could use a
baseline, be sure to modify it as much as needed, but keep the exposed port
as is to simplify the testing.

You are free to modify the repository as much as necessary to include or remove
dependencies, but please document these decisions using MRs or in this very
README adding sections to it, the same way you would be generating
documentation for any other deliverable. We want to see how you operate in a
quasi real work environment.

# Solution:

To resolve the proposed challenge, was use Java program language with Spring Boot.
For dependency management and build was used Maven.

For database was used H2 Database Engine, a free and small relational database.
This database can be in-memory and disk-based tables, for this project was used
in-memory mode, to facilitate the execution of acceptance tests scripts from the
image `cabify/challenge`. In a real project, would be used other advanced database 
disk-based, like MySQL, MariaDB, PostgreSQL, Oracle, and others.

The build process is done through Maven, which will perform the code checklist analyze,
tests execution, generation of test coverage report and creation of execution artifact.
There is a built-in maven in the project, to facilitate the commands execution
using `./mvnw`. After that, run the generated `.jar` and Spring Boot will up the
embedded server, which runs the flyway migrations automatically to create database
schema. And the server will waiting to receive requests in the port 8080.

## Instructions:

### Requirements for execution environment:

```sh
Java 12
```

### Requirements for development environment:

```sh
Java 12
Lombok plugin installed on IDE
```

* Lombok plugin for Eclipse: https://projectlombok.org/setup/eclipse
* Lombok plugin for IntelliJ: https://projectlombok.org/setup/intellij

### Building with tests

```sh
./mvnw clean test
```

### Run coverage

```sh
./mvnw clean test jacoco:report
```

To view the test coverage report, open the generated file `target/site/jacoco/index.html` in the browser.

### Running application

```sh
java -jar target/car-pooling-challenge-X.X.X.jar
```

### Access Swagger

```sh
http://<host>:<port>/swagger-ui.html
```

### Delivery

The CI process is divided in 3 stages:

* The first is the build stage. In this stage, build is done through Maven. Is
performed the code checklist analyze, tests execution and generation of artifact `.jar`.

* The second is the build image stage. In this stage, the artifact generated on the
previous stage is used to generate the docker image. This image is generated from the
Dockerfile script, which runs the command `java -jar` to up the server and exposes the
port 8080. In this stage is also done login in docker hub and push of generated image.

* The third and last stage is acceptance. In this stage, is done the acceptance through
image `cabify/challenge:latest`, which uses the image generated in the previous stage.
Is executed the script `/harness`, which tests if all endpoints and responses are correct
according the proposed challenge.
