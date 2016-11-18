package com.wetrack.json;

import com.google.gson.reflect.TypeToken;
import com.wetrack.model.Chat;
import com.wetrack.model.User;

import java.lang.reflect.Type;
import java.util.List;

public abstract class GsonTypes {

    public static final Type userListType = new TypeToken<List<User>>(){}.getType();
    public static final Type stringListType = new TypeToken<List<String>>(){}.getType();
    public static final Type chatListType = new TypeToken<List<Chat>>(){}.getType();

}
