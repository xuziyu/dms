package com.caili.utils;

/**
 * @Author: huey
 * @Desc:
 */
public class RedisKey {

    private final static  String SUFFER="_";

    private final static  String POINT=".";

    public static String  getKey(String id,String bonusMonth){
        return id+SUFFER+POINT+bonusMonth;
    }




}
