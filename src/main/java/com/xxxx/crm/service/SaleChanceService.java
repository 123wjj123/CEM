package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;
    /**
     * 多条件分页查询营销机会 (返回的数据格式必须满足LayUi中数据表格的格式
     */
    public Map<String,Object>querySaleChanceByParams(SaleChanceQuery saleChanceQuery){
        Map<String,Object>map=new HashMap<>();
        //开启分页
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        //得到对应分页对象
        PageInfo<SaleChance>pageInfo=new PageInfo<>(saleChanceMapper.selectByParams(saleChanceQuery));
        //设置map对象
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        //设置分页好的列表
        map.put("data",pageInfo.getList());

        return map;
    }

    /**
     *  添加营销机会
     *  1.参数效验
     *      customerName客户名称 非空
     *      linkMan联系人 非空
     *      LikePhone联系号码 非空，手机号码格式正确
     *   2.设置相关参数的默认值
     *      createMan创建人 当前用户名
     *      assignMan指派人
     *          如果未设置指派人 默认
     *             state分配状态 0未分配 1=已分配
     *             assignTime指派时间
     *                 设置为null
     *              系统当前时间
     *              devresult开发状态（0=未开放，1=开发中，2=开发成功，3=开发失败）
     *      isValid是否有效（0=无效 1=有效)
     *          设置为有效 1=有效
     *      createDate创建时间
     *      updateDate
     *          默认是系统当前时间
     *  3.执行添加操作，判断受影响的行数
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance){
    //1.效验参数
        checkSaleChanceParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
    //设置相关字段默认值
    //设置为有效 1=有效
        saleChance.setIsValid(1);
    //创建时间 CreateDate
        saleChance.setCreateDate(new Date());
    //UpdateDate默认是系统当前时间
        saleChance.setUpdateDate(new Date());
    //判断是否设置了指派人
        if (StringUtils.isBlank(saleChance.getAssignMan())){
            //如果为空则表示未设置指派人
            saleChance.setState(StateStatus.UNSTATE.getType());
            //assignTime指派时间
            saleChance.setAssignTime(null);
            //devResult开发状态
            saleChance.setDevResult(DevResult.UNDEV.getStatus());

        }else {
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setAssignTime(new Date());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }
        //3.执行添加操作，判断受影响的行数
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance)!=1,"添加营销机会失败！");

    }

    /**
     * 更新营销机会
     * 1.参数校验
     * @param saleChance
     *  customerName客户名称 非空
     *      linkMan联系人 非空
     *      LikePhone联系号码 非空，手机号码格式正确
     2.设置相关参数的默认值
        updateDate更新时间 设置为系统当前时间
     *   createMan创建人 当前用户名
     *   assignMan指派人
     *      原始数据未设置
     *          修改后未设置
     *              不需要操作
     *          修改后已设置
     *          assignTime指派时间 设置为系统当前时间
     *          分配状态 1=已分配
     *          开发状态 1=开发中
     *          原始数据已设置
     *            修改后未设置
     *      *          assignTime指派时间 设置为null
     *      *          分配状态 0=已分配
     *      *          开发状态 1=开发中
     *             修改后已设置
     *              判断修改前后是否同一个指派人
     *                  如果是，则不需要操作
     *                  如果不是，则需要更新操作
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance){
        //        1.参数效验
        //营销机会ID 非空，数据库中对应的记录存在
        AssertUtil.isTrue(null==saleChance.getId(),"待更新记录不存在");
        //通过主键查询对象
        SaleChance temp=saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        //判断数据库中对应的记录存在
        AssertUtil.isTrue(temp==null,"待更新记录不存在");
        //参数校验
        checkSaleChanceParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());

        //2.设置相关参数的默认值
        //updateDate更新时间 设置为系统当前时间
        saleChance.setUpdateDate(new Date());
        //assignMan指派人
        //判断原始数据是否存在
        if (StringUtils.isBlank(temp.getAssignMan())){//不存在
            //判断修改后的值是否存在
            if (!StringUtils.isBlank(saleChance.getAssignMan())){ //修改前为空，修改后有值
               // assignTime指派时间 设置为null
                saleChance.setAssignTime(new Date());
                //分配状态 0=已分配
                saleChance.setState(StateStatus.STATED.getType());
               // 开发状态 1=开发中
                saleChance.setDevResult(DevResult.DEVING.getStatus());

            }
        }else { //存在
            //判断修改后是否存在
            if (StringUtils.isBlank(saleChance.getAssignMan())){ //修改前有值，修改后无值
                saleChance.setAssignTime(new Date());
                saleChance.setState(StateStatus.STATED.getType());
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }else { //修改前有值，修改后有值
                //判断修改前后是否是同一个用户对象
                if (!saleChance.getAssignMan().equals(temp.getAssignMan())){
                    //更新指派时间
                    saleChance.setAssignTime(new Date());
                }else {
                    saleChance.setAssignTime(temp.getAssignTime());
                }
            }
        }
//        执行更新操作，判断受影响的行数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)!=1,"更新营销机会失败");
    }

    /**
     * 1.参数效验
     * customerName客户名称 非空
     * LinkMan联系人 非空
     * LikePhone联系号码 非空，手机号码格式正确
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkSaleChanceParams(String customerName, String linkMan, String linkPhone) {
        //customerName客户名称 非空
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名称不能为空!");
        //LinkMan联系人 非空
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空!");
        //LikePhone联系号码 非空，手机号码格式正确
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"联系号码不能为空！");

        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"联系号码格式不正确");
    }

    /**
     * 删除营销机会
     * @param ids
     */
    @Transactional(propagation=Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //判断ID是否为空
        AssertUtil.isTrue(null==ids || ids.length<1,"待删除记录不存在");
        //执行删除(更新)操作，判断影响的行数
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)!= ids.length,"营销机会数据删除失败");
    }

    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     */
    @Transactional(propagation=Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        //判断Id是否为空
        AssertUtil.isTrue(null==id,"待更新记录不存在！");
        //通过id查询机会数据
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        //判断对象是否为空
        AssertUtil.isTrue(null==saleChance,"带更新记录不存在!");
        //设置开发状态
        saleChance.setDevResult(devResult);
        //执行更新操作，判断受影响的函数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)!=1,"开发状态更新失败!");
    }


}
