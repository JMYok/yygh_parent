package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.mysql.cj.xdevapi.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @GetMapping("/findAll")
    public List<HospitalSet> findAllHospitalSet(){
        List<HospitalSet> list = hospitalSetService.list();
        return list;
    }
}
