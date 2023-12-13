layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //计划项数据展示
    var  tableIns = table.render({
        id : "cusDevPlanTable",
        elem: '#cusDevPlanList',
        url : ctx+'/cus_dev_plan/list?saleChanceId='+$("[name='id']").val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',sort:true, fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执行效果',align:"center"},
            {field: 'planDate', title: '执行时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#cusDevPlanListBar"}
        ]]
    });

    /**
     * 监听头部工具栏
     */
    table.on("toolbar(cusDevPlans)",function (data) {
       if (data.event=="add") {
           openAddOrUpdateCusDevPlanDialog();
       }else if (data.event=="success"){
           //更新营销机会的开发状态
           updateSaleChanceDevResult(2); //开发成功

        }else if (data.event=="failed"){
            //更新营销机会的开发状态
           updateSaleChanceDevResult(3)//开发失败
       }
    });

    /**
     * 监听行工具栏
     */
    table.on("tool(cusDevPlans)",function (data) {
        var layEvent = data.event;
        if(layEvent === "edit"){
            openAddOrUpdateCusDevPlanDialog(data.data.id);
        }else if(layEvent === "del"){
            layer.confirm("确认删除当前记录?",{icon: 3, title: "客户开发计划管理"},function (index) {
                $.post(ctx+"/cus_dev_plan/delete",{id:data.data.id},function (data) {
                    if(data.code==200){
                        layer.msg("删除成功");
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg);
                    }
                })
            })
        }
    });



    function openAddOrUpdateCusDevPlanDialog(id) {
        var title="计划项管理管理-添加计划项";
        var url=ctx+"/cus_dev_plan/toAddOrUpdateCusDevPlanPage?sId="+$("[name='id']").val();
        if(id !=null && id !=''){
            title="计划项管理管理-更新计划项";
            url=url+"&id="+id;
        }
        layui.layer.open({
            title:title,
            type:2,
            area:["500px","300px"],
            maxmin:true,
            content:url
        })
    }


    /**
     * 更新营销机会的开发状态
     * @param sid
     * @param devResult
     */
    function updateSaleChanceDevResult(devResult) {
        //弹出确认框，询问用户确认删除
        layer.confirm("确认更新机会数据状态?",{icon: 3, title: "客户机会管理"},function (index) {
            //得到需要被更新的营销机会的ID
            var sId=$("[name='id']").val();

            $.post(ctx+"/sale_chance/updateSaleChanceDevResult",{
                id:sId,
                devResult:devResult
            },function (result) {
                if(result.code==200){
                    layer.msg("机会数据更新成功");
                    //关闭窗口
                    layer.closeAll("iframe");
                    // 刷新父页面
                    parent.location.reload();
                }else{
                    layer.msg(result.msg);
                }
            })
        })
    }

    /**
     * 删除计划项
     */
function deleteCusDevPlan(id){
    layer.confirm("您确认要删除该记录吗?",{icon:3,title:'开发数据管理'},function (index){
        //发送ajax请求，执行删除操作
        $.post(ctx+'/cus_dev_plan/delete',{id:id},function (result){
            //删除结果
            if (result.code==200){
                // 提示成功
                layer.msg('删除成功',{icon:6});
                //刷新数据表格
                tableIns.reload();
            }else {
                //提示失败原因
                layer.msg(result.msg,{icon:5});
            }
        })
    });
    }



});
