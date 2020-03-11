package com.caili.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Author: huey
 * @Desc: 路径封装
 */
@Component
@ConfigurationProperties(prefix = "file")
@Data
@PropertySource("classpath:file-upload-dev.properties")
public class FileUpload {
    private  String imageUserFaceLocation;
    private String imageServerUrl;


}
