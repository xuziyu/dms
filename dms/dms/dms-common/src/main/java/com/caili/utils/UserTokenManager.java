package com.caili.utils;



/**
 * 维护用户token
 */
public class UserTokenManager {
    //生成token
	public static String generateToken(Integer id) {
        JwtHelper jwtHelper = new JwtHelper();
        return jwtHelper.createToken(id);
    }
    //获取token
    public static Integer getUserId(String token) {
    	JwtHelper jwtHelper = new JwtHelper();
    	Integer userId = jwtHelper.verifyTokenAndGetUserId(token);
    	if(userId == null || userId == 0){
    		return null;
    	}
        return userId;
    }
}
