package com.wetrack.dao.morphia;

import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.UserToken;

public class UserTokenRepositoryImpl extends MorphiaRepository<UserToken> implements UserTokenRepository {

    @Override
    protected Class<UserToken> getEntityClass() {
        return UserToken.class;
    }

    @Override
    public UserToken findByTokenStr(String tokenStr) {
        return findById(tokenStr);
    }

    @Override
    public UserToken findByUsername(String username) {
        return createQuery().field("username").equal(username).get();
    }

    @Override
    public void deleteByUsername(String username) {
        getDatastore().delete(createQuery().field("username").equal(username));
    }

}
