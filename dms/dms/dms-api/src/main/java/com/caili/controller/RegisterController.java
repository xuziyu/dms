package com.caili.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.caili.pojo.User;
import com.caili.pojo.UserInfo;
import com.caili.service.UserService;
import com.caili.config.CaptchaCodeManager;
import com.caili.utils.*;
import com.caili.utils.bcrypt.BCryptPasswordEncoder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caili.utils.WxResponseCode.*;

/**
 * @Author: huey
 * @Desc:  账号 密码 注册
 */
@Api(tags = "会员注册", description = "会员注册")
@RestController
@RequestMapping("/user")
public class RegisterController {


    @Autowired
    private UserService userService;

    @Autowired
    private WxMaService wxService;

    @Autowired
    private CaptchaCodeManager captchaCodeManager;
    /**
     * 账号注册
     *
     * @param
     * @param request 请求对象
     * @return 登录结果
     */
    @ApiOperation(value = "正常注册", notes = "用于：正常注册")
    @PostMapping("/register")
    public Object register(@RequestParam String username,@RequestParam String password,@RequestParam String mobile,@RequestParam String code, HttpServletRequest request) {


/*        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");
        String mobile = JacksonUtil.parseString(body, "mobile");
        String code = JacksonUtil.parseString(body, "code");*/
        //String wxCode = JacksonUtil.parseString(body, "wxCode");

        //String wxCode = wxRegisterBo.getWxCode();
        //|| StringUtils.isEmpty(wxCode)
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(mobile)
                || StringUtils.isEmpty(code)) {
            return ResponseUtil.badArgument();
        }

        List<User> userList = userService.queryByUsername(username);
        if (userList.size() > 0) {
            return ResponseUtil.fail(AUTH_NAME_REGISTERED, "用户名已注册");
        }
        userList = userService.queryByMobile(mobile);
        if (userList.size() > 0) {
            return ResponseUtil.fail(AUTH_MOBILE_REGISTERED, "手机号已注册");
        }
        if (!RegexUtil.isMobileExact(mobile)) {
            return ResponseUtil.fail(AUTH_INVALID_MOBILE, "手机号格式不正确");
        }
        //判断验证码是否正确
        String cacheCode = captchaCodeManager.getCachedCaptcha(mobile);
        if (cacheCode == null || cacheCode.isEmpty() || !cacheCode.equals(code)) {
            return ResponseUtil.fail(AUTH_CAPTCHA_UNMATCH, "验证码错误");
        }

       /* String openId = null;
        try {
            //调用微信接口 获取标识id
            WxMaJscode2SessionResult result = this.wxService.getUserService().getSessionInfo(wxCode);
            openId = result.getOpenid();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail(AUTH_OPENID_UNACCESS, "openid 获取失败");
        }

        if (StringUtils.isNotBlank(openId)) {
            User user = userService.queryByOid(openId);

            // 用户已经存在
            if (user != null) {

                // 判断手机号码是否存在
                if (StringUtils.isEmpty(user.getMobile())) {
                    // 绑定手机号码
                    user.setMobile(mobile);
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    user.setUsername(username);
                    user.setPassword(encoder.encode(password));
                    userService.updateById(user);

                    // token
                    String token = UserTokenManager.generateToken(user.getId());

                    // userInfo
                    UserInfo userInfo = new UserInfo();
                    userInfo.setNickName(user.getNickname());
                    userInfo.setAvatarUrl(user.getAvatar());
                    // 会员
                    if (StringUtils.isNotBlank(user.getDistNo())) {
                        userInfo.setIsDistVip((byte) 1);
                    } else {
                        userInfo.setIsDistVip((byte) 2);
                    }
                    Map<Object, Object> result = new HashMap<>();
                    result.put("token", token);
                    result.put("userInfo", userInfo);
                    return ResponseUtil.ok(result);
                }
                return ResponseUtil.fail(WxResponseCode.AUTH_INVALID_ACCOUNT, "openid已绑定账号");
            }
        }
*/
        User user = null;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //BCrypt 加密
        String encodedPassword = encoder.encode(password);

        user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setMobile(mobile);
        //user.setWeixinOpenid(openId);
        //头像地址  给一个默认地址
        user.setAvatar("https://yanxuan.nosdn.127.net/80841d741d7fa3073e0ae27bf487339f.jpg?imageView&quality=90&thumbnail=64x64");
        user.setNickname(username);
        user.setGender((byte) 0);
        user.setUserLevel((byte) 0);
        user.setStatus((byte) 0);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(IpUtil.getIpAddr(request));
        userService.add(user);

        // 给新用户发送注册优惠券
       // couponAssignService.assignForRegister(user.getId());

        // userInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(username);
        userInfo.setAvatarUrl(user.getAvatar());
        userInfo.setIsDistVip((byte) 2);

        // token
        String token = UserTokenManager.generateToken(user.getId());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return ResponseUtil.ok(result);
    }
}
