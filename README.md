# User Service

## Overview

The User Service is a central component of the application. This microservice is responsible for handling user information and providing basic CRUD operations in order to manage user data effectively. The User Service is central to the application because it is the service responsible for generating a unique user ID for each user. This user ID is referenced by the rest of the services.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Testing](#testing)


## Architecture

The User Service is built with the following technologies:
- **Backend**: Spring Boot
- **Database**: PostgreSQL
- **Testing**: JUnit
- **Communication**: RESTful APIs

## Installation

### Prerequisites

- JDK 17
- Maven
- PostgreSQL setup

### Steps

1. Clone the respository:
   ```bash
   git clone https://github.com/My-Budget-Buddy/Budget-Buddy-UserService.git
   cd userservice
   ```

2. Build the project:
    ```bash
    mvn clean install
    ```

3. Run the application:
    ```bash
    mvn spring-boot:run
    ```

## Configuration

Configure your database and other settings in the `application.yaml` file located in `src/main/resources`:

```yaml
spring:
  datasource:
    url: jdbc:postgres://localhost:5432/yourdbname
    username: yourusername
    password: yourpassword
  jpa:
    hibernate:
      ddl-auto: update
```

## Usage

The User Service provides endpoints for managing user transactions. You can access these endpoints using tools like Postman or CURL.

## API Documentation

### Endpoints

#### Get All Users
- **URL**: ```GET /users```
- **Description**: Create a new transaction.
- **Response**:
    ```json
    [
      {
        "id": 1,
        "email": "JohnDoe@gmail.com",
        "firstName": "John",
        "lastName": "Doe"
      },
      {
        "id": 2,
        "email": "JaneDoe@gmail.com",
        "firstName": "Jane",
        "lastName": "Doe"
      }
    [
    ```

#### Get User By ID
- **URL**: ```GET /users/user```
- **Description**: Retrieve the record of a single user based on a user ID.
- **Response**:
    ```json
      {
        "id": 1,
        "email": "JohnDoe@gmail.com",
        "firstName": "John",
        "lastName": "Doe"
      }
    ```

#### Create a User
- **URL**: ``` POST /users```
- **Description**: Create a new User record in the database.
- **Request**:
    ```json
      {
        "id": null,
        "email": "JaneDoe@gmail.com",
        "firstName": "Jane",
        "lastName": "Doe"
      }
    ```
- **Response**:
    ```json
      {
        "id": 1,
        "email": "JaneDoe@gmail.com",
        "firstName:" "Jane",
        "lastName": "Doe"
      {
    ```

#### Update a User
- **URL**: ``` PUT /users```
- **Description**: Update a User record in the database.
- **Request**:
    ```json
      {
        "id": 1,
        "email": "JaneDoe2@yahoo.com",
        "firstName": "Jane",
        "lastName": "Doe"
      }
    ```
- **Response**:
  ```json
        {
          "id": 1,
          "email": "JaneDoe2@yahoo.com",
          "firstName": "Jane",
          "lastName": "Doe"
        }
  ```

#### Delete a User
- **URL**: ``` DELETE /users```
- **Description**: Delete a User record from the database


## Testing

To run the tests, use the following command:

``bash
mvn test
```
