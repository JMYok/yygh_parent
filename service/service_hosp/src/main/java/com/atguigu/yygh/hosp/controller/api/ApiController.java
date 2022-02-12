package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    private DepartmentService departmentService;

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

    /**
     * 创建新的科室
     * @param request
     * @return
     */
    @ApiOperation(value = "上传科室")
    @PostMapping("saveDepartment")
    public Result saveDept(HttpServletRequest request){
        //获取传递过来的部门信息
        Map<String, String[]> map = request.getParameterMap();
        //转换数据形式方便之后操作
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(map);

        //1.获取医院系统传过来的签名
        String hospitalSignkey = (String) parameterMap.get("sign");

        //2.根据传递过来的医院编码，查询数据库得到签名
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

        departmentService.save(parameterMap);

        return Result.ok();
    }

    //查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        //获取传递过来的部门信息
        Map<String, String[]> map = request.getParameterMap();
        //转换数据形式方便之后操作
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(map);

        //医院的编号
        String hoscode = (String)parameterMap.get("hoscode");
        //获取医院系统传过来的签名
        String hospitalSignkey = (String) parameterMap.get("sign");

        //非必填
        String depcode = (String)parameterMap.get("depcode");
        int page = StringUtils.isEmpty(parameterMap.get("page")) ? 1 : Integer.parseInt((String)parameterMap.get("page"));
        int limit = StringUtils.isEmpty(parameterMap.get("limit")) ? 10 : Integer.parseInt((String)parameterMap.get("limit"));

        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        String signkey = hospitalSetService.getSignKey(hoscode);

        //将查询到的签名进行二次加密
        String encryptSignkey = MD5.encrypt(signkey);

        //比较两次加密的签名是否一样
        if(!hospitalSignkey.equals(encryptSignkey)){
            throw new  YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        departmentQueryVo.setDepcode(depcode);
        Page<Department> pageModel = departmentService.selectPage(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "删除科室")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        //获取传递过来的部门信息
        Map<String, String[]> map = request.getParameterMap();
        //转换数据形式方便之后操作
        Map<String, Object> parameterMap = HttpRequestHelper.switchMap(map);

        //1.获取医院系统传过来的签名
        String hospitalSignkey = (String) parameterMap.get("sign");

        //2.根据传递过来的医院编码，查询数据库得到签名
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

        String depcode = (String)parameterMap.get("depcode");

        departmentService.remove(hoscode,depcode);

        return Result.ok();
    }
}
