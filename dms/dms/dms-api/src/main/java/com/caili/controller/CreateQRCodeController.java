package com.caili.controller;

import com.caili.utils.QRCodeUtilP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: huey
 * @Desc: 信息生成二维码   可以放进入图片
 */
@RestController
@RequestMapping("/user")
@Api(tags = "文字生成二维码")
public class CreateQRCodeController {

    //C:\Users\admin\Desktop\test\123.jpg
    //C:\Users\admin\Desktop\test\1111.jpg

    /**
     *
     * @param imgPath
     * @param destPath
     * @param text
     */
    @GetMapping(value = "createQrCode")
    @ApiOperation(value = "文字生成二维码", notes = "imgPath：图片全地址，destPath：生成图片地址 都需要加上图片后缀")
    public void  createQrCode(@RequestParam("imgPath")String imgPath,
                              @RequestParam("destPath")String destPath,
                              @RequestParam("text")String text
    ){

        try {
            QRCodeUtilP.encode(text, imgPath, destPath, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
