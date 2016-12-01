package com.wetrack.dao.morphia;

import com.wetrack.dao.UserPortraitRepository;
import com.wetrack.model.UserPortrait;

public class UserPortraitRepositoryImpl extends MorphiaRepository<String, UserPortrait> implements UserPortraitRepository {
    @Override
    protected Class<UserPortrait> getEntityClass() {
        return UserPortrait.class;
    }
}
