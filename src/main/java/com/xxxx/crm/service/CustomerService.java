package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CustomerMapper;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.Customer;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerService extends BaseService<Customer,Integer> {
    @Resource
    private CustomerMapper customerMapper;
    /**
     * 多条件分页查询营销机会 (返回的数据格式必须满足LayUi中数据表格的格式
     */
    public Map<String,Object> queryCustomerByParams(CustomerQuery customerQuery){
        Map<String,Object>map=new HashMap<>();
        //开启分页
        PageHelper.startPage(customerQuery.getPage(),customerQuery.getLimit());
        //得到对应分页对象
        PageInfo<Customer> pageInfo=new PageInfo<>(customerMapper.selectByParams(customerQuery));
        //设置map对象
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        //设置分页好的列表
        map.put("data",pageInfo.getList());

        return map;
    }
    public  void saveCustomer(Customer customer){
        /**
         * 1.参数校验
         *     客户名称  name 非空 不可重复
         *     phone 联系电话  非空 格式合法
         *     法人  fr 非空
         * 2.参数默认值
         *     isValid
         *     createDate
         *     updateDate
         *     state  流失状态  0-未流失 1-已流失
         *3.执行添加 判断结果
         */
        checkParams(customer.getName(),customer.getPhone(),customer.getFr());
        AssertUtil.isTrue(null !=customerMapper.queryCustomerByName(customer.getName()),"该客户已存在!");

        customer.setIsValid(1);
        customer.setCreateDate(new Date());
        customer.setUpdateDate(new Date());
        customer.setState(0);

        // 设置客户编号
        String khno ="KH_"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        customer.setKhno(khno);
        AssertUtil.isTrue(insertSelective(customer)<1,"客户记录添加失败!");
    }

    private void checkParams(String name, String phone, String fr) {
        AssertUtil.isTrue(StringUtils.isBlank(name),"请指定客户名称!");
        AssertUtil.isTrue(!(PhoneUtil.isMobile(phone)),"手机号格式非法!");
        AssertUtil.isTrue(StringUtils.isBlank(fr),"请指定公司法人!");
    }
    public  void updateCustomer(Customer customer){
        /**
         * 1.参数校验
         *     id 存在性校验
         *     客户名称  name 非空 不可重复
         *     phone 联系电话  非空 格式合法
         *     法人  fr 非空
         * 2.参数默认值
         *     updateDate
         *3.执行更新 判断结果
         */
        Customer temp =selectByPrimaryKey(customer.getId());
        AssertUtil.isTrue(null ==temp,"待更新的客户记录不存在!");
        checkParams(customer.getName(),customer.getPhone(),customer.getFr());
        temp =customerMapper.queryCustomerByName(customer.getName());
        AssertUtil.isTrue(null !=temp && !(temp.getId().equals(customer.getId())),"该客户已存在!");
        customer.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(customer)<1,"客户记录更新失败!");
    }
    public void deleteCustomer(Integer id) {
        Customer customer = selectByPrimaryKey(id);
        AssertUtil.isTrue(null == customer,"待删除的客户记录不存在!");
        customer.setIsValid(0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(customer)<1,"客户记录删除失败!");
    }

}
