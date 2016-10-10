package com.wetrack;

import com.wetrack.config.WeTrackApplication;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

public abstract class JerseyTest extends org.glassfish.jersey.test.JerseyTest {

    @Override
    protected Application configure() {
        return new WeTrackApplication();
    }

    protected Invocation.Builder request(String path) {
        return target(path).request();
    }

    protected Invocation.Builder request(String path, MediaType... mediaTypes) {
        return target(path).request(mediaTypes);
    }

}
