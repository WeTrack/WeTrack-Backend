# WeTrack-Backend

[![Build Status](https://travis-ci.org/WeTrack/WeTrack-Backend.svg?branch=master)](https://travis-ci.org/WeTrack/WeTrack-Backend)

Java backend project for WeTrack, our HKUST CSIT 5510 course project.

## Test Run

Execute command:

```
gradlew appRun
```

The service classes in `service` module should be listening to requests come from `http://localhost:8080/service/`.

The project will open a MongoDB connection to `localhost:27017` anonymously and use its `wetrack` database. Make sure your MongoDB instance is correctly configured and running when you start the project.

## Frameworks and Technology

- [Spring](http://projects.spring.io/spring-framework/) for Dependency Injection and Management.
- [Jersey](https://jersey.java.net/) for RESTful Servlet Implementation.
- [Tyrus](https://tyrus.java.net/) for WebSocket Servlet Implementation.
- [Gson](https://github.com/google/gson) for JSON Serialization and Deserialization.
- [Retrofit](https://square.github.io/retrofit/) and [RxJava](https://github.com/ReactiveX/RxJava) for Asynchronous Client Request.
- [Guava](https://github.com/google/guava) for Advanced Data Structure.
- [SLF4J](http://www.slf4j.org/) and [Log4j](https://logging.apache.org/log4j/1.2/) for Logging.

## Future Task

- [x] Implement service for user login and token management.
- [x] Write unit test cases for service class.
- [x] Refactor the service classes to use `Gson`.
- [x] Implement client for user login and management.
- [x] Write unit test cases for client class.
- [x] Write integration test cases for user login and management.
- [x] Modularize the service classes in a more granular manner.
- [x] Implement Location uploading and query.
- [x] Implement Friend adding.
- [x] Implement Chat creating.
- [x] Implement chat message service.
- [x] Implement WebSocket notification pushing.
- [ ] Deploy version `0.1`.
- [ ] Write test cases for WebSocket service.
- [ ] Migrate from Jersey to SpringMVC.
- [ ] Implement file and image uploading.
- [ ] Implement friend invitation.
- [ ] Implement group invitation.
- [ ] Implement permission framework.
- [ ] Deploy version `0.2`.
- [ ] Refactor the service classes to add Spring Aspect for logging.
- [ ] Implement P2P Encryption.
- [ ] Deploy version `0.3` in `HTTPS` and `TLS`

