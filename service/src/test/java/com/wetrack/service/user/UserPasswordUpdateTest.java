package com.wetrack.service.user;

import com.wetrack.test.WeTrackServerTestWithUserLoggedIn;
import com.wetrack.util.CryptoUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UserPasswordUpdateTest extends WeTrackServerTestWithUserLoggedIn {
    private String newPassword = "I\'m the new password";

    @Test
    public void testInvalidPasswordUpdate() {
        UserPasswordUpdateService.PasswordUpdateRequest updateRequestEntity =
                new UserPasswordUpdateService.PasswordUpdateRequest(null, null);
        Response response = put("/users/" + robertPeng.getUsername() + "/password", updateRequestEntity);
        logResponse(response, "password update with empty old password and new password");
        assertReceivedNonemptyMessage(response, 400); // Bad Request for empty fields

        updateRequestEntity.setOldPassword(robertPeng.getPassword()); // Not md5ed
        updateRequestEntity.setNewPassword(newPassword);
        response = put("/users/" + robertPeng.getUsername() + "/password", updateRequestEntity);
        logResponse(response, "password update with incorrect old password");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized for incorrect old password
    }

    @Test
    public void testValidPasswordUpdate() {
        UserPasswordUpdateService.PasswordUpdateRequest updateRequestEntity =
                new UserPasswordUpdateService.PasswordUpdateRequest(
                        CryptoUtils.md5Digest(robertPeng.getPassword()),
                        newPassword
                );
        Response response = put("/users/" + robertPeng.getUsername() + "/password", updateRequestEntity);
        logResponse(response, "password update");
        assertReceivedNonemptyMessage(response, 200);

        // Assert the token has been invalidated
        response = post("/users/" + robertPeng.getUsername() + "/tokenVerify",
                tokens.get(robertPeng), MediaType.TEXT_PLAIN_TYPE);
        logResponse(response, "invalidated token verification");
        assertReceivedNonemptyMessage(response, 401);
    }

}
