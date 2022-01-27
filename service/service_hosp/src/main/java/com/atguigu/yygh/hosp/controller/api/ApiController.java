package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.HttpUtil;
import com.atguigu.yygh.hosp.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 从请求中得到参数，解析参数然后放到数据库中
 */
@RestController
@RequestMapping("api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * 上传医院接口
     * @param request
     * @return
     */
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> map = request.getParameterMap();
            //转换数据形式方便之后操作
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(map);

        //调用service方法
        hospitalService.save(parameterMap);

        return Result.ok();
    }
}
