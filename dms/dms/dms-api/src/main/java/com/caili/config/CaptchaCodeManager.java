package com.caili.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Author: huey
 * @Desc: 缓存系统中的验证码 在数据量大的时候，第一种方式并不适合
 * 这里改成 redis
 */
@Component
public class CaptchaCodeManager {


    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 把验证码放如redis
     * @param phoneNumber
     * @param code
     * @return
     */
    public  boolean addToCache(String phoneNumber, String code) {
        String value=redisTemplate.opsForValue().get(phoneNumber);
        if(StringUtils.isEmpty(value)){
            // 不知道是不是想多了   实际情况下 如果redis在项目用的多 崩了 也是影响到好多地方的  所以感觉不需要考虑  看开发个人
            //todo 如果出现redis服务出问题，需要想下如何验证
            // 捕捉异常 放入本地内存 拿取的时候 在本地发现 有这个会员编号出现就 取本地的 需要设置过期时间
            redisTemplate.opsForValue().set(phoneNumber,code);
            redisTemplate.expire(phoneNumber,60,TimeUnit.SECONDS);
            return true;
        }else {
            return false;
        }

    }


    public  String getCachedCaptcha(String phoneNumber) {
        Boolean key=redisTemplate.hasKey(phoneNumber);
        if(!key){
            return null;
        }
        String value=redisTemplate.opsForValue().get(phoneNumber);
        if(StringUtils.isEmpty(value)){
            return null;
        }else {
            return value;
        }
    }


}
