package com.atguigu.yygh.hosp.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    //创建新的科室
    @Override
    public void save(Map<String, Object> parameterMap) {
        //将参数的map集合转换为对象 Hospital
        String mapString = JSONObject.toJSONString(parameterMap);
        Department department = JSONObject.parseObject(mapString, Department.class);

        //判断是否存在数据
        String hoscode = department.getHoscode();
        String depcode = department.getDepcode();
        Department targetDepartment  = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode,depcode);

        if(null != targetDepartment ){
            BeanUtils.copyProperties(department,targetDepartment,Department.class);
            departmentRepository.save(targetDepartment);
        }else{
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }

    }

}
