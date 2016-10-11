package com.wetrack.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wetrack.client.json.LocalDateTimeTypeAdapter;
import com.wetrack.client.json.LocalDateTypeAdapter;
import okhttp3.OkHttpClient;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class Config {

    public static Gson gson() {
        return new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    public static Retrofit retrofit(String baseUrl, int timeout) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(timeout, TimeUnit.SECONDS);

        return new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

}
