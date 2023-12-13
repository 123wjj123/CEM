package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Customer;

public interface CustomerMapper extends BaseMapper<Customer,Integer> {
    Customer  selectByParams(String name);
    Customer  queryCustomerByName(String name);
}