package com.wetrack.service.user;

import com.wetrack.test.WeTrackServerTestWithUserLoggedIn;
import com.wetrack.util.CryptoUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UserPasswordUpdateTest extends WeTrackServerTestWithUserLoggedIn {

    @Test
    public void testInvalidPasswordUpdate() {
        UserPasswordUpdateService.PasswordUpdateRequest updateRequestEntity =
                new UserPasswordUpdateService.PasswordUpdateRequest(null, null);
        Response response = put("/users/" + username + "/password", updateRequestEntity);
        logResponse(response, "password update with empty old password and new password");
        assertReceivedNonemptyMessage(response, 400);

        updateRequestEntity.setOldPassword(password);
        updateRequestEntity.setNewPassword(anotherPassword);
        response = put("/users/" + username + "/password", updateRequestEntity);
        logResponse(response, "password update with incorrect old password");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testValidPasswordUpdate() {
        UserPasswordUpdateService.PasswordUpdateRequest updateRequestEntity =
                new UserPasswordUpdateService.PasswordUpdateRequest(CryptoUtils.md5Digest(password), anotherPassword);
        Response response = put("/users/" + username + "/password", updateRequestEntity);
        logResponse(response, "password update");
        assertReceivedNonemptyMessage(response, 200);

        // Assert the token has been invalidated
        response = post("/users/" + username + "/tokenVerify", token, MediaType.TEXT_PLAIN_TYPE);
        logResponse(response, "invalidated token verification");
        assertReceivedNonemptyMessage(response, 401);
    }

}
