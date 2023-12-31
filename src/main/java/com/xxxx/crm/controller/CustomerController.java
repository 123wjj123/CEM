package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.service.CustomerOrderService;
import com.xxxx.crm.service.CustomerService;
import com.xxxx.crm.vo.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("customer")
public class CustomerController extends BaseController {
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerOrderService customerOrderService;

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerByparams(CustomerQuery customerQuery) {
        return customerService.queryCustomerByParams(customerQuery);
    }
    @RequestMapping("index")
    public String index(){
        return "customer/customer";
    }
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveCustomer(Customer customer){
        customerService.saveCustomer(customer);
        return  success("客户记录添加成功!");
    }
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCustomer(Customer customer){
        customerService.updateCustomer(customer);
        return  success("客户记录更新成功!");
    }
    @RequestMapping("addOrUpdateCustomerPage")
    public String addOrUpdateCustomerPage(Integer id, Model model){
        model.addAttribute("customer",customerService.selectByPrimaryKey(id));
        return "customer/add_update";
    }
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteCustomer(Integer id){
        customerService.deleteCustomer(id);
        return success("客户记录删除成功!");
    }
    @RequestMapping("orderInfoPage")
    public String orderInfoPage(Integer cid, Model model){
        model.addAttribute("customer",customerService.selectByPrimaryKey(cid));
        return "customer/customer_order";
    }
    @RequestMapping("orderDetailPage")
    public String orderDetailPage(Integer orderId,Model model){
        model.addAttribute("order",customerOrderService.queryCustomerOrderByOrderId(orderId));
        return "customer/customer_order_detail";
    }
}
