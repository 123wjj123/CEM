package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
@Controller
public class IndexController extends BaseController {
    @Resource
    private UserService userService;
 /**
   * 系统登录页
   * @return
  */
    @RequestMapping("index")
    public String index(){
    return "index";
           }
       // 系统界面欢迎页
    @RequestMapping("welcome")
       public String welcome(){
       return "welcome";
          }
          /*** 后端管理主页面* @return*/
    @RequestMapping("main")
        public String main(HttpServletRequest request){
          //获取cookie中的用户id
          Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
          User user= userService.selectByPrimaryKey(userId);
          request.getSession().setAttribute("user",user);
           return "main";
            }
}
