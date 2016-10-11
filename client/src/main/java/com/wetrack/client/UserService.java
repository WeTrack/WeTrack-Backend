package com.wetrack.client;

import com.wetrack.client.model.User;
import retrofit2.http.*;
import rx.Observable;

public interface UserService {

    @HEAD("/users/{username}")
    Observable<Boolean> userExists(@Path("username") String username);

    @POST("/users/{username}/tokenValidate")
    Observable<Boolean> tokenValidate(@Path("username") String username,
                                      @Body String token);

    @GET("/users/{username}")
    Observable<User> getUserInfo(@Path("username") String username);

    @POST("/users/{username}")
    Observable<Boolean> updateUser(@Path("username") String username,
                                   @Body TokenUserRequest request);

    @POST("/users")
    Observable<Boolean> createUser(@Body User newUser);

    @POST("/users/{username}/password")
    Observable<Boolean> updateUserPassword(@Path("username") String username,
                                           @Body PasswordUpdateRequest request);

    class TokenUserRequest {
        private String token;
        private User user;

        public TokenUserRequest() {}

        public TokenUserRequest(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public static TokenUserRequest of(String token, User user) {
            return new TokenUserRequest(token, user);
        }

        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }
    }

    class PasswordUpdateRequest {
        private String token;
        private String oldPassword;
        private String newPassword;

        public PasswordUpdateRequest() {}

        public PasswordUpdateRequest(String token, String oldPassword, String newPassword) {
            this.token = token;
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        public static PasswordUpdateRequest of(String token, String oldPassword, String newPassword) {
            return new PasswordUpdateRequest(token, oldPassword, newPassword);
        }

        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
        public String getOldPassword() {
            return oldPassword;
        }
        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }
        public String getNewPassword() {
            return newPassword;
        }
        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

}
