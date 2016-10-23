package com.wetrack.test;

import com.google.gson.Gson;
import com.wetrack.client.model.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.fail;

public class Utils {

    public static User loadExampleUser(Gson gson) {
        return loadExampleUser(gson, "user.json");
    }

    public static User loadExampleUser(Gson gson, String fileName) {
        return gson.fromJson(readResource(fileName), User.class);
    }

    public static String readResource(String fileName) {
        URL resourceUrl = Utils.class.getClassLoader().getResource(fileName);
        if (resourceUrl == null)
            fail("Failed to find `" + fileName + "` in resources folder. Check if it is deleted.");
        try {
            return new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));
        } catch (URISyntaxException ex) {
            throw new AssertionError("Failed to convert URL `" + resourceUrl.toString() + "` to URI.", ex);
        } catch (IOException ex) {
            throw new AssertionError("Failed to read from `" + resourceUrl.toString() + "`.", ex);
        }
    }

}
