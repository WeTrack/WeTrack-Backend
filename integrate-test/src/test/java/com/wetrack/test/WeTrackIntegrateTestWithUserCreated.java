package com.wetrack.test;

import com.wetrack.client.model.User;
import com.wetrack.client.test.MessageResponseTestHelper;
import org.joda.time.LocalDate;
import org.junit.Before;

public class WeTrackIntegrateTestWithUserCreated extends WeTrackIntegrateTest {

    protected User user;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(password);
        user.setEmail(email);
        user.setBirthDate(LocalDate.parse(birthDate.toString()));
        MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(201);
        client.createUser(user, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();

        username = user.getUsername();
    }

}
