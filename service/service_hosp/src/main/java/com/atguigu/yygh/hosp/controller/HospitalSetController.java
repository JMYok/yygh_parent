package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //1 查询医院设置表所有信息
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("/findAll")
    public Result findAllHospitalSet(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    //2 逻辑删除医院设置
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("/{id}")
    public Result deleteHospitalSet(@PathVariable("id") Integer id){
        boolean status = hospitalSetService.removeById(2L);
        if(status){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //3 条件查询带分页
    @ApiOperation("条件分页查询")
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {

        //创建page对象
        Page<HospitalSet> hospitalSetPage = new Page<>(current, limit);

        //构建查询条件
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();

        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();

        if(!StringUtils.isEmpty(hosname)){
            queryWrapper.like("hosname",hosname);
        }

        if(!StringUtils.isEmpty(hoscode)){
            queryWrapper.eq("hoscode",hoscode);
        }

        //调用分页方法
        Page<HospitalSet> page = hospitalSetService.page(hospitalSetPage,queryWrapper);

        return Result.ok(page);
    }

}
