package com.caili.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class UserListDto implements Serializable {

    private Integer id;

    private String username;

    private String mobile;

    private String distNo;

    private Byte pin;

    private Byte deleted;

    private String province;

    private Date addTime;

    private Date memberAddTime;

    private Date vipTime;

    private Date svipTime;

    private Date updateTime;

    private String sponsorNo;

}
