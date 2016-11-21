package com.wetrack.dao.morphia;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.wetrack.config.SpringConfig;
import com.wetrack.config.SpringTestConfig;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.UserToken;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.temporal.ChronoUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class UserTokenRepositoryTest {

    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private MongoClient client;

    private String username = "robert-peng";
    private String anotherUsername = username + "Oi!";

    private MongoCollection<Document> tokens;

    @Before
    public void setUp() {
        tokens = client.getDatabase(SpringConfig.DEFAULT_DATABASE).getCollection("tokens");
    }

    @After
    public void cleanUp() {
        // Delete the inserted user
        tokens.deleteOne(new Document("username", username));
        tokens.deleteOne(new Document("username", anotherUsername));
    }

    @Test
    public void testUserTokenDeleteByTokenStr() {
        UserToken token = new UserToken(username, 1, ChronoUnit.DAYS);
        userTokenRepository.insert(token);

        UserToken anotherToken = new UserToken(anotherUsername, 3, ChronoUnit.DAYS);
        userTokenRepository.insert(anotherToken);

        userTokenRepository.deleteByTokenStr(token.getToken());

        Document tokenInDB = tokens.find(new Document("_id", token.getId())).first();
        assertNull(tokenInDB);
        tokenInDB = tokens.find(new Document("_id", anotherToken.getId())).first();
        assertNotNull(tokenInDB);
    }

    @Test
    public void testUserTokenDeleteByUsername() {
        UserToken token = new UserToken(username, 1, ChronoUnit.DAYS);
        userTokenRepository.insert(token);

        UserToken anotherToken = new UserToken(anotherUsername, 3, ChronoUnit.DAYS);
        userTokenRepository.insert(anotherToken);

        userTokenRepository.deleteByUsername(username);

        Document tokenInDB = tokens.find(new Document("_id", token.getId())).first();
        assertNull(tokenInDB);
        tokenInDB = tokens.find(new Document("_id", anotherToken.getId())).first();
        assertNotNull(tokenInDB);
    }

    @Test
    public void testUserTokenInsert() {
        UserToken token = new UserToken(username, 1, ChronoUnit.DAYS);
        userTokenRepository.insert(token);

        Document tokenInDB = tokens.find(new Document("_id", token.getId())).first();
        assertNotNull(tokenInDB);
        assertThat(tokenInDB.getString("username"), is(username));
    }

    @Test(expected = DuplicateKeyException.class)
    public void testUserTokenDuplicateInsert() {
        UserToken token = new UserToken(username, 1, ChronoUnit.DAYS);
        userTokenRepository.insert(token);

        token = new UserToken(username, 3, ChronoUnit.MINUTES);
        userTokenRepository.insert(token); // Should trigger the expected exception
    }
}
