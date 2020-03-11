package com.caili.controller;

import com.caili.config.CaptchaCodeManager;
import com.caili.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import static com.caili.utils.WxResponseCode.AUTH_CAPTCHA_UNSUPPORT;

/**
 * @Author: huey
 * @Desc:  短信验证登入
 */
@RestController
@RequestMapping("/user")
@Api(tags = "短信验证登入接口",description = "短信验证登入接口")
public class MessageLoginController {

    @Autowired
    private CaptchaCodeManager captchaCodeManager;
    @ApiOperation(value = "短信验证登入", notes = "用于：短信验证登入")
    @PostMapping("message")
    public Object registerCaptcha(@RequestParam String phoneNumber,@RequestParam String code) {

        if (StringUtils.isEmpty(phoneNumber)) {
            return ResponseUtil.fail(402, "手机号码不正确");
        }
        if(StringUtils.isEmpty(code)){
            return ResponseUtil.fail(402, "验证码不正确");
        }
        String message = captchaCodeManager.getCachedCaptcha(phoneNumber);
        //这里机制防止短信验证码被滥用
        if (StringUtils.isEmpty(message)) {
            return ResponseUtil.fail(402, "验证码不存在");
        }
        if(code.equals(message)){
            return ResponseUtil.ok();
        }else {
            return ResponseUtil.fail(402, "验证码错误");
        }
    }


}
