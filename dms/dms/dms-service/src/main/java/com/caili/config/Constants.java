package com.caili.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotEmpty;

/**
 * @Author: huey
 * @Desc: 微信 QQ登入必要参数，放在yml
 */
@ApiModel(value = "微信QQ登入必要参数")
@Data
@Configuration
@ConfigurationProperties(prefix = "constants")
public class Constants {

    @ApiModelProperty(value = "申请通过后qq获得的id",example = "123abc")
    @NotEmpty
    private String qqAppId;
    @ApiModelProperty(value = "申请通过后qq获得的密钥",example = "123abc")
    @NotEmpty
    private String qqAppSecret;
    @ApiModelProperty(value = "申请通过后wx获得的qq连接访问地址",example = "123abc")
    @NotEmpty
    private String qqRedirectUrl;


    @ApiModelProperty(value = "申请通过后wx获得的id",example = "http://")
    @NotEmpty
    private String weChatAppId;
    @ApiModelProperty(value = "申请通过后wx密钥",example = "123abc")
    @NotEmpty
    private String weChatAppSecret;
    @ApiModelProperty(value = "申请通过后wx获得的访问地址",example = "http://")
    @NotEmpty
    private String weChatRedirectUrl;

}
