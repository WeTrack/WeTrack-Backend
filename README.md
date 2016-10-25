# WeTrack-Backend

Java backend project for WeTrack, our HKUST CSIT 5510 course project.

## Test Run

Execute command:

```
gradlew appRun
```

The service classes in `service` module should be listening to requests come from `http://localhost:8080/service/`.

The project will open a MongoDB connection to `localhost:27017` anonymously and use its `wetrack` database. Make sure your MongoDB instance is correctly configured and running when you start the project.

## Future Task

- [x] Implement service for user login and token management.
- [x] Write unit test cases for service class.
- [x] Refactor the service classes to use `Gson`.
- [x] Implement client for user login and management.
- [x] Write unit test cases for client class.
- [x] Write integration test cases for user login and management.
- [x] Modularize the service classes in a more granular manner.
- [x] Implement Location uploading and query.
- [ ] Implement Friend adding.
- [ ] Implement Chat creating.
- [ ] Implement Message sending.
- [ ] Implement personal icon uploading.
- [ ] Refactor the service classes to add Spring Aspect for logging.
