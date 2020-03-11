package com.caili.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: huey
 * @Desc:
*/

@Data
public class DmsMemberMasterEntity {

    private Integer id;

    private String distNo;

    private String distName;

    private String  sponsorName;

    private String sponsorNo;

    private Integer levelNo;

    private Integer pin;

    private Integer distStatus;

    private String telephone;

    @JsonFormat(pattern = "yyyyMM")
    private Date updateTime;



}
