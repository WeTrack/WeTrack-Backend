package com.wetrack.service.user;

import com.wetrack.model.User;
import com.wetrack.test.WeTrackServerTest;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class UserCreateTest extends WeTrackServerTest {

    @Test
    public void testCreatingUser() {
        User newUser = new User(username, password, nickname);

        Response response = post("/users", newUser);

        logResponse(response, "user create");
        assertReceivedCreatedMessage(response, "/users/" + username);
    }

    @Test
    public void testCreatingDuplicateUser() {
        createUserWithAssertion();

        User newUser = new User(username, nickname, password);
        newUser.setEmail(email);

        Response response = post("/users", newUser);

        logResponse(response, "duplicate user create");
        assertReceivedNonemptyMessage(response, 403);
    }

    @Test
    public void testCreatingUserWithEmptyField() {
        User newUser = new User();

        Response response = post("/users", newUser);

        logResponse(response, "user create with empty username");
        assertReceivedNonemptyMessage(response, 400); // Bad Request as username is empty

        newUser.setUsername(username);

        response = post("/users", newUser);

        logResponse(response, "user create with empty password");
        assertReceivedNonemptyMessage(response, 400); // Bad Request as password is empty
    }

}
