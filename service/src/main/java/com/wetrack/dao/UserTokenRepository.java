package com.wetrack.dao;

import com.wetrack.model.UserToken;

public interface UserTokenRepository extends Repository<UserToken> {

    UserToken findByTokenStr(String tokenStr);

    UserToken findByUsername(String username);

}
