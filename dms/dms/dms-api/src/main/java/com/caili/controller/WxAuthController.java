package com.caili.controller;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.caili.pojo.User;
import com.caili.pojo.UserInfo;
import com.caili.pojo.bo.WxLoginInfo;
import com.caili.service.DmsMemberMaterService;
import com.caili.service.UserService;
import com.caili.config.GenerationDistNo;
import com.caili.utils.IpUtil;
import com.caili.utils.RedisKey;
import com.caili.utils.ResponseUtil;
import com.caili.utils.UserTokenManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: huey
 * @Desc:  登入 注册 包括 微信登入  号码密码登入  短信验证登入
 */



@RestController
@RequestMapping("/wx/user")
@Validated
@Slf4j
@Api(tags = "微信小程序登入接口", description = "微信小程序登入接口")
public class WxAuthController {

    @Autowired
    private WxMaService wxService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private GenerationDistNo generationDistNo;

    @Autowired
    private UserService userService;

    @Autowired
    private DmsMemberMaterService dmsMemberMaterService;

    @Autowired
    private RestTemplate restTemplate;

    //这边直接用 openFeigen 或者 restTemplate 去跨服务调用优惠券系统的东西
/*    @Autowired
    private CouponAssignService couponAssignService;*/


    /**
     * 微信登录
     * @param wxLoginInfo 请求内容，{ code: xxx, userInfo: xxx }
     * @param request     请求对象
     * @return 登录结果   @RequestParam("sponsorNo") String sponsorNo
     */
    @ApiOperation(value = "微信登录", notes = "用于：微信登录")
    @PostMapping("login_by_weixin")
    public Object loginByWeixin(@RequestBody WxLoginInfo wxLoginInfo, HttpServletRequest request) {
        String code = wxLoginInfo.getCode();
        UserInfo userInfo = wxLoginInfo.getUserInfo();
        if (code == null || userInfo == null) {
            return ResponseUtil.badArgument();
        }
        String sessionKey = null;
        String openId = null;
        try {
            //通过code获取access_token的接口。
            WxMaJscode2SessionResult result = this.wxService.getUserService().getSessionInfo(code);
            //接口调用凭证Id
            sessionKey = result.getSessionKey();
            //授权用户唯一标识
            openId = result.getOpenid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sessionKey == null || openId == null) {
            return ResponseUtil.fail();
        }

        User user = userService.queryByOid(openId);
        if (user == null) {
            user = new User();
            user.setUsername(openId);
            user.setPassword(openId);
            user.setWeixinOpenid(openId);
            //头像地址
            user.setAvatar(userInfo.getAvatarUrl());
            user.setNickname(userInfo.getNickName());
            user.setGender(userInfo.getGender());
            user.setStatus((byte) 0);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.getIpAddr(request));
            user.setSessionKey(sessionKey);
            if (!StringUtils.isEmpty(userInfo.getDistNo())) {
                user.setSponsorNo(userInfo.getDistNo());
                try {
                    generationDistNo.generationDistNo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String key = RedisKey.getKey("max_dist_no", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
                String distNo = redisTemplate.opsForValue().get(key);
                userInfo.setDistNo(distNo);
                user.setDistNo(distNo);
                userInfo.setIsDistVip((byte) 1);
                dmsMemberMaterService.setDistInfo(user);
            } else {
                userInfo.setIsDistVip((byte) 2);
            }
            userService.add(user);
            // 新用户发送注册优惠券
            //这边直接用 feigen 或者 restTemplate 去跨服务调用优惠券系统的东西
            //couponAssignService.assignForRegister(user.getId());
            //url 就可以填路径
            //第二个是参数
            //第三个是参数类型
            //restTemplate.postForObject("",user.getId(),String.class);

        } else {
            // 头像
            userInfo.setAvatarUrl(user.getAvatar());
            // 名字
            userInfo.setNickName(user.getNickname());
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.getIpAddr(request));
            user.setSessionKey(sessionKey);
            if (StringUtils.isEmpty(user.getDistNo())) {
                //查询是否vip
  /*              userInfo.setSponsorNo(user.getSponsorNo());
                userInfo.setDistNo(user.getDistNo());*/
                userInfo.setIsDistVip((byte) 2);
            } else {
                userInfo.setSponsorNo(user.getSponsorNo());
                userInfo.setDistNo(user.getDistNo());
                userInfo.setIsDistVip((byte) 1);
            }
            if (userService.updateById(user) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
            // 用户等级
        }
        userInfo.setUserLevel(user.getUserLevel().intValue());
        // token
        String token = UserTokenManager.generateToken(user.getId());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return ResponseUtil.ok(result);
    }





}
