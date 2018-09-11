package com.haoming.service;

import com.haoming.pojo.Users;

public interface UserService {

    /**
     *  Identify whether given username exists or not.
     * @author zhanghm
     * @date 2018-09-02 12:48
     */
    boolean queryUsernameIsExist(String username);

    /**
     * Identify whether given user exists or not.
     * @author zhanghm
     * @date 2018-09-02 13:10
     */
    Users queryUserForLogin(String username, String password);

    /**
     *  New user register.
     * @author zhanghm
     * @date 2018-09-02 14:18
     */
    Users saveUser(Users user);

    /**
     * Update user's profile.
     * @author zhanghm
     * @date 2018-09-05 19:33
     */
    Users updateUserInfo(Users user);

    /**
     * Query the pre condition for adding friends.
     * @author zhanghm
     * @date 2018-09-09 01:24
     */
    Integer preconditionSearchFriends(String myUserId, String friendUserName);

    /**
     * Query an user by a given username.
     * @author zhanghm
     * @date 2018-09-09 02:01
     */
    Users queryUserInfoByUsername(String username);

    /**
     * Send a friend request.
     * @author zhanghm
     * @date 2018-09-10 23:37
     */
    void sendFriendRequest(String myUserId, String friendUserName);
}
