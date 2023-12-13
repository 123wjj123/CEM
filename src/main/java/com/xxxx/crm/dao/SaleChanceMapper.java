package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.SaleChance;

import java.util.List;
import java.util.Map;

public interface SaleChanceMapper extends BaseMapper<SaleChance,Integer> {
    /**
     * 多条件查询不需要单独定义
     * 由于多个模块涉及到多个条件查询操作，所以将对应的多条件查询功能定义在父接口BaseMapper
     */

}