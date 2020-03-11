package com.caili.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caili.config.AccessToken;
import com.caili.config.Constants;
import com.caili.pojo.User;
import com.caili.utils.RestTemplateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: huey
 * @Desc: qq 微信集成 扫码登入
 */
@RequestMapping("/user")
@RestController
@Api(tags = "微信qq扫码登入")
public class WxQQLoginController {

    @Autowired
    private Constants constants;

    @Autowired
    private RestTemplate restTemplate;
    /**
     * 根据不同的登录类型返回相应的地址，前端直接使用新窗口或者悬浮窗口打开：
     * @param type  1 qq  2微信
     * @return
     */
    @ApiOperation(value = "微信qq扫码登入", notes = "根据不同的登录类型返回相应的地址，前端直接使用新窗口或者悬浮窗口打开： type:1 qq  2微信")
    @PostMapping("/third/login")
    public String getLoginUrl(String type) {
        //前端打开后会出现登录的二维码等，扫描之后会跳转到我们配置的redirect_uri并在url后面返回code参数和我们自定义的state参数
        String uuid=UUID.randomUUID().toString().replaceAll("-","");
        if ("1".equals(type)) {
            // QQ
            return "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id="
                    + constants.getQqAppId()
                    + "&redirect_uri="
                    + constants.getQqRedirectUrl() //回调地址
                    + "&state="
                    + uuid;
        } else {
            // 微信
            /**
             * 第三方使用网站应用授权登录前请注意已获取相应网页授权作用域（scope=snsapi_login），则可以通过在PC端打开以下链接：
             * https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
             * 若提示“该链接无法访问”，请检查参数是否填写错误，如redirect_uri的域名与审核时填写的授权域名不一致或scope不为snsapi_login。
             */

            try {
                return "https://open.weixin.qq.com/connect/qrconnect?appid="
                        + constants.getWeChatAppId()
                        + "&redirect_uri="
                        + constants.getWeChatRedirectUrl()
                        + "&response_type=code&scope=snsapi_login&state="
                        + URLEncoder.encode(
                        URLEncoder.encode(uuid, "utf-8"),
                        "utf-8") + "#wechat_redirect";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @GetMapping("/qq")
    //前端拿到code传给后台 获取 access_token,openid 注意 qq 是要分开拿这两个参数，微信不需要
    public String getAccessToken(String code) {
        String url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id="
                + constants.getQqAppId()
                + "&client_secret="
                + constants.getQqAppSecret()
                + "&code="
                + code
                + "&redirect_uri=" + constants.getQqRedirectUrl();
        String resp = restTemplate.getForObject(url,
                String.class);
        if (resp != null && resp.contains("access_token")) {
            Map<String, String> map = RestTemplateUtil.URLRequest(resp);
            String access_token = map.get("access_token");
            //获取到 openid
            String openid= getOpenId(access_token);
            return access_token;
            //看前端是否需要返回
        }
        return null;
    }



    public String getOpenId(String accessToken) {
        String url = "https://graph.qq.com/oauth2.0/me?access_token="
                + accessToken;
        String resp = restTemplate.getForObject(url,
                String.class);
        if (resp != null && resp.contains("openid")) {
            Map<String, String> map = RestTemplateUtil.URLRequest(resp);
            String openid= map.get("access_token");
            getUserInfoQq(accessToken,openid);
            return openid;
        }
        return null;
    }

    /**
     * 微信通过code 获取 accessToken
     * @param code
     * @return
     */
    @GetMapping("wx")
    public AccessToken getAccessTokenWx(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + constants.getWeChatAppId() + "&secret="
                + constants.getWeChatAppSecret() + "&code=" + code
                + "&grant_type=authorization_code";
        String resp = restTemplate.getForObject(url,
                String.class);
        if (resp != null && resp.contains("access_token")) {
            AccessToken accessToken = JSON.parseObject(resp, AccessToken.class);
            //非必须（国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语，默认为zh-CN）
            //这里可以做成动态的
            getUserInfoWx(accessToken.getAccess_token(),accessToken.getOpenid(),"zh-CN");
            return accessToken;
        }
        return null;
    }

    /**
     * qq获取用户信息
     * @param accessToken
     * @param openId
     * @return
     */

    public String getUserInfoQq(String accessToken, String openId) {
        String url = "https://graph.qq.com/user/get_user_info?access_token="
                + accessToken + "&oauth_consumer_key=" + constants.getQqAppId()
                + "&openid=" + openId;
        String resp = restTemplate.getForObject(url,
                String.class);
        //把获取到的信息放到 数据库里面 所以此方法 和放到实现类
        return resp;
    }

    /**
     * 获取用户信息
     * wx
     * @param accessToken
     * @param openId
     * @param lang
     * @return
     */
    public String getUserInfoWx(String accessToken, String openId, String lang) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
                + accessToken + "&openid=" + openId;
        if (lang != null && !"".equals(lang)) {
            url += "&lang=" + lang;
        }
        String resp = restTemplate.getForObject(url,
                String.class);
        //这里拿到的东西 需要存入数据库
        return resp;
    }


}
