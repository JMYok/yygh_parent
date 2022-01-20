package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;

public class TestRead {
    public static void main(String[] args) {
        //设置文件名称
        String filename = "C:/Users/37412/Desktop/easyexcel.xlsx";

        EasyExcel.read(filename, UserData.class,new ExcelListener())
                 .sheet("用户信息")
                 .doRead();
    }
}
