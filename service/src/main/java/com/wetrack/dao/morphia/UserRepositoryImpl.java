package com.wetrack.dao.morphia;

import com.wetrack.dao.UserRepository;
import com.wetrack.model.User;

import java.util.Iterator;

public class UserRepositoryImpl extends MorphiaRepository<String, User> implements UserRepository {
    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public User findByUsername(String username) {
        return findById(username);
    }

    @Override
    public long countByUsername(String username) {
        return createQuery().field("_id").equal(username).countAll();
    }

    @Override
    public Iterator<User> findAllByNickname(String nickname) {
        return createQuery().search(nickname).iterator();
    }
}
