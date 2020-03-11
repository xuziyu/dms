package com.caili.pojo.bo;

import com.caili.pojo.UserInfo;
import lombok.Data;

/**
 * @Author: huey
 * @Desc:
 */
@Data
public class WxLoginInfo {

    /**
     * 编号
     */
    private String code;
    /**
     * 用户信息
     */
    private UserInfo userInfo;
}
