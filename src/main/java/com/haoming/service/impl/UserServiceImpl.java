package com.haoming.service.impl;

import com.haoming.enums.SearchFriendsStatusEnum;
import com.haoming.mapper.FriendsRequestMapper;
import com.haoming.mapper.MyFriendsMapper;
import com.haoming.mapper.UsersMapper;
import com.haoming.pojo.FriendsRequest;
import com.haoming.pojo.MyFriends;
import com.haoming.pojo.Users;
import com.haoming.service.UserService;
import com.haoming.utils.FastDFSClient;
import com.haoming.utils.FileUtils;
import com.haoming.utils.QRCodeUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.io.IOException;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper userMapper;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Users query = new Users();
        query.setUsername(username);

        Users result = userMapper.selectOne(query);

        return result != null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {

        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);

        return userMapper.selectOneByExample(userExample);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users saveUser(Users user) {

        String userId = sid.nextShort();

        // Set qrCode for each user.
        String qrCodePath = "/Users/Haoming/Documents/developer/imgs/" + userId + "qrcode.png";
        // letschat_qrcode:[username]
        qrCodeUtils.createQRCode(qrCodePath, "letschat_qrcode:" + user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

        String qrCodeUrl = null;
        try {
            qrCodeUrl = fastDFSClient.uploadBase64(qrCodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setQrcode(qrCodeUrl);

        user.setId(userId);
        userMapper.insert(user);

        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users user) {
        userMapper.updateByPrimaryKeySelective(user);
        return queryUserById(user.getId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserById(String id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendUserName) {
        // 1. Return "no such user" if the given friendUserName doesn't exist.
        Users user = queryUserInfoByUsername(friendUserName);
        if (user == null) {
            return SearchFriendsStatusEnum.USER_NOT_EXIST.getStatus();
        }
        // 2. Return "Can not add yourself" if the given friendUserName coincides with current user's username.
        if (user.getId().equals(myUserId)) {
            return  SearchFriendsStatusEnum.NOT_YOURSELF.getStatus();
        }
        // 3. Return "You guys are already friends, start chatting now" if the current user is trying to re-add a friend.
        Example myFri = new Example(MyFriends.class);
        Criteria criteria = myFri.createCriteria();
        criteria.andEqualTo("myUserId", myUserId);
        criteria.andEqualTo("myFriendUserId", user.getId());
        MyFriends myFriends = myFriendsMapper.selectOneByExample(myFri);
        if (myFriends != null) {
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.getStatus();
        }

        return SearchFriendsStatusEnum.SUCCESS.getStatus();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfoByUsername(String username) {
        Example usr = new Example(Users.class);
        Criteria uc = usr.createCriteria();

        uc.andEqualTo("username", username);
        return userMapper.selectOneByExample(usr);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String myUserId, String friendUserName) {
        Users user = queryUserInfoByUsername(friendUserName);

        Example fre = new Example(FriendsRequest.class);
        Criteria frc = fre.createCriteria();
        frc.andEqualTo("sendUserId", myUserId);
        frc.andEqualTo("acceptUserId", user.getId());
        FriendsRequest request = friendsRequestMapper.selectOneByExample(fre);
        if (request == null) {
            // Send a request if the user is not your friend nor you haven't send him a request.
            String requestId = sid.nextShort();

            FriendsRequest friendsRequest = new FriendsRequest();
            friendsRequest.setId(requestId);
            friendsRequest.setSendUserId(myUserId);
            friendsRequest.setAcceptUserId(user.getId());
            friendsRequest.setRequestDateTime(new Date());

            friendsRequestMapper.insert(friendsRequest);
        }

    }

}
