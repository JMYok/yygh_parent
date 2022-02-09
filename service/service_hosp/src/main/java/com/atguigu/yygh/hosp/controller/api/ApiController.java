package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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

    @Autowired
    private HospitalSetService hospitalSetService;

    //查询医院
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> map = request.getParameterMap();
        //转换数据形式方便之后操作
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(map);

        //1.获取医院系统传过来的签名
        String hospitalSignkey = (String) parameterMap.get("sign");

        //2.根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String)parameterMap.get("hoscode");

        if(StringUtils.isEmpty(hoscode)){
            throw new  YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        String signkey = hospitalSetService.getSignKey(hoscode);


        //3.将查询到的签名进行二次加密
        String encryptSignkey = MD5.encrypt(signkey);

        //4.比较两次加密的签名是否一样
        if(!hospitalSignkey.equals(encryptSignkey)){
            throw new  YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //调用service实现医院编号的查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);

        return Result.ok(hospital);
    }

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

        //1.获取医院系统传过来的签名
        String hospitalSignkey = (String) parameterMap.get("sign");

        //2.根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String)parameterMap.get("hoscode");

        if(StringUtils.isEmpty(hoscode)){
            throw new  YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        String signkey = hospitalSetService.getSignKey(hoscode);


        //3.将查询到的签名进行二次加密
        String encryptSignkey = MD5.encrypt(signkey);

        //4.比较两次加密的签名是否一样
        if(!hospitalSignkey.equals(encryptSignkey)){
            throw new  YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoData = (String) parameterMap.get("logoData");
        String newlogoData = logoData.replaceAll(" ", "+");
        parameterMap.put("logoData",newlogoData);

        //调用service方法
        hospitalService.save(parameterMap);
        return Result.ok();
    }
}
