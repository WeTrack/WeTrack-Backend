package com.wetrack.client.test;

import com.wetrack.client.ResultCallback;

public class ResultResponseTest extends WeTrackClientTest {

    protected boolean successful;
    protected Throwable receivedException;

    protected ResultCallback callback(final int successStatusCode) {
        return new ResultCallback(successStatusCode) {
            @Override
            protected void onSuccess() {
                successful = true;
                receivedException = null;
            }

            @Override
            protected void onFail() {
                successful = false;
                receivedException = null;
            }

            @Override
            protected void onError(Throwable ex) {
                successful = false;
                receivedException = ex;
            }
        };
    }

}
