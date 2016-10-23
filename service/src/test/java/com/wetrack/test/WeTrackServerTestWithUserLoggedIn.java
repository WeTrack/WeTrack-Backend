package com.wetrack.test;

import org.junit.Before;

public abstract class WeTrackServerTestWithUserLoggedIn extends WeTrackServerTest {

    protected String token;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createUserWithAssertion();
        token = loginUserWithAssertion();
    }

}
