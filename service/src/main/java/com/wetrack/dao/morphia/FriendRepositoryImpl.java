package com.wetrack.dao.morphia;

import com.wetrack.dao.FriendRepository;
import com.wetrack.model.Friend;

public class FriendRepositoryImpl extends MorphiaRepository<String, Friend> implements FriendRepository {
    @Override
    protected Class<Friend> getEntityClass() {
        return Friend.class;
    }

    @Override
    public boolean isFriend(String usernameA, String usernameB) {
        return createQuery().field("_id").equal(usernameA).field("friendNames").hasThisOne(usernameB).countAll() > 0;
    }
}
