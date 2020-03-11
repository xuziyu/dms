package com.caili.controller;

import com.caili.config.FileUpload;
import com.caili.pojo.Users;
import com.caili.utils.CookieUtils;
import com.caili.utils.DateUtil;
import com.caili.utils.JSONResult;
import com.caili.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: huey
 * @Desc: 图片上传
 */
@Slf4j
@Api(tags = "图片上传", description = "图片上传")
@RestController
@RequestMapping("/user")
public class UploadFaceController {


    @Autowired
    private FileUpload fileUpload;

    //把客户从 他本地的图片 重命名 保存到 我们存图片的地方，并且，图片地址放入数据库
    @ApiOperation(value = "用户头像修改", notes = "用户头像修改", httpMethod = "POST")
    @PostMapping("uploadFace")
    public JSONResult uploadFace(MultipartFile file,
                                 HttpServletRequest request,
                                 HttpServletResponse response, @RequestParam String userId){
        //定义保存头像地址
        //String fileSpace=IMAGE_USER_FACE_LOCATION;
        //路径放到了配置中心  file-upload-dev.properties
        String fileSpace=fileUpload.getImageUserFaceLocation();
        //在为每个用户 给不同的用户上传
        String uploadPathPreFix=File.separator+ userId;
        String uploadPathPrefix="/"+userId;
        log.info("uploadPathPreFix"+uploadPathPrefix);
        //开始文件上传
        if(file==null){
            return JSONResult.errorMsg("文件不能为空");
        }else {
            FileOutputStream fileOutputStream = null;
            try {
                //获得文件上传名称
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件重命名  i-face.png -> ["i-face", "png"]
                    String fileNameArr[] = fileName.split("\\.");
                    //获取文件后缀名称
                    String suffix = fileNameArr[fileNameArr.length - 1];
                    if (!suffix.equalsIgnoreCase("png") &&
                            !suffix.equalsIgnoreCase("jpg") &&
                            !suffix.equalsIgnoreCase("jpeg") ) {
                        return JSONResult.errorMsg("图片格式不正确！");
                    }

                    // face-{userid}.png
                    // 文件名称重组 覆盖式上传，增量式：额外拼接当前时间
                    String newFileName = "face-" + userId +"."+suffix;
                    //文件上传地址
                    String finalFacePath = fileSpace + uploadPathPreFix + File.separator + newFileName;
                    uploadPathPrefix+=("/"+newFileName);
                    File outFile = new File(finalFacePath);
                    //路径不为空
                    if (outFile.getParentFile() != null) {
                        // 创建文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    //文件输出保存到目录
                    fileOutputStream = new FileOutputStream(outFile);
                    //文件输入的地方
                    InputStream inputStream=file.getInputStream();
                    //将输入流中的内容拷贝到输出流中，并可以指定字符编码
                    IOUtils.copy(inputStream,fileOutputStream);}
            }catch(IOException e){
                e.printStackTrace();
            }finally {
                if(fileOutputStream!=null){
                    try {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String imageServerUrl=fileUpload.getImageServerUrl();
        //加时间戳 为了前端缓存导致 图片不能更换
        String finalUserFaceUrl=imageServerUrl+uploadPathPrefix+"?t"+DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN);
        // 更新用户头像到数据库
/*        Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);
        userResult = setNullProperty(userResult);*/
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(finalUserFaceUrl), true);
        // TODO 后续要改，增加令牌token，会整合进redis，分布式会话
        return JSONResult.ok();
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }

}
