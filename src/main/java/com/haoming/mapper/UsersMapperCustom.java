package com.haoming.mapper;


import com.haoming.pojo.Users;
import com.haoming.utils.MyMapper;
import com.haoming.vo.FriendRequestVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersMapperCustom  extends MyMapper<Users>{

    List<FriendRequestVO> queryFriendRequestList(String accept_user_id);


}