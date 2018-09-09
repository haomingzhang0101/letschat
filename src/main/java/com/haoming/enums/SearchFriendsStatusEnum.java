package com.haoming.enums;

/**
 * 
 * @Description: 添加好友前置状态 枚举
 */
public enum SearchFriendsStatusEnum {
	
	SUCCESS(0, "OK"),
	USER_NOT_EXIST(1, "No such user."),
	NOT_YOURSELF(2, "Can not add yourself."),
	ALREADY_FRIENDS(3, "You guys are already friends, start chatting now.");
	
	public final Integer status;
	public final String msg;
	
	SearchFriendsStatusEnum(Integer status, String msg){
		this.status = status;
		this.msg = msg;
	}
	
	public Integer getStatus() {
		return status;
	}  
	
	public static String getMsgByKey(Integer status) {
		for (SearchFriendsStatusEnum type : SearchFriendsStatusEnum.values()) {
			if (type.getStatus() == status) {
				return type.msg;
			}
		}
		return null;
	}
	
}
