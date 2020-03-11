package com.caili.utils;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: huey
 * @Desc:
 */
public class ExcelData implements Serializable {
    private static final long serialVersionUID = 6133772627258154184L;

    /**
     * 表头
     */
    private String sheetName;

    /**
     * 数据
     */
    private List<String[]> excelData;

    /**
     * 页签名称
     */
    private String fileName;

    private Integer columnWidth;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<String[]> getExcelData() {
        return excelData;
    }

    public void setExcelData(List<String[]> excelData) {
        this.excelData = excelData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
    }
}
