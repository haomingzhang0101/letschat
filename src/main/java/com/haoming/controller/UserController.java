package com.haoming.controller;

import com.haoming.bo.UsersBO;
import com.haoming.enums.SearchFriendsStatusEnum;
import com.haoming.pojo.Users;
import com.haoming.service.UserService;
import com.haoming.utils.FastDFSClient;
import com.haoming.utils.FileUtils;
import com.haoming.utils.IMoocJSONResult;
import com.haoming.utils.MD5Utils;
import com.haoming.vo.FriendRequestVO;
import com.haoming.vo.UsersVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("u")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/registerOrLogin")
    public IMoocJSONResult registerOrLogin(@RequestBody Users user) throws Exception {

        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return IMoocJSONResult.errorMsg("Empty username or password.");
        }

        // 1. Identify whether given username exists or not.
        boolean isExist = userService.queryUsernameIsExist(user.getUsername());
        Users userResult = null;
        if (isExist) {
            // 1.1 Login
            userResult = userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if (userResult == null) {
                return IMoocJSONResult.errorMsg("Incorrect username or password.");
            }
        } else {
            // 1.2 Register
            user.setNickname(user.getUsername());
            user.setFaceImage("");
            user.setFaceImageBig("");
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            userResult = userService.saveUser(user);
        }

        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userResult, userVO);

        return IMoocJSONResult.ok(userVO);
    }

    @PostMapping("/uploadFaceBase64")
    public IMoocJSONResult uploadFaceBase64(@RequestBody UsersBO usersBO) throws Exception {
        String base64Data = usersBO.getFaceData();
        String userFacePath = "/Users/Haoming/Documents/developer/imgs/" + usersBO.getUserId() + "userface64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);

        // Upload the image file to fastDFS.
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);

        // Acquire the thumb image's url.
        String thumb = "_80x80.";
        String arr[] = url.split("\\.");
        String thumbImgUrl = arr[0] + thumb + arr[1];

        // Update user's profile image.
        Users update = new Users();
        update.setId(usersBO.getUserId());
        update.setFaceImage(thumbImgUrl);
        update.setFaceImageBig(url);
        Users result = userService.updateUserInfo(update);

        return IMoocJSONResult.ok(result);
    }

    @PostMapping("/setnickname")
    public IMoocJSONResult setnickname(@RequestBody UsersBO usersBO) throws Exception {
        Users update = new Users();
        update.setId(usersBO.getUserId());
        update.setNickname(usersBO.getNickname());
        Users result = userService.updateUserInfo(update);

        return IMoocJSONResult.ok(result);
    }

    /**
     * Find a user that matches the input username.
     * @author zhanghm
     * @date 2018-09-09 01:10
     */
    @PostMapping("/search")
    public IMoocJSONResult searchUser(String myUserId, String friendUserName) throws Exception {

        // Make sure that either myUserId or friendUserName can't be null.
        if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUserName)) {
            return IMoocJSONResult.errorMsg("");
        }

        // 1. Return "no such user" if the given friendUserName doesn't exist.
        // 2. Return "Can not add yourself" if the given friendUserName coincides with current user's username.
        // 3. Return "You guys are already friends, start chatting now" if the current user is trying to re-add a friend.
        Integer status = userService.preconditionSearchFriends(myUserId, friendUserName);
        if (status != SearchFriendsStatusEnum.SUCCESS.getStatus()) {
            return IMoocJSONResult.errorMsg(SearchFriendsStatusEnum.getMsgByKey(status));
        } else {
            Users user = userService.queryUserInfoByUsername(friendUserName);
            UsersVO userVO = new UsersVO();
            BeanUtils.copyProperties(user, userVO);
            return IMoocJSONResult.ok(userVO);
        }
    }

    /**
     * Send a friend request.
     * @author zhanghm
     * @date 2018-09-09 01:10
     */
    @PostMapping("/addFriendRequest")
    public IMoocJSONResult addFriendRequest(String myUserId, String friendUserName) throws Exception {

        // Make sure that either myUserId or friendUserName can't be null.
        if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUserName)) {
            return IMoocJSONResult.errorMsg("");
        }

        // 1. Return "no such user" if the given friendUserName doesn't exist.
        // 2. Return "Can not add yourself" if the given friendUserName coincides with current user's username.
        // 3. Return "You guys are already friends, start chatting now" if the current user is trying to re-add a friend.
        Integer status = userService.preconditionSearchFriends(myUserId, friendUserName);
        if (status != SearchFriendsStatusEnum.SUCCESS.getStatus()) {
            return IMoocJSONResult.errorMsg(SearchFriendsStatusEnum.getMsgByKey(status));
        } else {
            userService.sendFriendRequest(myUserId, friendUserName);
        }

        return IMoocJSONResult.ok();
    }

    /**
     * Query the request list for an user.
     * @author zhanghm
     * @date 2018-09-15 21:38
     */
    @RequestMapping("/queryFriendRequests")
    public IMoocJSONResult queryFriendRequests(String acceptUserId) {
        if (StringUtils.isBlank(acceptUserId)) {
            return IMoocJSONResult.errorMsg("Empty user id");
        }
        List<FriendRequestVO> list = userService.queryFriendRequestList(acceptUserId);
        return IMoocJSONResult.ok(list);
    }


}
