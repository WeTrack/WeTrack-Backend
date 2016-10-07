package com.wetrack.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.wetrack.config.SpringConfig;
import com.wetrack.config.SpringTestConfig;
import com.wetrack.model.DbEntity;
import com.wetrack.model.User;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoClient client;

    private String username = "robert-peng";
    private String nickname = "Robert Peng";
    private String password = "I\'m a password";

    private MongoCollection<Document> users;

    @Before
    public void setUp() {
        users = client.getDatabase(SpringConfig.DATABASE_NAME).getCollection("users");
    }

    @After
    public void cleanUp() {
        // Delete the inserted user
        users.deleteOne(new Document("_id", username));
    }

    /**
     * Unit test for {@link Repository#insert(DbEntity)} on {@link User} type
     */
    @Test
    public void testUserDaoInsert() {
        User user = new User(username, password, nickname);

        // Insert the user via the testing method
        userRepository.insert(user);

        // Verify the result
        Document userInDB = users.find(new Document("_id", username)).first();
        assertNotNull(userInDB);
        assertThat(userInDB.getString("nickname"), is(nickname));
        assertThat(userInDB.getString("password"), is(password));
    }

    /**
     * Unit test for {@link Repository#delete(DbEntity)} on {@link User} type
     * and {@link UserRepository#findByUsername(String)}
     */
    @Test
    public void testUserDelete() {
        Document userDoc = new Document("_id", username).append("nickname", nickname).append("password", password);
        users.insertOne(userDoc);

        // Delete the user via the testing method
        User user = new User(username, "anything", "anything");
        userRepository.delete(user);

        assertThat(users.count(new Document("_id", username)), is(0L));
    }

    /**
     * Unit test for {@link Repository#findById(Object)} on {@link User} type
     */
    @Test
    public void testUserFindById() {

        Document userDoc = new Document("_id", username).append("nickname", nickname).append("password", password);
        users.insertOne(userDoc);

        // Query via the testing method
        User user = userRepository.findById(username);
        assertThat(user.getId(), is(username));
        assertThat(user.getUsername(), is(username));
        assertThat(user.getNickname(), is(nickname));
        assertThat(user.getPassword(), is(password));

        user = userRepository.findByUsername(username);
        assertThat(user.getId(), is(username));
        assertThat(user.getUsername(), is(username));
        assertThat(user.getNickname(), is(nickname));
        assertThat(user.getPassword(), is(password));
    }

    /**
     * Unit test for {@link Repository#update(DbEntity)} on {@link User} type
     */
    @Test
    public void testUserUpdate() {
        Document userDoc = new Document("_id", username).append("nickname", nickname).append("password", password);
        users.insertOne(userDoc);

        // Update via the testing method
        String newNickname = nickname.concat("Robert Peng");
        String newPassword = password.concat("This is a password");
        User user = new User(username, newPassword, newNickname);
        userRepository.update(user);

        // Verify the result
        Document userInDB = users.find(new Document("_id", username)).first();
        assertThat(userInDB, notNullValue());
        assertThat(userInDB.getString("_id"), is(username));
        assertThat(userInDB.getString("nickname"), is(newNickname));
        assertThat(userInDB.getString("password"), is(newPassword));
    }

}
