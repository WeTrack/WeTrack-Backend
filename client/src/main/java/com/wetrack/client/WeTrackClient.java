package com.wetrack.client;

import com.google.gson.Gson;
import com.wetrack.client.model.Message;
import com.wetrack.client.model.User;
import com.wetrack.client.model.UserToken;
import com.wetrack.util.CryptoUtils;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Scheduler;

import java.util.concurrent.TimeUnit;

import static com.wetrack.client.config.Config.gson;

public class WeTrackClient {

    private Gson gson;
    private Retrofit retrofit;
    private UserService userService;

    public WeTrackClient(String baseUrl, int timeoutSeconds) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(timeoutSeconds, TimeUnit.SECONDS);

        this.gson = gson();
        Retrofit.Builder builder = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl);

        this.retrofit = builder.build();
        setUpServices();
    }

    public WeTrackClient(Retrofit retrofit) {
        this.retrofit = retrofit;
        setUpServices();
    }

    public void tokenValidate(String username, String token, final Callback<UserToken> callback) {
        userService.tokenValidate(username, token).subscribe(observer(callback));
    }

    public void creatUser(User newUser, final ResultMessageCallback callback) {
        userService.createUser(newUser).subscribe(observer(callback));
    }

    public void updateUser(String username, String token, User updatedUser, final ResultMessageCallback callback) {
        updatedUser.setPassword(null);
        userService.updateUser(username, new UserService.TokenUserRequest(token, updatedUser))
                .subscribe(observer(callback));
    }

    public void updateUserPassword(String username, String token, String oldPassword, String newPassword,
                                   final ResultMessageCallback callback) {
        oldPassword = CryptoUtils.md5Digest(oldPassword);
        userService.updateUserPassword(username, new UserService.PasswordUpdateRequest(token, oldPassword, newPassword))
                .subscribe(observer(callback));
    }

    public void creatUser(User newUser, final Callback<Message> callback) {
        userService.createUser(newUser).subscribe(observer(callback));
    }

    public void updateUser(String username, String token, User updatedUser, final Callback<Message> callback) {
        updatedUser.setPassword(null);
        userService.updateUser(username, new UserService.TokenUserRequest(token, updatedUser))
                .subscribe(observer(callback));
    }

    public void updateUserPassword(String username, String token, String oldPassword, String newPassword,
                                   final Callback<Message> callback) {
        oldPassword = CryptoUtils.md5Digest(oldPassword);
        userService.updateUserPassword(username, new UserService.PasswordUpdateRequest(token, oldPassword, newPassword))
                .subscribe(observer(callback));
    }

    public void tokenValidate(String username, String token,
                              final Callback<UserToken> callback, Scheduler scheduler) {
        userService.tokenValidate(username, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void creatUser(User newUser, final ResultMessageCallback callback, Scheduler scheduler) {
        userService.createUser(newUser).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void updateUser(String username, String token, User updatedUser,
                           final ResultMessageCallback callback, Scheduler scheduler) {
        updatedUser.setPassword(null);
        userService.updateUser(username, new UserService.TokenUserRequest(token, updatedUser))
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void updateUserPassword(String username, String token, String oldPassword, String newPassword,
                                   final ResultMessageCallback callback, Scheduler scheduler) {
        oldPassword = CryptoUtils.md5Digest(oldPassword);
        userService.updateUserPassword(username, new UserService.PasswordUpdateRequest(token, oldPassword, newPassword))
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void creatUser(User newUser, final Callback<Message> callback, Scheduler scheduler) {
        userService.createUser(newUser).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void updateUser(String username, String token, User updatedUser,
                           final Callback<Message> callback, Scheduler scheduler) {
        updatedUser.setPassword(null);
        userService.updateUser(username, new UserService.TokenUserRequest(token, updatedUser))
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void updateUserPassword(String username, String token, String oldPassword, String newPassword,
                                   final Callback<Message> callback, Scheduler scheduler) {
        oldPassword = CryptoUtils.md5Digest(oldPassword);
        userService.updateUserPassword(username, new UserService.PasswordUpdateRequest(token, oldPassword, newPassword))
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Checks if a user with the given username exists synchronously.
     * The {@link ResultCallback#onSuccess()} method will be invoked if there is such user,
     * otherwise the {@link ResultCallback#onFail()} will be invoked.
     *
     * @param username the given username
     * @param callback callback object which defines how to handle different result
     */
    public void userExists(String username, final ResultCallback callback) {
        userService.userExists(username).subscribe(observer(callback));
    }

    /**
     * Gets the information of the user with the given username synchronously.
     * The {@link Callback#onReceive(Object)} method will be invoked when successfully
     * received the response entity as a {@link User}.
     *
     * @param username the given username of the designated user.
     * @param callback callback object which defines how to handle different response.
     */
    public void getUserInfo(String username, final Callback<User> callback) {
        userService.getUserInfo(username).subscribe(observer(callback));
    }

    /**
     * Logs in with the given username and password synchronously.
     * The {@link Callback#onReceive(Object)} method will be invoked with the received {@link UserToken}
     * if the log in is successful.
     *
     * @param username the given username to be used for logging in.
     * @param password the given password to be used for logging in.
     * @param callback callback object which defines how to handle different response.
     */
    public void userLogin(String username, String password, final Callback<UserToken> callback) {
        password = CryptoUtils.md5Digest(password);
        userService.userLogin(new UserService.UserLoginRequest(username, password))
                .subscribe(observer(callback));
    }

    /**
     * Checks if a user with the given username exists asynchronously.
     * The method will use the given {@link Scheduler} to execute the network request.
     * The {@link ResultCallback#onSuccess()} method will be invoked on the current thread
     * if there is such user, otherwise the {@link ResultCallback#onFail()} will be invoked.
     *
     * @param username the given username
     * @param callback callback object which defines how to handle different result
     * @param scheduler the given scheduler on which to execute the network request.
     */
    public void userExists(String username, final ResultCallback callback, Scheduler scheduler) {
        userService.userExists(username).subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Gets the information of the user with the given username asynchronously.
     * The method will use the given {@link Scheduler} to execute the network request.
     * The {@link Callback#onReceive(Object)} method will be invoked on the current thread
     * when successfully received the response entity as a {@link User}.
     *
     * @param username the given username of the designated user.
     * @param callback callback object which defines how to handle different response.
     * @param scheduler the given scheduler on which to execute the network request.
     */
    public void getUserInfo(String username, final Callback<User> callback, Scheduler scheduler) {
        userService.getUserInfo(username).subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Logs in with the given username and password asynchronously.
     * The method will use the given {@link Scheduler} to execute the network request.
     * The {@link Callback#onReceive(Object)} method will be invoked on the current thread
     * with the received {@link UserToken} if the log in is successful.
     *
     * @param username the given username to be used for logging in.
     * @param password the given password to be used for logging in.
     * @param callback callback object which defines how to handle different response.
     * @param scheduler the given scheduler on which to execute the network request.
     */
    public void userLogin(String username, String password, final Callback<UserToken> callback, Scheduler scheduler) {
        password = CryptoUtils.md5Digest(password);
        userService.userLogin(new UserService.UserLoginRequest(username, password))
                .subscribeOn(scheduler)
                .subscribe(observer(callback));
    }

    private void setUpServices() {
        userService = retrofit.create(UserService.class);
    }

    private Observer<Response<Message>> observer(final ResultMessageCallback callback) {
        return new Observer<Response<Message>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }

            @Override
            public void onNext(Response<Message> messageResponse) {
                if (messageResponse.code() == callback.getSuccessStatusCode())
                    callback.onSuccess(messageResponse.body().getMessage());
                else
                    callback.onFail(messageResponse.body().getMessage());
            }
        };
    }

    private Observer<Response> observer(final ResultCallback callback) {
        return new Observer<Response>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }

            @Override
            public void onNext(Response response) {
                if (response.code() == callback.getSuccessStatusCode())
                    callback.onSuccess();
                else
                    callback.onFail();
            }
        };
    }

    private <T> Observer<Response<T>> observer(final Callback<T> callback) {
        return new Observer<Response<T>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onException(e);
            }

            @Override
            public void onNext(Response<T> response) {
                callback.onResponse(response);
                if (response.code() == 200)
                    callback.onReceive(response.body());
                else
                    callback.onErrorResponse(response);
            }
        };
    }

}
