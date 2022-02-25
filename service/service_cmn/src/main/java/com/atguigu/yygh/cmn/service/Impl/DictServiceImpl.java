package com.atguigu.yygh.cmn.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.http.HttpResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    DictMapper dictMapper;

    /**
     * 根据数据id查询子数据列表
     * @param id
     * @return
     */
    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChildDataById(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dicts = baseMapper.selectList(wrapper);
        for(Dict dict : dicts){
            boolean is_children = isChildren(dict.getId());
            dict.setHasChildren(is_children);
        }
        return dicts;
    }

    /**
     * 判断id下面是否有子节点
     * @param id
     * @return
     */
    private boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer cnt = baseMapper.selectCount(wrapper);
        return cnt>0;
    }

    /**
     * 导入数据字典
     * @param file
     */
    @Override
    @CacheEvict(value = "dict", allEntries=true)
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper))
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出数据为Excel
     * @param response
     */
    @Override
    public void exportDictData(HttpServletResponse response) {
        try {
            //设置下载信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");

            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("dict", "UTF-8");

            //让操作以下载方式打开
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            //查询数据库
            List<Dict> dicts = baseMapper.selectList(null);

            //Dict--DictEeVo
            List<DictEeVo> dictEeVos = new ArrayList<>();
            for(Dict dict:dicts){
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties(dict,dictEeVo);
                dictEeVos.add(dictEeVo);
            }

            //执行写操作
            EasyExcel.write(response.getOutputStream(), DictEeVo.class)
                    .sheet("dict")
                    .doWrite(dictEeVos);
            } catch (Exception e) {
                e.printStackTrace();
        }
    }

    /**
     * 根据上级编码与值获取数据字典名称
     * @param parentDictCode
     * @param value
     * @return
     */
    @Override
    public String getNameByParentDictCodeAndValue(String parentDictCode, String value) {
        //如果value能唯一定位数据字典，parentDictCode可以传空，例如：省市区的value值能够唯一确定
        if(StringUtils.isEmpty(parentDictCode)) {
            Dict dict = dictMapper.selectOne(new QueryWrapper<Dict>().eq("value", value));
            if(null != dict) {
                return dict.getName();
            }
        } else {
            Dict parentDict = this.getDictByDictsCode(parentDictCode);
            if(null == parentDict) return "";
            Dict dict = dictMapper.selectOne(new QueryWrapper<Dict>().eq("parent_id", parentDict.getId()).eq("value", value));
            if(null != dict) {
                return dict.getName();
            }
        }
        return "";
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        Dict codeDict = this.getDictByDictsCode(dictCode);
        if(null == codeDict) return null;
        return this.findChildDataById(codeDict.getId());
    }

    private Dict getDictByDictsCode(String parentDictCode){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code", parentDictCode);
        Dict dict = baseMapper.selectOne(wrapper);
        return dict;
    }
}
