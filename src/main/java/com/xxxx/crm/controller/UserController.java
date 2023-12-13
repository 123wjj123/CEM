package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.util.Password;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;

    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){
            ResultInfo resultInfo=new ResultInfo();
        //调用service层登录方法
        UserModel userModel=userService.userLogin(userName,userPwd);
        //设置ResultInfo的result的值（将数据返回给请求)
        resultInfo.setResult(userModel);
        //通过try catch捕获service层异常，如果service层抛出异常，否则表示登录失败
       /* try {
            //调用service层登录方法
            UserModel userModel=userService.userLogin(userName,userPwd);
            //设置ResultInfo的result的值（将数据返回给请求)
            resultInfo.setResult(userModel);

        }catch (ParamsException p){
            resultInfo.setCode(p.getCode());
            resultInfo.setMsg(p.getMsg());
            p.printStackTrace();
        }catch (Exception e){
            resultInfo.setCode(500);
            resultInfo.setMsg("登录失败！");
        }*/
        return resultInfo;
    }

    /**
     * 用户修改密码
     * @param request
     * @param oldPassword
     * @param newPassword
     * @param repeatPassword
     * @return
     */
    @PostMapping ("updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,String oldPassword,String newPassword,String repeatPassword){
        ResultInfo resultInfo=new ResultInfo();
        //获取cookie中的userId
        Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
        //调用Service层修改密码方法

//        try {
//            //获取cookie中的userId
//            Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
//            //调用Service层修改密码方法
//            userService.updatePassWord(userId,oldPassword,newPassword,repeatPassword);
//        }catch (ParamsException p){
//            resultInfo.setCode(p.getCode());
//            resultInfo.setMsg(p.getMsg());
//            p.printStackTrace();
//        }catch (Exception e){
//            resultInfo.setCode(500);
//            resultInfo.setMsg("修改密码失败");
//            e.printStackTrace();
//        }
        return  resultInfo;
    }
    /**
     * 进入修改的页面
     */
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>>queryAllSales(){
        return userService.queryAllSales();
    }

    /**
     * 分页多条件查询用户列表
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object>selectByParams(UserQuery userQuery){
        return userService.queryByParamsForTable(userQuery);
    }
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    @RequestMapping ("add")
    @ResponseBody
    public ResultInfo addUser(User user){
        userService.addUser(user);
        return success("用户添加成功！");
    }
    /**
     * 用户更新
     */
    @RequestMapping("update")
    @ResponseBody
    public  ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户记录更新成功");
    }
    /**
     *打开添加或修改用户的页面
     */
    @RequestMapping("addOrUpdateUserPage")
    public String addOrUpdateUserPage(Integer id, Model model){
        //判断id是否为空不为空查询用户
        if(id!=null) {
            model.addAttribute("userInfo", userService.selectByPrimaryKey(id));
        }
        return "user/add_update";
    }
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteUserByIds(ids);
        return success("用户记录删除成功");
    }
}
