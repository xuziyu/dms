package com.caili.controller;

import com.caili.pojo.User;
import com.caili.pojo.UserInfo;
import com.caili.service.UserService;
import com.caili.utils.JacksonUtil;
import com.caili.utils.ResponseUtil;
import com.caili.utils.UserTokenManager;
import com.caili.utils.bcrypt.BCryptPasswordEncoder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caili.utils.WxResponseCode.AUTH_INVALID_ACCOUNT;

/**
 * @Author: huey
 * @Desc:  正常账号密码登入
 */
@Api(tags = "会员登入接口", description = "会员登入接口")
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 账号登录
     * @return 登录结果
     */
    @ApiOperation(value = "正常登录", notes = "用于：正常登录")
    @PostMapping("login")
    //@RequestBody String body, HttpServletRequest request
    public Object login(@RequestParam String mobile,@RequestParam String password) {
        //转成String
/*        String mobile = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");*/

       // String mobile = JacksonUtil.parseString(body, "mobile");
       // String password =JacksonUtil.parseString(body, "password");
        if (mobile == null || password == null) {
            return ResponseUtil.badArgument();
        }

//        List<LitemallUser> userList = userService.queryByUsername(username);
        List<User> userList = userService.queryByMobile(mobile);
        User user = null;
        if (userList.size() > 1) {
            return ResponseUtil.serious();
        } else if (userList.size() == 0) {
            return ResponseUtil.fail(AUTH_INVALID_ACCOUNT, "此号码未注册");
        } else {
            user = userList.get(0);
        }
        //密码验证 是否符合 编码规则
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //前端 过来的密码  和后台的密码 是否一致的判断  方法里面 有 解密
        if (!encoder.matches(password, user.getPassword())) {
            return ResponseUtil.fail(AUTH_INVALID_ACCOUNT, "账号密码不对");
        }

        // userInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(user.getNickname());
        userInfo.setAvatarUrl(user.getAvatar());
        userInfo.setUserLevel(user.getUserLevel().intValue());
        if (StringUtils.isEmpty(user.getDistNo())) {
            //查询是否vip
            userInfo.setIsDistVip((byte) 2);
        } else {
            userInfo.setSponsorNo(user.getSponsorNo());
            userInfo.setDistNo(user.getDistNo());
            userInfo.setIsDistVip((byte) 1);
        }

        // token
        String token = UserTokenManager.generateToken(user.getId());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return ResponseUtil.ok(result);
    }
}
