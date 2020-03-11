package com.caili.controller;

import com.caili.utils.VerificationCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;

/**
 * @Author: huey
 * @Desc:
 */
@RequestMapping("/user")
@RestController
@Api(tags = "发送邮件")
public class EmailController {

    @Value("${spring.mail.host}")
    public String  mail;

    @Value("${spring.mail.username}")
    public String  username;

    @Value("${spring.mail.password}")
    public String  password;

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * 邮件发送 1 普通文本 2 html格式 3 带文本文件 4 带图片
     * @param type
     * @return
     * @throws MessagingException
     */
    @ApiOperation(value = "邮件发送", notes = "1 普通文本 2 html格式 3 带文本文件 4 带图片")
    @GetMapping("/email")
    public String sendEmail(@RequestParam Integer type,
                            @RequestParam String mailUrl) throws MessagingException{
        String code="您的验证码："+VerificationCodeUtils.getCaptcha();
        //todo 为了防止频繁刷 这里跟短信验证一样 做一个验证
        String img="C:\\Users\\admin\\Desktop\\test\\1111.jpg";
        String fileNam="C:\\Users\\admin\\Desktop\\test\\test.txt";
       // SimpleMailMessage mailMessage = new SimpleMailMessage();
        MimeMessage mineMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mineMessage, true);
        // 邮件发送人
        mimeMessageHelper.setFrom(username);

        // 邮件收件人
        mimeMessageHelper.setTo(mailUrl);
        // 邮件标题
        mimeMessageHelper.setSubject("才立商城");
        FileSystemResource file =null;
        if(type==3){
             file = new FileSystemResource(new File(fileNam));
        }else {
            file = new FileSystemResource(new File(img));
        }
        switch (type){
            case 1: mimeMessageHelper.setText(code);  break;
            case 2: mimeMessageHelper.setText("<span style='color:red'>" + code + "</span>", true);
                break;
            case 3:
                mimeMessageHelper.setText(code, true);
                // 添加附件
                String fileName = file.getFilename();
               // mimeMessageHelper.addAttachment(fileName, file); //可以重复添加文件
                mimeMessageHelper.addAttachment(fileName, file);   break;
            case 4:
                String fileId = String.valueOf(System.currentTimeMillis());
                mimeMessageHelper.setText(code+"：图片"+": <img src='cid:" + fileId + "'></img>", true);
                //这种方式是把图片放在 文字后面
                mimeMessageHelper.addInline(fileId,file);
                break;
            default:
        }
        // 调用 api, 发送邮件
        javaMailSender.send(mineMessage);

        return "文本邮件发送成功: " + LocalDateTime.now();
    }



}
