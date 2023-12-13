package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.ArrayUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    /**
     * Service层 (业务逻辑层：非空判断、条件判断等业务逻辑处理)
     *         1.参数判断，判断用户姓名，用户密码非空等
     *           如果参数为空，抛出异常（异常被控制单元捕获并处理）
     *         2.调用数据访问层，通过用户名查询用户记录，返回用户对象
     *         3.判断用户对象是否为空
     *             如果为空，抛出异常 （异常被控制单元捕获并处理）
     *         4.判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象中的用户密码
     *             如果密码不相等，抛出异常 （异常被控制单元捕获并处理）
     *         5.如果密码正确 登录成功
     */
    public UserModel userLogin(String userName,String userPwd){
       // 1.参数判断，判断用户姓名，用户密码非空等
        checkLoginParams(userName, userPwd);
       //2.调用数据访问层，通过用户名查询用户记录，返回用户对象
        User user= userMapper.queryUserByName(userName);
        //3.判断用户对象是否为空
        AssertUtil.isTrue(user==null,"用户名不存在！");
        //4.判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象中的用户密码
        checkUserPwd(userPwd,user.getUserPwd());
        //返回构建用户对象
        return buildUserInfo(user);
    }
    /**
     * 修改密码
     *   1.接收四个参数（用户id、原始密码、新密码、确认密码)
     *   2.通过request对象，获取设置在cookie中的用户id
     *   3.参数效验
     *    待更新用户记录是否存在(用户对象是否为空)
     *     判断原始密码是否为空
     *     判断原始密码是否正确
     *    判断新密码是否为空
     *    判断新密码是否与原始密码一致
     *     判断确认密码是否为空
     *    判断确认密码是否与新密码一致
     *     4.设置用户的新密码
     *  需要将新密码通过指定算法进行加密（md5加密)
     *    5.执行更新操作，判断受影响的行数
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePassWord(Integer userid,String oldPwd,String newPwd,String repeatPwd){
        //通过用户Id查询用户记录，返回用户对象
        User user=userMapper.selectByPrimaryKey(userid);
        //判断用户记录是否存在
        AssertUtil.isTrue(null==user,"待更新记录不存在");
        //参数效验
        checkPasswordParams(user,oldPwd,newPwd,repeatPwd);
        //设置用户的新密码
        user.setUserPwd(Md5Util.encode(newPwd));
        //执行更新，判断受影响的行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改密码失败！");
    }

    /**
     * 判断原始密码是否为空
     *判断原始密码是否正确
     * 判断新密码是否为空
     *  判断新密码是否与原始密码一致
     *  判断确认密码是否为空
     *  判断确认密码是否与新密码一致
     * @param user
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd
     */
    private void checkPasswordParams(User user, String oldPwd, String newPwd, String repeatPwd) {
        //判断原始密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd),"原始密码不能为空");
        //判断原始密码是否正确
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)),"原始密码不正确");
        //判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPwd),"新密码不能为空!");
        //判断新密码是否与原始密码一致
        AssertUtil.isTrue(oldPwd.equals(newPwd),"新密码不能与原始密码相同");
        //判断确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(repeatPwd),"确认密码不能为空!");
        //判断确实确认密码是否新密码
        AssertUtil.isTrue(!newPwd.equals(repeatPwd),"确认密码与新密码不一致！");

    }


    //构建返回给客户端的用户对象
    private UserModel buildUserInfo(User user) {
        UserModel userModel=new UserModel();
//        userModel.setUserId(user.getId());
        //设置加密的用户id
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    //密码判断
    private void checkUserPwd(String userPwd, String pwd) {
        //将客户端传递的密码加密
        userPwd= Md5Util.encode(userPwd);
        //判断秘密是否相当
        AssertUtil.isTrue(!userPwd.equals(pwd),"用户密码不正确!");
    }

    //参数判断
    private void checkLoginParams(String userName, String userPwd) {
        //验证用户姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户姓名不能为空！");
        //验证用户密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空!");
    }
    /**
     * 查询所有的销售人员
     */
    public List<Map<String,Object>>queryAllSales(){
       return userMapper.queryAllSales();
    }

    /**
     * 1.参数校验
     *     用户名 非空 值唯一
     *     email  非空  格式合法
     *     手机号非空  格式合法
     * 2.默认参数设置
     *     isValid  1
     *     createDate  系统时间
     *     updateDate 系统时间
     *     默认密码设置   123456
     * 3.执行添加
     */
    @Transactional(propagation =Propagation.REQUIRED)
    public void addUser(User user){
        /*参数校验*/
        checkUserParams(user.getUserName(), user.getEmail(),user.getPhone());

        AssertUtil.isTrue(null !=userMapper.queryUserByName(user.getUserName()),"用户名不能重复!");
        /*设置参数的默认值*/
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        /*设置默认密码*/
        user.setUserPwd(Md5Util.encode("123456"));
        /*执行添加操作，判断受影响的行数*/
        AssertUtil.isTrue(userMapper.insertSelective(user)<1,"用户记录添加失败!");
        /*用户角色关联*/
        /**
         * 用户ID
         *  userId
         * 角色ID
         *  roleIds
         */
        relationUserRole(user.getId(), user.getRoleIds());
    }
    /**
     * 用户角色管理
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        /**
         *  用户修改(添加同样适用)时
         *     用户原始的角色记录
         *       存在
         *          *          81    (1,2)-->81  null
         *          *          81   (1,2)  -->81  1,2,3,4
         *          *          81  (1,2)-->81 2
         *       不存在
         *          直接执行批量添加(选择角色记录)
         *   推荐方案--> 首先将用户原始用户角色记录删除(存在情况)  然后加入修改后的用户角色记录(选择角色记录)
         */
        int total = userRoleMapper.countUserRoleByUserId(userId);
        if(total>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=total,"用户角色记录关联失败!");
        }


        if(StringUtils.isNotBlank(roleIds)){
            List<UserRole> userRoles=new ArrayList<>();
            for(String s : roleIds.split(",")){
                UserRole userRole=new UserRole();
                userRole.setCreateDate(new Date());
                userRole.setRoleId(Integer.parseInt(s));
                userRole.setUpdateDate(new Date());
                userRole.setUserId(userId);
                userRoles.add(userRole);
            }
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoles)!=userRoles.size(),"用户角色记录管理失败!");
        }
    }

    @Transactional(propagation =Propagation.REQUIRED)
    public  void updateUser(User user){
        /**
         * 1.参数校验
         *     id 记录存在
         *     用户名 非空 值唯一
         *     email  非空  格式合法
         *     手机号非空  格式合法
         * 2.默认参数设置
         *     updateDate 系统时间
         * 3.执行更新
         */
        User temp =selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(null==temp,"待更新的用户记录不存在!");
        checkFormParams(user.getUserName(),user.getEmail(),user.getPhone());
        temp = userMapper.queryUserByName(user.getUserName());
        AssertUtil.isTrue(null !=temp && !(temp.getId().equals(user.getId())),"该用户已存在!");
        user.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(user)<1,"用户记录更新失败!");
        /*用户角色关联*/
        /**
         * 用户ID
         *  userId
         * 角色ID
         *  roleIds
         */
        relationUserRole(user.getId(), user.getRoleIds());
    }
    private void checkFormParams(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"请输入用户名!");
        AssertUtil.isTrue(StringUtils.isBlank(email),"请输入邮箱!");
        AssertUtil.isTrue(!(PhoneUtil.isMobile(phone)),"手机号格式非法!");
    }

    private void checkUserParams(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"请输入用户名!");
        //判断用户名的唯一性
        //通过用户名查询用户对象
        User temp=userMapper.queryUserByName(userName);
        //如果用户名对象为空,则用户名可用;如果用户对象不为空，则表示用户名不可用
        AssertUtil.isTrue(null!=temp,"用户名已存在，请重新输入！");
        AssertUtil.isTrue(StringUtils.isBlank(email),"请输入邮箱!");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"请输入用户名!");
        AssertUtil.isTrue(!(PhoneUtil.isMobile(phone)),"手机号格式非法!");
    }
    @Transactional(propagation =Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids) {
        AssertUtil.isTrue(null==ids || ids.length==0,"请选择待删除的用户记录!");
        AssertUtil.isTrue(deleteBatch(ids)!=ids.length,"用户记录删除失败!");
        for (Integer userId:ids){
            Integer count = userRoleMapper.countUserRoleByUserId(userId);
            if(count>0){
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户删除失败");
            }
        }
    }
}
