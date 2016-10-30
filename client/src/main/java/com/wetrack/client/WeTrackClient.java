package com.wetrack.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wetrack.client.model.*;
import com.wetrack.util.CryptoUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.joda.time.LocalDateTime;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Scheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.wetrack.client.config.Config.gson;

public class WeTrackClient {

    private Gson gson;
    private Retrofit retrofit;
    private UserService userService;
    private ChatService chatService;
    private FriendService friendService;
    private LocationService locationService;

    /**
     * Creates a {@code WeTrackClient} connected to the given base URL with given timeout in seconds.
     *
     * @param baseUrl the given base URL.
     * @param timeoutSeconds the given timeout in seconds.
     */
    public WeTrackClient(String baseUrl, int timeoutSeconds) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(timeoutSeconds, TimeUnit.SECONDS);

        this.gson = gson();
        Retrofit.Builder builder = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl);
        this.retrofit = builder.build();
        setUpServices();
    }

    /**
     * Creates a {@code WeTrackClient} with the given {@link Retrofit} instance.
     *
     * @param retrofit the given {@code Retrofit} instance.
     */
    public WeTrackClient(Retrofit retrofit) {
        this.gson = gson();
        this.retrofit = retrofit;
        setUpServices();
    }

    /**
     * Checks if the given username and token is still valid synchronously.
     * The {@link Callback#onReceive(Object)} method will be invoked if the provided token is still valid,
     * provided with the token value and expired time sent from the server;
     * otherwise the {@link Callback#onErrorResponse(Response)} will be invoked with status code {@code 401}
     * and empty response body.
     *
     * @param username the given username.
     * @param token the given token to be verified.
     * @param callback callback object which defines how to handle different result.
     */
    public void tokenVerify(String username, String token, final Callback<UserToken> callback) {
        userService.tokenValidate(username, RequestBody.create(MediaType.parse("text/plain"), token))
                .subscribe(observer(callback));
    }

    /**
     * Checks if the given username and token is still valid asynchronously.
     * The method will use the given {@link Scheduler} to execute the network request.
     * The {@link Callback#onReceive(Object)} method will be invoked if the provided token is still valid,
     * provided with the token value and expired time sent from the server;
     * otherwise the {@link Callback#onErrorResponse(Response)} will be invoked with status code {@code 401}
     * and empty response body.
     *
     * @param username the given username.
     * @param token the given token to be verified.
     * @param callback callback object which defines how to handle different result.
     * @param scheduler the given scheduler on which to execute the network request.
     */
    public void tokenVerify(String username, String token,
                            final Callback<UserToken> callback, Scheduler scheduler) {
        userService.tokenValidate(username, RequestBody.create(MediaType.parse("text/plain"), token))
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Creates a user with the fields provided in the given {@link User} instance synchronously.
     * The {@link ResultMessageCallback#onSuccess(String)} method will be invoked if the creation is
     * successful; otherwise, the {@link ResultMessageCallback#onFail(String, int)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Fields in the provided {@code User} instance are invalid.</td></tr>
     *     <tr><td>{@code 403}</td><td>User with same username already exist.</td></tr>
     * </table>
     *
     * @param newUser the given {@code User} instance.
     * @param callback callback object which defines how to handle different result.
     */
    public void createUser(User newUser, final ResultMessageCallback callback) {
        userService.createUser(newUser).subscribe(observer(callback));
    }

    /**
     * Creates a user with the fields provided in the given {@link User} instance asynchronously.
     * The method will use the provided {@link Scheduler} to execute the network request.
     * The {@link ResultMessageCallback#onSuccess(String)} method will be invoked if the creation is
     * successful; otherwise, the {@link ResultMessageCallback#onFail(String, int)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Fields in the provided {@code User} instance are invalid.</td></tr>
     *     <tr><td>{@code 403}</td><td>User with same username already exist.</td></tr>
     * </table>
     *
     * @param newUser the given {@code User} instance.
     * @param callback callback object which defines how to handle different result.
     * @param scheduler the provided {@code Scheduler} on which the method will execute network request.
     */
    public void createUser(User newUser, final ResultMessageCallback callback, Scheduler scheduler) {
        userService.createUser(newUser).subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Creates a user with the fields provided in the given {@link User} instance synchronously.
     * The {@link Callback#onReceive(Object)} method will be invoked if the creation is successful;
     * otherwise, the {@link Callback#onErrorResponse(Response)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Fields in the provided {@code User} instance are invalid.</td></tr>
     *     <tr><td>{@code 403}</td><td>User with same username already exist.</td></tr>
     * </table>
     *
     * @param newUser the given {@code User} instance.
     * @param callback callback object which defines how to handle different result.
     */
    public void createUser(User newUser, final Callback<Message> callback) {
        userService.createUser(newUser).subscribe(observer(callback));
    }

    /**
     * Creates a user with the fields provided in the given {@link User} instance asynchronously.
     * The method will use the given {@link Scheduler} to execute network request.
     * The {@link Callback#onReceive(Object)} method will be invoked if the creation is successful;
     * otherwise, the {@link Callback#onErrorResponse(Response)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Fields in the provided {@code User} instance are invalid.</td></tr>
     *     <tr><td>{@code 403}</td><td>User with same username already exist.</td></tr>
     * </table>
     *
     * @param newUser the given {@code User} instance.
     * @param callback callback object which defines how to handle different result.
     * @param scheduler the given {@code Scheduler} on which the method should execute network request.
     */
    public void createUser(User newUser, final Callback<Message> callback, Scheduler scheduler) {
        userService.createUser(newUser).subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Updates the user with the given username with the fields provided in the given {@link User} instance
     * synchronously. The {@link ResultMessageCallback#onSuccess(String)} method will be invoked if the update
     * is successful; otherwise, the {@link ResultMessageCallback#onFail(String, int)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Fields in the provided {@code User} instance are invalid.</td></tr>
     *     <tr>
     *         <td>{@code 401}</td>
     *         <td>
     *             The provided token is invalid or has expired; or the logged-in user has no permission for
     *             updating this user.
     *         </td>
     *     </tr>
     *     <tr><td>{@code 404}</td><td>User with the given username does not exist.</td></tr>
     * </table>
     *
     * @param username the given username.
     * @param token given token for authentication and permission authorization.
     * @param updatedUser the given {@code User} instance.
     * @param callback callback object which defines how to handle different result.
     */
    public void updateUser(String username, String token, User updatedUser, final ResultMessageCallback callback) {
        updatedUser.setPassword(null);
        userService.updateUser(username, token, updatedUser).subscribe(observer(callback));
    }

    /**
     * Updates the user with the given username with the fields provided in the given {@link User} instance
     * asynchronously. The method will use the given {@link Scheduler} to execute network request.
     * The {@link ResultMessageCallback#onSuccess(String)} method will be invoked if the update
     * is successful; otherwise, the {@link ResultMessageCallback#onFail(String, int)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Fields in the provided {@code User} instance are invalid.</td></tr>
     *     <tr>
     *         <td>{@code 401}</td>
     *         <td>
     *             The provided token is invalid or has expired; or the logged-in user has no permission for
     *             updating this user.
     *         </td>
     *     </tr>
     *     <tr><td>{@code 404}</td><td>User with the given username does not exist.</td></tr>
     * </table>
     *
     * @param username the given username.
     * @param token given token for authentication and permission authorization.
     * @param updatedUser the given {@code User} instance.
     * @param callback callback object which defines how to handle different result.
     * @param scheduler the given {@code Scheduler} on which the method should execute network request.
     */
    public void updateUser(String username, String token, User updatedUser,
                           final ResultMessageCallback callback, Scheduler scheduler) {
        updatedUser.setPassword(null);
        userService.updateUser(username, token, updatedUser).subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Updates the user with the given username with the fields provided in the given {@link User} instance
     * synchronously. The {@link Callback#onReceive(Object)} method will be invoked if the update is successful;
     * otherwise, the {@link Callback#onErrorResponse(Response)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Fields in the provided {@code User} instance are invalid.</td></tr>
     *     <tr>
     *         <td>{@code 401}</td>
     *         <td>
     *             The provided token is invalid or has expired; or the logged-in user has no permission for
     *             updating this user.
     *         </td>
     *     </tr>
     *     <tr><td>{@code 404}</td><td>User with the given username does not exist.</td></tr>
     * </table>
     *
     * @param username the given username.
     * @param token given token for authentication and permission authorization.
     * @param updatedUser the given {@code User} instance.
     * @param callback callback object which defines how to handle different result.
     */
    public void updateUser(String username, String token, User updatedUser, final Callback<Message> callback) {
        updatedUser.setPassword(null);
        userService.updateUser(username, token, updatedUser).subscribe(observer(callback));
    }

    /**
     * Updates the user with the given username with the fields provided in the given {@link User} instance
     * asynchronously. The method will use the given {@link Scheduler} to execute network request.
     * The {@link Callback#onReceive(Object)} method will be invoked if the update is successful;
     * otherwise, the {@link Callback#onErrorResponse(Response)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Fields in the provided {@code User} instance are invalid.</td></tr>
     *     <tr>
     *         <td>{@code 401}</td>
     *         <td>
     *             The provided token is invalid or has expired; or the logged-in user has no permission for
     *             updating this user.
     *         </td>
     *     </tr>
     *     <tr><td>{@code 404}</td><td>User with the given username does not exist.</td></tr>
     * </table>
     *
     * @param username the given username.
     * @param token given token for authentication and permission authorization.
     * @param updatedUser the given {@code User} instance.
     * @param callback callback object which defines how to handle different result.
     * @param scheduler the given {@code Scheduler} on which the method should execute network request.
     */
    public void updateUser(String username, String token, User updatedUser,
                           final Callback<Message> callback, Scheduler scheduler) {
        updatedUser.setPassword(null);
        userService.updateUser(username, token, updatedUser).subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Updates the password of the user with the given username synchronously. The user's original password and
     * new password must be provided in plain text. The {@link ResultMessageCallback#onSuccess(String)} method
     * will be invoked if the update is successful, and any formerly logged-in token of this user will be
     * invalidated. Otherwise, the {@link ResultMessageCallback#onFail(String, int)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Provided old or/and new password is/are empty or invalid.</td></tr>
     *     <tr><td>{@code 401}</td><td>Provided old password is incorrect.</td></tr>
     *     <tr><td>{@code 404}</td><td>User with provided username does not exist.</td></tr>
     * </table>
     *
     * @param username the given username.
     * @param oldPassword the provided original password of the user in plain text.
     * @param newPassword the new password of the user in plain text.
     * @param callback callback object which defines how to handle different result.
     */
    public void updateUserPassword(String username, String oldPassword, String newPassword,
                                   final ResultMessageCallback callback) {
        oldPassword = CryptoUtils.md5Digest(oldPassword);
        userService.updateUserPassword(username, new UserService.PasswordUpdateRequest(oldPassword, newPassword))
                .subscribe(observer(callback));
    }

    /**
     * Updates the password of the user with the given username asynchronously. The method will use the given
     * {@link Scheduler} to execute network request. The user's original password and new password must be
     * provided in plain text. The {@link ResultMessageCallback#onSuccess(String)} method will be invoked if
     * the update is successful, and any formerly logged-in token of this user will be invalidated. Otherwise,
     * the {@link ResultMessageCallback#onFail(String, int)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Provided old or/and new password is/are empty or invalid.</td></tr>
     *     <tr><td>{@code 401}</td><td>Provided old password is incorrect.</td></tr>
     *     <tr><td>{@code 404}</td><td>User with provided username does not exist.</td></tr>
     * </table>
     *
     * @param username the given username.
     * @param oldPassword the provided original password of the user in plain text.
     * @param newPassword the new password of the user in plain text.
     * @param callback callback object which defines how to handle different result.
     */
    public void updateUserPassword(String username, String oldPassword, String newPassword,
                                   final ResultMessageCallback callback, Scheduler scheduler) {
        oldPassword = CryptoUtils.md5Digest(oldPassword);
        userService.updateUserPassword(username, new UserService.PasswordUpdateRequest(oldPassword, newPassword))
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Updates the password of the user with the given username synchronously. The user's original password and
     * new password must be provided in plain text. The {@link Callback#onReceive(Object)} method will be invoked
     * if the update is successful, and any formerly logged-in token of this user will be invalidated. Otherwise,
     * the {@link Callback#onErrorResponse(Response)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Provided old or/and new password is/are empty or invalid.</td></tr>
     *     <tr><td>{@code 401}</td><td>Provided old password is incorrect.</td></tr>
     *     <tr><td>{@code 404}</td><td>User with provided username does not exist.</td></tr>
     * </table>
     *
     * @param username the given username.
     * @param oldPassword the provided original password of the user in plain text.
     * @param newPassword the new password of the user in plain text.
     * @param callback callback object which defines how to handle different result.
     */
    public void updateUserPassword(String username, String oldPassword, String newPassword,
                                   final Callback<Message> callback) {
        oldPassword = CryptoUtils.md5Digest(oldPassword);
        userService.updateUserPassword(username, new UserService.PasswordUpdateRequest(oldPassword, newPassword))
                .subscribe(observer(callback));
    }

    /**
     * Updates the password of the user with the given username asynchronously. The method will use the given
     * {@link Scheduler} to execute network request. The user's original password and new password must be
     * provided in plain text. The {@link Callback#onReceive(Object)} method will be invoked if the update is
     * successful, and any formerly logged-in token of this user will be invalidated. Otherwise,
     * the {@link Callback#onErrorResponse(Response)} method will be invoked.
     * <br><br>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Provided old or/and new password is/are empty or invalid.</td></tr>
     *     <tr><td>{@code 401}</td><td>Provided old password is incorrect.</td></tr>
     *     <tr><td>{@code 404}</td><td>User with provided username does not exist.</td></tr>
     * </table>
     *
     * @param username the given username.
     * @param oldPassword the provided original password of the user in plain text.
     * @param newPassword the new password of the user in plain text.
     * @param callback callback object which defines how to handle different result.
     * @param scheduler the given {@code Scheduler} on which the method should execute network request.
     */
    public void updateUserPassword(String username, String oldPassword, String newPassword,
                                   final Callback<Message> callback, Scheduler scheduler) {
        oldPassword = CryptoUtils.md5Digest(oldPassword);
        userService.updateUserPassword(username, new UserService.PasswordUpdateRequest(oldPassword, newPassword))
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void getUserLocationsSince(String username, LocalDateTime sinceTime, final Callback<List<Location>> callback) {
        locationService.getLocationSince(username, sinceTime.toString())
                .subscribe(observer(callback));
    }

    public void getUserLocationsSince(String username, LocalDateTime sinceTime,
                                      final Callback<List<Location>> callback, Scheduler scheduler) {
        locationService.getLocationSince(username, sinceTime.toString())
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void uploadLocations(String username, String token, List<Location> locations, final ResultMessageCallback callback) {
        locationService.uploadLocations(username, new LocationService.LocationsUploadRequest(token, locations))
                .subscribe(observer(callback));
    }

    public void uploadLocations(String username, String token, List<Location> locations,
                                final ResultMessageCallback callback, Scheduler scheduler) {
        locationService.uploadLocations(username, new LocationService.LocationsUploadRequest(token, locations))
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void getUserLatestLocation(String username, final Callback<Location> callback) {
        locationService.getLatestLocation(username).subscribe(observer(callback));
    }

    public void getUserLatestLocation(String username, final Callback<Location> callback, Scheduler scheduler) {
        locationService.getLatestLocation(username).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void createChat(String token, Chat chat, final ResultMessageCallback callback) {
        chatService.createChat(token, serializeChat(chat)).subscribe(observer(callback));
    }

    public void createChat(String token, Chat chat, final Callback<Message> callback) {
        chatService.createChat(token, serializeChat(chat)).subscribe(observer(callback));
    }

    public void createChat(String token, Chat chat, final ResultMessageCallback callback, Scheduler scheduler) {
        chatService.createChat(token, serializeChat(chat)).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void createChat(String token, Chat chat, final Callback<Message> callback, Scheduler scheduler) {
        chatService.createChat(token, serializeChat(chat)).subscribeOn(scheduler).subscribe(observer(callback));
    }

    private JsonObject serializeChat(Chat chat) {
        JsonObject requestEntity = new JsonObject();
        requestEntity.addProperty("name", chat.getName());
        List<String> memberNames = new ArrayList<>(chat.getMembers().size());
        for (User member : chat.getMembers())
            memberNames.add(member.getUsername());
        requestEntity.add("members", gson.toJsonTree(memberNames));
        return requestEntity;
    }

    public void addChatMembers(String chatId, String token, List<User> newMembers,
                               final ResultMessageCallback callback) {
        chatService.addChatMembers(chatId, token, usersToUsernames(newMembers)).subscribe(observer(callback));
    }

    public void addChatMembers(String chatId, String token, List<User> newMembers,
                               final Callback<Message> callback) {
        chatService.addChatMembers(chatId, token, usersToUsernames(newMembers)).subscribe(observer(callback));
    }

    public void addChatMembers(String chatId, String token, List<User> newMembers,
                               final ResultMessageCallback callback, Scheduler scheduler) {
        chatService.addChatMembers(chatId, token, usersToUsernames(newMembers))
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void addChatMembers(String chatId, String token, List<User> newMembers,
                               final Callback<Message> callback, Scheduler scheduler) {
        chatService.addChatMembers(chatId, token, usersToUsernames(newMembers))
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    private List<String> usersToUsernames(List<User> users) {
        List<String> usernames = new ArrayList<>(users.size());
        for (User user : users)
            usernames.add(user.getUsername());
        return usernames;
    }

    public void getChatMembers(String chatId, String token, final Callback<List<User>> callback) {
        chatService.getChatMembers(chatId, token).subscribe(observer(callback));
    }

    public void getChatMembers(String chatId, String token, final Callback<List<User>> callback, Scheduler scheduler) {
        chatService.getChatMembers(chatId, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void removeChatMember(String chatId, String token, User member, final ResultMessageCallback callback) {
        chatService.removeChatMember(chatId, member.getUsername(), token).subscribe(observer(callback));
    }

    public void removeChatMember(String chatId, String token, User member, final Callback<Message> callback) {
        chatService.removeChatMember(chatId, member.getUsername(), token).subscribe(observer(callback));
    }

    public void removeChatMember(String chatId, String token, User member,
                                 final ResultMessageCallback callback,
                                 Scheduler scheduler) {
        chatService.removeChatMember(chatId, member.getUsername(), token)
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void removeChatMember(String chatId, String token, User member,
                                 final Callback<Message> callback,
                                 Scheduler scheduler) {
        chatService.removeChatMember(chatId, member.getUsername(), token)
                .subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void getUserChatList(String username, String token, final Callback<List<Chat>> callback) {
        chatService.getUserChatList(username, token).subscribe(observer(callback));
    }

    public void getUserChatList(String username, String token,
                                final Callback<List<Chat>> callback,
                                Scheduler scheduler) {
        chatService.getUserChatList(username, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void exitChat(String username, String token, String chatId, final ResultMessageCallback callback) {
        chatService.exitChat(username, chatId, token).subscribe(observer(callback));
    }

    public void exitChat(String username, String token, String chatId, final Callback<Message> callback) {
        chatService.exitChat(username, chatId, token).subscribe(observer(callback));
    }

    public void exitChat(String username, String token, String chatId,
                         final ResultMessageCallback callback,
                         Scheduler scheduler) {
        chatService.exitChat(username, chatId, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void exitChat(String username, String token, String chatId,
                         final Callback<Message> callback,
                         Scheduler scheduler) {
        chatService.exitChat(username, chatId, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void getUserFriendList(String username, String token, final Callback<List<User>> callback) {
        friendService.getUserFriendList(username, token).subscribe(observer(callback));
    }

    public void getUserFriendList(String username, String token,
                                  final Callback<List<User>> callback,
                                  Scheduler scheduler) {
        friendService.getUserFriendList(username, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void addFriend(String username, String token, String friendName,
                          final ResultMessageCallback callback) {
        friendService.addFriend(username, friendName, token).subscribe(observer(callback));
    }

    public void addFriend(String username, String token, String friendName,
                          final Callback<Message> callback) {
        friendService.addFriend(username, friendName, token).subscribe(observer(callback));
    }

    public void addFriend(String username, String token, String friendName,
                          final ResultMessageCallback callback,
                          Scheduler scheduler) {
        friendService.addFriend(username, friendName, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void addFriend(String username, String token, String friendName,
                          final Callback<Message> callback,
                          Scheduler scheduler) {
        friendService.addFriend(username, friendName, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void deleteFriend(String username, String token, String friendName,
                             final ResultMessageCallback callback) {
        friendService.deleteFriend(username, friendName, token).subscribe(observer(callback));
    }

    public void deleteFriend(String username, String token, String friendName,
                             final Callback<Message> callback) {
        friendService.deleteFriend(username, friendName, token).subscribe(observer(callback));
    }

    public void deleteFriend(String username, String token, String friendName,
                             final ResultMessageCallback callback,
                             Scheduler scheduler) {
        friendService.deleteFriend(username, friendName, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void deleteFriend(String username, String token, String friendName,
                             final Callback<Message> callback,
                             Scheduler scheduler) {
        friendService.deleteFriend(username, friendName, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    public void isFriend(String username, String token, String friendName,
                         final ResultCallback callback) {
        friendService.isFriend(username, friendName, token).subscribe(observer(callback));
    }

    public void isFriend(String username, String token, String friendName,
                         final ResultCallback callback,
                         Scheduler scheduler) {
        friendService.isFriend(username, friendName, token).subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Checks if a user with the given username exists synchronously.
     * The {@link ResultCallback#onSuccess()} method will be invoked if there is such user,
     * otherwise the {@link ResultCallback#onFail(int)} will be invoked.
     *
     * @param username the given username
     * @param callback callback object which defines how to handle different result
     */
    public void userExists(String username, final ResultCallback callback) {
        userService.userExists(username).subscribe(observer(callback));
    }

    /**
     * Checks if a user with the given username exists asynchronously.
     * The method will use the given {@link Scheduler} to execute the network request.
     * The {@link ResultCallback#onSuccess()} method will be invoked on the current thread
     * if there is such user, otherwise the {@link ResultCallback#onFail(int)} will be invoked.
     *
     * @param username the given username.
     * @param callback callback object which defines how to handle different result.
     * @param scheduler the given scheduler on which to execute the network request.
     */
    public void userExists(String username, final ResultCallback callback, Scheduler scheduler) {
        userService.userExists(username).subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Gets the information of the user with the given username synchronously.
     * The {@link Callback#onReceive(Object)} method will be invoked when successfully
     * received the response entity as a {@link User}.
     *
     * @param username the given username of the designated user.
     * @param callback callback object which defines how to handle different response.
     */
    public void getUserInfo(String username, final Callback<User> callback) {
        userService.getUserInfo(username).subscribe(observer(callback));
    }

    /**
     * Gets the information of the user with the given username asynchronously.
     * The method will use the given {@link Scheduler} to execute the network request.
     * The {@link Callback#onReceive(Object)} method will be invoked on the current thread
     * when successfully received the response entity as a {@link User}.
     *
     * @param username the given username of the designated user.
     * @param callback callback object which defines how to handle different response.
     * @param scheduler the given scheduler on which to execute the network request.
     */
    public void getUserInfo(String username, final Callback<User> callback, Scheduler scheduler) {
        userService.getUserInfo(username).subscribeOn(scheduler).subscribe(observer(callback));
    }

    /**
     * Logs in with the given username and password synchronously.
     * The {@link Callback#onReceive(Object)} method will be invoked with the received {@link UserToken}
     * if the log in is successful.
     *
     * @param username the given username to be used for logging in.
     * @param password the given password to be used for logging in.
     * @param callback callback object which defines how to handle different response.
     */
    public void userLogin(String username, String password, final Callback<UserToken> callback) {
        password = CryptoUtils.md5Digest(password);
        userService.userLogin(new UserService.UserLoginRequest(username, password))
                .subscribe(observer(callback));
    }

    /**
     * Logs in with the given username and password asynchronously.
     * The method will use the given {@link Scheduler} to execute the network request.
     * The {@link Callback#onReceive(Object)} method will be invoked on the current thread
     * with the received {@link UserToken} if the log in is successful.
     *
     * @param username the given username to be used for logging in.
     * @param password the given password to be used for logging in.
     * @param callback callback object which defines how to handle different response.
     * @param scheduler the given scheduler on which to execute the network request.
     */
    public void userLogin(String username, String password, final Callback<UserToken> callback, Scheduler scheduler) {
        password = CryptoUtils.md5Digest(password);
        userService.userLogin(new UserService.UserLoginRequest(username, password))
                .subscribeOn(scheduler)
                .subscribe(observer(callback));
    }

    private void setUpServices() {
        userService = retrofit.create(UserService.class);
        chatService = retrofit.create(ChatService.class);
        friendService = retrofit.create(FriendService.class);
        locationService = retrofit.create(LocationService.class);
    }

    private Observer<Response<Message>> observer(final ResultMessageCallback callback) {
        return new Observer<Response<Message>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }

            @Override
            public void onNext(Response<Message> messageResponse) {
                if (messageResponse.code() == callback.getSuccessStatusCode()) {
                    callback.onSuccess(messageResponse.body().getMessage());
                } else {
                    if (messageResponse.body() != null) {
                        callback.onFail(messageResponse.body().getMessage(), messageResponse.code());
                    } else {
                        try (ResponseBody errorBody = messageResponse.errorBody()) {
                            String errorResponse = errorBody.string();
                            Message message = gson.fromJson(errorResponse, Message.class);
                            callback.onFail(message.getMessage(), messageResponse.code());
                        } catch (IOException e) {
                            callback.onFail(messageResponse.message(), messageResponse.code());
                        }
                    }
                }
            }
        };
    }

    private Observer<Response> observer(final ResultCallback callback) {
        return new Observer<Response>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }

            @Override
            public void onNext(Response response) {
                if (response.code() == callback.getSuccessStatusCode())
                    callback.onSuccess();
                else
                    callback.onFail(response.code());
            }
        };
    }

    private <T> Observer<Response<T>> observer(final Callback<T> callback) {
        return new Observer<Response<T>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onException(e);
            }

            @Override
            public void onNext(Response<T> response) {
                callback.onResponse(response);
                if (response.code() >= 200 && response.code() < 300)
                    callback.onReceive(response.body());
                else
                    callback.onErrorResponse(response);
            }
        };
    }

}
