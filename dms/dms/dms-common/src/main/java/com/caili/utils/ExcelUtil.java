package com.caili.utils;

/**
 * @Author: huey
 * @Desc:
 */



import org.apache.poi.hssf.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Excel工具类
 */
public class ExcelUtil {

    /**
     * Excel表格导出
     * @param response HttpServletResponse对象
     * @param excelData Excel表格的数据，封装为List<String[]>
     * @throws IOException 抛IO异常
     */
    public static void exportExcel(HttpServletResponse response,
                                   ExcelData excelData) throws IOException {

        //声明一个工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();

        //生成一个表格，设置表格名称
        HSSFSheet sheet = workbook.createSheet(excelData.getSheetName());//sheet的名字

        //设置表格列宽度
        sheet.setDefaultColumnWidth(excelData.getColumnWidth());//Excel表格的宽度，建议为15
        int rowIndex = 0;
        for(String[] data : excelData.getExcelData()){
            //创建一个row行，然后自增1
            HSSFRow row = sheet.createRow(rowIndex++);
            //遍历添加本行数据
            for (int i = 0; i < data.length; i++) {
                //创建一个单元格
                HSSFCell cell = row.createCell(i);
               // memberInfo=data.get(i);
                //创建一个内容对象
               HSSFRichTextString text = new HSSFRichTextString(data[i]);
                //将内容对象的文字内容写入到单元格中
                cell.setCellValue(text);
            }
        }
        //准备将Excel的输出流通过response输出到页面下载
        //application/octet-stream
        //八进制输出流
        response.reset(); // 清除buffer缓存
        // 指定下载的文件名
        String  fileName =  java.net.URLEncoder.encode(excelData.getFileName(),"UTF-8");
        //配置响应格式 和字符集
        response.setContentType("application/octet-stream");
        //设置请求头文件
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setCharacterEncoding("utf8");
        //transfer-encoding
        //response.setContentType("application/octet-stream");
      //  response.setHeader("Pragma", "no-cache");
        //response.setHeader("Cache-Control", "no-cache");
      //  response.setDateHeader("Expires", 0);
        //刷新缓冲
        response.flushBuffer();
    //    System.out.println(response.);
        //workbook将Excel写入到response的输出流中，供页面下载该Excel文件
        workbook.write(response.getOutputStream());
        //关闭workbook
        workbook.close();
    }

}

