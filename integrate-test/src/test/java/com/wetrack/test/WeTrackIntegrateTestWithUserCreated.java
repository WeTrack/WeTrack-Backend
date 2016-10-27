package com.wetrack.test;

import com.wetrack.client.model.User;
import com.wetrack.client.test.MessageResponseTestHelper;
import org.joda.time.LocalDate;
import org.junit.Before;

public class WeTrackIntegrateTestWithUserCreated extends WeTrackIntegrateTest {

    protected User robertPeng = new User("robert-peng", "robert-peng", "Robert Peng");
    protected User windyChan = new User("windy-chan", "windy-chan", "Windy Chan");
    protected User mrDai = new User("mr-dai", "mr-dai", "Mr. Dai");
    protected User littleHearth = new User("little-hearth", "little-hearth", "Little Hearth");

    protected String username = robertPeng.getUsername();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        robertPeng.setEmail("robert.peng@example.com");
        robertPeng.setBirthDate(LocalDate.parse("1993-09-08"));
        robertPeng.setGender(User.Gender.Male);

        windyChan.setEmail("windy-chan@example.com");
        windyChan.setBirthDate(LocalDate.parse("1994-12-09"));
        windyChan.setGender(User.Gender.Female);

        mrDai.setEmail("mr-dai@example.com");
        mrDai.setBirthDate(LocalDate.parse("1993-08-09"));
        mrDai.setGender(User.Gender.Male);

        littleHearth.setEmail("little-hearth@example.com");
        littleHearth.setBirthDate(LocalDate.parse("1994-10-02"));
        littleHearth.setGender(User.Gender.Female);

        MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(201);
        client.createUser(robertPeng, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
        client.createUser(windyChan, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
        client.createUser(mrDai, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
        client.createUser(littleHearth, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
    }

}
