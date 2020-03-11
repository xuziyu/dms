package com.caili.service;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * @Author: huey
 * @Desc:
 */
public interface AuthService {

    abstract String getAccessToken(String code);
    abstract String getOpenId(String accessToken);
    abstract String refreshToken(String code);
    abstract String getAuthorizationUrl() throws UnsupportedEncodingException;
    abstract JSONObject getUserInfo(String accessToken, String openId);

}
