package com.wetrack.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wetrack.client.json.LocalDateTimeTypeAdapter;
import com.wetrack.client.json.LocalDateTypeAdapter;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class Config {

    public static Gson gson() {
        return new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

}
