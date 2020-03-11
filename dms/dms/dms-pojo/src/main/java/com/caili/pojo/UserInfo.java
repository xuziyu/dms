package com.caili.pojo;

import lombok.Data;

@Data
public class UserInfo {

    //姓名
    private String nickName;
    //头像连接地址
    private String avatarUrl;
    //国籍
    private String country;
    //省份
    private String province;
    //城市
    private String city;
    //语言
    private String language;
    //性别
    private Byte gender;

    private String distNo;

    private byte isDistVip;

    private String sponsorNo;

    private Integer userLevel;

}
