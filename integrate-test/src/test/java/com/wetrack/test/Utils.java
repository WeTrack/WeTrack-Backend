package com.wetrack.test;

import com.google.gson.Gson;
import com.wetrack.client.model.User;

import static com.wetrack.util.ResourceUtils.readResource;

public class Utils {

    public static User loadExampleUser(Gson gson) {
        return loadExampleUser(gson, "user.json");
    }

    public static User loadExampleUser(Gson gson, String fileName) {
        return gson.fromJson(readResource(fileName), User.class);
    }

}
