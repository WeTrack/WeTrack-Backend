package com.wetrack.service.user;

import com.google.gson.JsonObject;
import com.wetrack.test.WeTrackServerTestWithUserLoggedIn;
import com.wetrack.util.CryptoUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UserPasswordUpdateTest extends WeTrackServerTestWithUserLoggedIn {
    private String newPassword = "I\'m the new password";

    @Test
    public void testInvalidPasswordUpdate() {
        Response response = passwordUpdate(robertPeng.getUsername(), "", newPassword);
        logResponse(response, "password update with empty old password and new password");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized for empty fields

        response = passwordUpdate(robertPeng.getUsername(), windyChan.getPassword(), newPassword);
        logResponse(response, "password update with incorrect old password");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized for incorrect old password
    }

    @Test
    public void testValidPasswordUpdate() {
        Response response = passwordUpdate(robertPeng.getUsername(), robertPeng.getPassword(), newPassword);
        logResponse(response, "password update");
        assertReceivedNonemptyMessage(response, 200);

        // Assert the token has been invalidated
        response = post("/users/" + robertPeng.getUsername() + "/tokenVerify",
                tokenOf(robertPeng), MediaType.TEXT_PLAIN_TYPE);
        logResponse(response, "invalidated token verification");
        assertReceivedNonemptyMessage(response, 401);
    }

    private Response passwordUpdate(String username, String oldPassword, String newPassword) {
        JsonObject requestEntity = new JsonObject();
        requestEntity.addProperty("old_password", CryptoUtils.md5Digest(oldPassword));
        requestEntity.addProperty("new_password", newPassword);
        return put("/users/" + username + "/password", requestEntity);
    }

}
