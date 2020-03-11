package com.caili.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.caili.config.Constants;
import com.caili.service.QQAuthService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * @Author: huey
 * @Desc:
 */
public class QQAuthServiceImpl implements QQAuthService  {

    @Autowired
    private Constants constants;

    @Override
    public String getAccessToken(String code) {
        return null;
    }

    @Override
    public String getOpenId(String accessToken) {
        return null;
    }

    @Override
    public String refreshToken(String code) {
        return null;
    }

    @Override
    public String getAuthorizationUrl() throws UnsupportedEncodingException {
        //获取  code
        String uuid=UUID.randomUUID().toString().replaceAll("-","");
        return "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id="
                + constants.getQqAppId()
                + "&redirect_uri="
                + constants.getQqRedirectUrl() //回调地址
                + "&state="
                + uuid;
    }

    @Override
    public JSONObject getUserInfo(String accessToken, String openId) {
        return null;
    }
}
