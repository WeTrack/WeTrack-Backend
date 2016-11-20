package com.wetrack.dao.morphia;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.wetrack.config.SpringConfig;
import com.wetrack.config.SpringTestConfig;
import com.wetrack.dao.FriendRepository;
import com.wetrack.dao.UserRepository;
import com.wetrack.model.Friend;
import com.wetrack.model.User;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class FriendRepositoryTest {

    @Autowired private MongoClient client;
    @Autowired private UserRepository userRepository;
    @Autowired private FriendRepository friendRepository;

    private MongoCollection<Document> friends;

    private User userA = new User("robert-peng", null, null);
    private User userB = new User("windy-chan", null, null);
    private User userC = new User("mr-dai", null, null);
    private User userD = new User("little-hearth", null, null);


    @Before
    public void setUp() {
        friends = client.getDatabase(SpringConfig.DEFAULT_DATABASE).getCollection("friends");
        userRepository.insert(userA);
        userRepository.insert(userB);
        userRepository.insert(userC);
        userRepository.insert(userD);
    }

    @After
    public void tearDown() {
        userRepository.delete(userA);
        userRepository.delete(userB);
        userRepository.delete(userC);
        userRepository.delete(userD);

        friends.deleteMany(new Document("_id", userA.getUsername()));
        friends.deleteMany(new Document("_id", userB.getUsername()));
        friends.deleteMany(new Document("_id", userC.getUsername()));
        friends.deleteMany(new Document("_id", userD.getUsername()));
    }

    @Test
    public void testIsFriend() {
        Friend friend = new Friend(userA.getUsername());
        friend.addFriend(userB);
        friend.addFriend(userC);
        friendRepository.insert(friend);

        assertThat(friendRepository.isFriend(userA.getUsername(), userB.getUsername()), is(true));
        assertThat(friendRepository.isFriend(userA.getUsername(), userC.getUsername()), is(true));
        assertThat(friendRepository.isFriend(userA.getUsername(), userD.getUsername()), is(false));
        assertThat(friendRepository.isFriend(userB.getUsername(), userC.getUsername()), is(false));
    }

    @Test
    public void testFriendInsert() {
        Friend friend = new Friend(userA.getUsername());
        friend.addFriend(userB);
        friendRepository.insert(friend);

        Document friendInDB = friends.find(new Document("_id", userA.getUsername())).first();
        assertThat(friendInDB.containsKey("friendNames"), is(true));
        ArrayList<String> friendsInDoc = friendInDB.get("friendNames", ArrayList.class);
        assertThat(friendsInDoc.size(), is(1));
        assertThat(friendsInDoc.get(0), is(userB.getUsername()));
    }

    @Test
    public void testFriendDuplicateInsert() {
        Friend friend = new Friend(userA.getUsername());
        friend.addFriend(userB);
        friend.addFriend(userC);
        friend.addFriend(userD);

        friend.addFriend(userB);

        friendRepository.insert(friend);
        Document friendInDB = friends.find(new Document("_id", userA.getUsername())).first();
        assertThat(friendInDB.containsKey("friendNames"), is(true));
        ArrayList<String> friendsInDoc = friendInDB.get("friendNames", ArrayList.class);
        assertThat(friendsInDoc.size(), is(3));
    }

    @Test
    public void testFriendUpdate() {
        Friend friend = new Friend(userA.getUsername());
        friend.addFriend(userB);
        friendRepository.insert(friend);

        // Incremental update
        friend.addFriend(userC);
        friendRepository.update(friend);
        Document friendInDB = friends.find(new Document("_id", userA.getUsername())).first();
        assertThat(friendInDB.containsKey("friendNames"), is(true));
        ArrayList<String> friendsInDoc = friendInDB.get("friendNames", ArrayList.class);
        assertThat(friendsInDoc.size(), is(2));
        assertThat(friendsInDoc.get(0), is(userB.getUsername()));
        assertThat(friendsInDoc.get(1), is(userC.getUsername()));

        // Decremental update
        friend.removeFriend(userB);
        friendRepository.update(friend);
        friendInDB = friends.find(new Document("_id", userA.getUsername())).first();
        assertThat(friendInDB.containsKey("friendNames"), is(true));
        friendsInDoc = friendInDB.get("friendNames", ArrayList.class);
        assertThat(friendsInDoc.size(), is(1));
        assertThat(friendsInDoc.get(0), is(userC.getUsername()));
    }


}
