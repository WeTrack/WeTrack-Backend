package com.wetrack.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class ResourceUtils {

    public static String readResource(String fileName) {
        URL resourceUrl = ResourceUtils.class.getClassLoader().getResource(fileName);
        if (resourceUrl == null)
            throw new AssertionError("Failed to find `" + fileName + "` in resources folder. Check if it is deleted.");
        try {
            return new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));
        } catch (URISyntaxException ex) {
            throw new AssertionError("Failed to convert URL `" + resourceUrl.toString() + "` to URI.", ex);
        } catch (IOException ex) {
            throw new AssertionError("Failed to read from `" + resourceUrl.toString() + "`.", ex);
        }
    }

}
