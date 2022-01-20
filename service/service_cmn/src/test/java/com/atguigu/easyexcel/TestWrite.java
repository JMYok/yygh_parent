package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestWrite {
    public static void main(String[] args) {
        //设置文件名称
        String filename = "C:/Users/37412/Desktop/easyexcel.xlsx";

        List<UserData> list = new ArrayList<>();
        for(int i=1;i<=10;i++) {
            UserData data = new UserData();
            data.setUid(i);
            data.setUsername("VIP用户"+i);
            list.add(data);
        }
        EasyExcel.write(filename,UserData.class).sheet("用户信息").doWrite(list);
    }
}
