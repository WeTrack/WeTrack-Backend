package com.wetrack.client;

import com.wetrack.client.model.User;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
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

    public void getUserInfo(String username, final AsyncCallback<User> callback) {
        userService.getUserInfo(username).subscribe(userObserver(callback));
    }

    public void getUserInfo(String username, final AsyncCallback<User> callback, Scheduler scheduler) {
        userService.getUserInfo(username).subscribeOn(scheduler).subscribe(userObserver(callback));
    }

    private void setUpServices() {
        userService = retrofit.create(UserService.class);
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
