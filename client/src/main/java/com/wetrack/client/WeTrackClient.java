package com.wetrack.client;

import com.wetrack.client.model.User;
import com.wetrack.client.model.UserToken;
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

    private Retrofit retrofit;
    private UserService userService;

    public WeTrackClient(String baseUrl, int timeoutSeconds) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(timeoutSeconds, TimeUnit.SECONDS);

        Retrofit.Builder builder = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl);

        this.retrofit = builder.build();
        setUpServices();
    }

    public WeTrackClient(Retrofit retrofit) {
        this.retrofit = retrofit;
        setUpServices();
    }

    /**
     * Gets the information of the user with the given username synchronously.
     * The {@link AsyncCallback#onReceive(Object)} method will be invoked when successfully
     * received the response entity as a {@link User}.
     *
     * @param username the given username of the designated user.
     * @param callback callback object which defines how to handle different response.
     */
    public void getUserInfo(String username, final AsyncCallback<User> callback) {
        userService.getUserInfo(username).subscribe(userObserver(callback));
    }

    /**
     * Logs in with the given username and password synchronously.
     * The {@link AsyncCallback#onReceive(Object)} method will be invoked with the received {@link UserToken}
     * if the log in is successful.
     *
     * @param username the given username to be used for logging in.
     * @param password the given password to be used for logging in.
     * @param callback callback object which defines how to handle different response.
     */
    public void userLogin(String username, String password, final AsyncCallback<UserToken> callback) {
        userService.userLogin(new UserService.UserLoginRequest(username, password))
                .subscribe(userTokenObserver(callback));
    }

    /**
     * Gets the information of the user with the given username asynchronously.
     * The method will use the given {@link Scheduler} to execute the network request.
     * The {@link AsyncCallback#onReceive(Object)} method will be invoked on the current thread
     * when successfully received the response entity as a {@link User}.
     *
     * @param username the given username of the designated user.
     * @param callback callback object which defines how to handle different response.
     * @param scheduler the given scheduler on which to execute the network request.
     */
    public void getUserInfo(String username, final AsyncCallback<User> callback, Scheduler scheduler) {
        userService.getUserInfo(username).subscribeOn(scheduler).subscribe(userObserver(callback));
    }

    /**
     * Logs in with the given username and password asynchronously.
     * The method will use the given {@link Scheduler} to execute the network request.
     * The {@link AsyncCallback#onReceive(Object)} method will be invoked on the current thread
     * with the received {@link UserToken} if the log in is successful.
     *
     * @param username the given username to be used for logging in.
     * @param password the given password to be used for logging in.
     * @param callback callback object which defines how to handle different response.
     * @param scheduler the given scheduler on which to execute the network request.
     */
    public void userLogin(String username, String password, final AsyncCallback<UserToken> callback, Scheduler scheduler) {
        userService.userLogin(new UserService.UserLoginRequest(username, password))
                .subscribeOn(scheduler)
                .subscribe(userTokenObserver(callback));
    }

    private void setUpServices() {
        userService = retrofit.create(UserService.class);
    }

    private Observer<Response<UserToken>> userTokenObserver(final AsyncCallback<UserToken> callback) {
        return new Observer<Response<UserToken>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onException(e);
            }

            @Override
            public void onNext(Response<UserToken> response) {
                callback.onResponse(response);
                if (response.code() == 200)
                    callback.onReceive(response.body());
                else
                    callback.onErrorResponse(response);
            }
        };
    }

    private Observer<Response<User>> userObserver(final AsyncCallback<User> callback) {
        return new Observer<Response<User>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onException(e);
            }

            @Override
            public void onNext(Response<User> response) {
                callback.onResponse(response);
                if (response.code() == 200)
                    callback.onReceive(response.body());
                else
                    callback.onErrorResponse(response);
            }
        };
    }

}
