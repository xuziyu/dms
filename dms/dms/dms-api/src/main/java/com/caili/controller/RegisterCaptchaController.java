package com.caili.controller;

import com.aliyuncs.exceptions.ClientException;
import com.caili.config.AliyunSmsTask;
import com.caili.config.CaptchaCodeManager;
import com.caili.utils.RegexUtil;
import com.caili.utils.ResponseUtil;
import com.caili.utils.VerificationCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.caili.utils.WxResponseCode.AUTH_CAPTCHA_FREQUENCY;

/**
 * @Author: huey
 * @Desc:
 */
@RequestMapping("user")
@RestController
@Api(tags = "用于发送短信验证",description ="短信验证" )
public class RegisterCaptchaController {


    @Autowired
    private AliyunSmsTask aliyunSmsSender;

    @Autowired
    private CaptchaCodeManager captchaCodeManager;

    /**
     * 请求验证码
     * @param phoneNumber 手机号码 { mobile }
     * @return
     */
    @ApiOperation(value = "短信发送", notes = "用于：短信发送")
    @PostMapping("regCaptcha")
    public Object registerCaptcha(@RequestParam String phoneNumber) {

        //验证电话号码
        if (!RegexUtil.isMobileExact(phoneNumber)) {
            return ResponseUtil.fail(402, "手机号码不正确");
        }
        String code = VerificationCodeUtils.getCaptcha();
        boolean successful = captchaCodeManager.addToCache(phoneNumber, code);
        //这里机制防止短信验证码被滥用
        if (!successful) {
            return ResponseUtil.fail(AUTH_CAPTCHA_FREQUENCY, "验证码未超时1分钟，不能发送");
        }
        //发送验证信息
        try {
            aliyunSmsSender.sendSms(phoneNumber, code);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok();
    }
}
