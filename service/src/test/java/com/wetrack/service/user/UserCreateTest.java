package com.wetrack.service.user;

import com.wetrack.model.User;
import com.wetrack.test.WeTrackServerTest;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class UserCreateTest extends WeTrackServerTest {
    private String username = "robert-peng";
    private String password = "I\'m a password";
    private String nickname = "Robert Peng";
    private String email = "robert.peng@example.com";

    @Test
    public void testCreatingUser() {
        User newUser = new User(username, password, nickname);
        Response response = post("/users", newUser);

        logResponse(response, "user create");
        assertReceivedCreatedMessage(response, "/users/" + username);
    }

    @Test
    public void testCreatingDuplicateUser() {
        // Create a user
        User newUser = new User(username, password, nickname);
        Response response = post("/users", newUser);

        // Create it again with email field updated
        newUser.setEmail(email);
        response = post("/users", newUser);

        logResponse(response, "duplicate user create");
        assertReceivedNonemptyMessage(response, 403); // Forbidden for duplicate username
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
