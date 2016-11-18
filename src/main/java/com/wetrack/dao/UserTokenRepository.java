package com.wetrack.dao;

import com.wetrack.model.UserToken;

public interface UserTokenRepository extends Repository<String, UserToken> {

    UserToken findByTokenStr(String tokenStr);

    UserToken findByUsername(String username);

    void deleteByUsername(String username);

    void deleteByTokenStr(String tokenStr);

}
