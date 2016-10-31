package com.wetrack.test;

import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import org.junit.Before;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WeTrackIntegrateTestWithFriendAdded extends WeTrackIntegrateTestWithUserLoggedIn {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        addFriendWithAssertion(robertPeng, windyChan);
        addFriendWithAssertion(robertPeng, mrDai);
        addFriendWithAssertion(windyChan, littleHearth);
        addFriendWithAssertion(mrDai, littleHearth);
    }

    protected void addFriendWithAssertion(User user, User friend) {
        MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(200);
        client.addFriend(user.getUsername(), tokenOf(user), friend.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
    }

    protected void assertFriendNum(User user, int expectedFriendNum) {
        EntityResponseTestHelper<List<User>> entityHelper = new EntityResponseTestHelper<>(gson);
        client.getUserFriendList(user.getUsername(), tokenOf(user), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        assertThat(entityHelper.getReceivedEntity().size(), is(expectedFriendNum));
    }

}
