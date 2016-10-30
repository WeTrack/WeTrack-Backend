package com.wetrack.client;

import com.wetrack.client.model.Message;
import com.wetrack.client.model.User;
import retrofit2.Response;
import retrofit2.http.*;
import rx.Observable;

import java.util.List;

interface FriendService {

    @GET("/users/{username}/friends")
    Observable<Response<List<User>>> getUserFriendList(@Path("username") String username,
                                                       @Query("token") String token);

    @POST("/users/{username}/friends/{friendName}")
    Observable<Response<Message>> addFriend(@Path("username") String username,
                                            @Path("friendName") String friendName,
                                            @Query("token") String token);

    @DELETE("/users/{username}/friends/{friendName}")
    Observable<Response<Message>> deleteFriend(@Path("username") String username,
                                               @Path("friendName") String friendName,
                                               @Query("token") String token);

    @HEAD("/users/{username}/friends/{friendName}")
    Observable<Response<Void>> isFriend(@Path("username") String username,
                                        @Path("friendName") String friendName,
                                        @Query("token") String token);

}
