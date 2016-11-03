package com.wetrack.client;

import com.wetrack.client.model.Chat;
import com.wetrack.client.model.CreatedMessage;
import com.wetrack.client.model.Message;
import com.wetrack.client.model.User;
import retrofit2.Response;
import retrofit2.http.*;
import rx.Observable;

import java.util.List;

interface ChatService {

    @POST("/chats")
    Observable<Response<CreatedMessage>> createChat(@Query("token") String token, @Body Chat chatEntity);

    @POST("/chats/{chatId}/members")
    Observable<Response<Message>> addChatMembers(@Path("chatId") String chatId,
                                                 @Query("token") String token,
                                                 @Body List<String> newMemberNames);

    @GET("/chats/{chatId}/members")
    Observable<Response<List<User>>> getChatMembers(@Path("chatId") String chatId,
                                                    @Query("token") String token);

    @DELETE("/chats/{chatId}/members/{memberName}")
    Observable<Response<Message>> removeChatMember(@Path("chatId") String chatId,
                                                   @Path("memberName") String memberName,
                                                   @Query("token") String token);

    @GET("/users/{username}/chats")
    Observable<Response<List<Chat>>> getUserChatList(@Path("username") String username,
                                                     @Query("token") String token);

    @DELETE("/users/{username}/chats/{chatId}")
    Observable<Response<Message>> exitChat(@Path("username") String username,
                                           @Path("chatId") String chatId,
                                           @Query("token") String token);

}
