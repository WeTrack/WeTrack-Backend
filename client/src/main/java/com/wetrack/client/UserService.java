package com.wetrack.client;

import com.wetrack.client.model.Message;
import com.wetrack.client.model.User;
import com.wetrack.client.model.UserToken;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.*;
import rx.Observable;

interface UserService {

    @POST("/login")
    Observable<Response<UserToken>> userLogin(@Body UserLoginRequest request);

    @HEAD("/users/{username}")
    Observable<Response<Void>> userExists(@Path("username") String username);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/users/{username}/tokenValidate")
    Observable<Response<UserToken>> tokenValidate(@Path("username") String username,
                                                  @Body RequestBody token);

    @GET("/users/{username}")
    Observable<Response<User>> getUserInfo(@Path("username") String username);

    @PUT("/users/{username}")
    Observable<Response<Message>> updateUser(@Path("username") String username,
                                             @Body TokenUserRequest request);

    @POST("/users")
    Observable<Response<Message>> createUser(@Body User newUser);

    @PUT("/users/{username}/password")
    Observable<Response<Message>> updateUserPassword(@Path("username") String username,
                                                     @Body PasswordUpdateRequest request);

    class UserLoginRequest {
        private String username;
        private String password;

        UserLoginRequest() {}

        UserLoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        String getUsername() {
            return username;
        }
        void setUsername(String username) {
            this.username = username;
        }
        String getPassword() {
            return password;
        }
        void setPassword(String password) {
            this.password = password;
        }
    }

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
        private String oldPassword;
        private String newPassword;

        public PasswordUpdateRequest() {}

        public PasswordUpdateRequest(String oldPassword, String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        public static PasswordUpdateRequest of(String oldPassword, String newPassword) {
            return new PasswordUpdateRequest(oldPassword, newPassword);
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
