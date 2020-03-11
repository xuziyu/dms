package com.caili.controller;

import com.caili.pojo.User;
import com.caili.service.UserService;
import com.caili.utils.ExcelData;
import com.caili.utils.ExcelUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: huey
 * @Desc: 导出execl  目前方便测试 放在这里，后面随机放入实现类里面
 */
@RequestMapping("/user")
@RestController
@Api(tags = "导出execl")
public class ExcelController {
    @Autowired
    private UserService userService;

    /**
     * 转换类型
     * @param memberInfos 要转换的类型
     * @return 转换后的类型
     */
    private List<String[]> convertList(List<User> memberInfos){
        List<String[]> list = null;
        if(null != memberInfos && memberInfos.size() > 0){
            list = new ArrayList<>();
            for(User memberInfo : memberInfos){
                String[] strings = new String[]{memberInfo.getDistNo(),memberInfo.getUsername(),memberInfo.getIdCar(),memberInfo.getIdCarName()};
                list.add(strings);
            }
        }
        return list;
    }

    @RequestMapping(value = "test",method = RequestMethod.GET,produces="application/json;charset=UTF-8")
    public void excelDownloadSecond(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String suffix=".xls";
        List<User> memberInfos=userService.queryByUsername("");
        List<String[]> list=new ArrayList<>();
        String[] headList={"会员编号","会员姓名","地址","城市"};
        list.add(headList);
        List<String[]> dateList=convertList(memberInfos);
        list.addAll(dateList);
        ExcelData excelData=new ExcelData();
        excelData.setExcelData(list);
        excelData.setColumnWidth(15);
        String date=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        excelData.setFileName("test"+date+suffix);
        excelData.setSheetName("会员信息");
        ExcelUtil.exportExcel(response, excelData);
    }


}
