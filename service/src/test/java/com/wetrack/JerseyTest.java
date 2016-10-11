package com.wetrack;

import com.wetrack.config.WeTrackApplication;
import com.wetrack.util.GsonJerseyProvider;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

public abstract class JerseyTest extends org.glassfish.jersey.test.JerseyTest {

    @Override
    protected Application configure() {
        return new WeTrackApplication();
    }

    @Override
    protected void configureClient(final ClientConfig config) {
        config.register(GsonJerseyProvider.class);
    }

    protected Invocation.Builder request(String path) {
        return target(path).request();
    }

    protected Invocation.Builder request(String path, MediaType... mediaTypes) {
        return target(path).request(mediaTypes);
    }

}
