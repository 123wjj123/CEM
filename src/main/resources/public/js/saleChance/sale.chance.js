layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //用户列表展示
    var  tableIns = table.render({
        id:'saleChanceTable',
        elem: '#saleChanceList',
        url : ctx+'/sale_chance/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,20,30,40,50],
        limit : 10,
        toolbar: "#toolbarDemo",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',sort:true,fixed:"true"},
            {field: 'chanceSource', title: '机会来源',align:"center"},
            {field: 'customerName', title: '客户名称',  align:'center'},
            {field: 'cgjl', title: '成功几率', align:'center'},
            {field: 'overview', title: '概要', align:'center'},
            {field: 'linkMan', title: '联系人',  align:'center'},
            {field: 'linkPhone', title: '联系电话', align:'center'},
            {field: 'description', title: '描述', align:'center'},
            {field: 'createMan', title: '创建人', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'updateDate', title: '修改时间', align:'center'},
            {field: 'uname', title: '指派人', align:'center'},
            {field: 'assignTime', title: '分配时间', align:'center'},
            {field: 'state', title: '分配状态', align:'center',templet:function(d){
                    return formatterState(d.state);
                }},
            {field: 'devResult', title: '开发状态', align:'center',templet:function (d) {
                    return formatterDevResult(d.devResult);
                }},
            {title: '操作', templet:'#saleChanceListBar',fixed:"right",align:"center", minWidth:150}
        ]]
    });

    function formatterState(state){
        if(state==0){
            return "<div style='color:yellow '>未分配</div>";
        }else if(state==1){
            return "<div style='color: green'>已分配</div>";
        }else{
            return "<div style='color: red'>未知</div>";
        }
    }

    function formatterDevResult(value){
        /**
         * 0-未开发
         * 1-开发中
         * 2-开发成功
         * 3-开发失败
         */
        if(value==0){
            return "<div style='color: yellow'>未开发</div>";
        }else if(value==1){
            return "<div style='color: #00FF00;'>开发中</div>";
        }else if(value==2){
            return "<div style='color: #00B83F'>开发成功</div>";
        }else if(value==3){
            return "<div style='color: red'>开发失败</div>";
        }else {
            return "<div style='color: #af0000'>未知</div>"
        }
    }

    // 多条件搜索
    $(".search_btn").click(function(){
        tableIns.reload({
            where: {
                customerName: $("[name='customerName']").val(),  //客户名
                createMan: $("[name='createMan']").val(),  //创建人
                state: $("#state").val()  //状态
            } ,page: {
                curr: 1 //重新从第 1 页开始
            },
        })
    });

 /*  监听头工具栏事件*/
    table.on('toolbar(saleChances)', function(data){
       console.log(data);
       //判断对应的事件类型
        if (data.event=="add"){
            //添加操作
            openSaleChanceDialog();
        }else if (data.event=="del"){
            //删除操作
            deleteSaleChance(data);
        }
    });
    // 打开添加机会数据页面
    function openSaleChanceDialog(saleChanceId){
        var url  =  ctx+"/sale_chance/toSaleChancePage";
        var title="营销机会管理-添加营销机会";
        //判断营销机会ID是否为空
        if(saleChanceId !=null && saleChanceId != ''){
            title="营销机会管理-机会更新";
            //请求地址传递营销机会的ID
            url += '?saleChanceId=' + saleChanceId;
        }
        layui.layer.open({
            title : title,
            type : 2,
            area:["700px","560px"],
            maxmin:true,
            content : url
        });
    }

    /**
     * 删除营销机会
     */
    function deleteSaleChance(data) {
        //获取数据表格选中的行数据
        var checkStatus=table.checkStatus("saleChanceTable");
        //获取所有被选中的记录对应的数据
        var saleChanceData=checkStatus.data;
        //判断用户是否选择的记录（选中行的数量大于0)
        if (saleChanceData.length<1){
            layer.msg("请选择要删除的记录!");
            return;
        }
        //询问用户是否确认删除
        layer.confirm('您确定要删除选中的记录吗？',{icon:3,title:"营销机会管理"},function (index){
            //关闭确认框
            layer.close(index);
            //传递的参数是数组
            var ids="ids=";
               for(var i=0;i<saleChanceData.length;i++){
                   if (i<saleChanceData.length-1){
                       ids=ids + saleChanceData[i].id+"&ids="
                   }else {
                       ids=ids + saleChanceData[i].id;
                   }
            }
               // console.log(ids);
            //发送ajax请求，执行删除营销机会
            $.ajax({
                type:"post",
                url:ctx+"/sale_chance/delete",
                data:ids,
                // dataType: "json",
                success:function (result){
                    if (result.code==200){
                        //提示成功
                        layer.msg("删除成功")
                        //刷新表格
                        tableIns.reload();
                    }else {
                        //提示失败
                        layer.msg(result.msg);
                    }
                }
            });
        });
    }
    /**
     * 行监听
      */
    table.on('tool(saleChances)', function(data){
        console.log(data);
        if (data.event=="edit") {
            //添加操作
            var saleChanceId=data.data.id;
            openSaleChanceDialog(saleChanceId);
        }else if (data.event=="del"){
            //删除操作
            layer.confirm("确认要删除该记录吗?",{icon:3,title:"营销机会管理"},function (index) {
                    //关闭确认框
                layer.close(index);
                //发送对应的ajax请求，删除记录
                $.ajax({
                    type:"post",
                    url:ctx+"/sale_chance/delete",
                    data:{
                        ids:data.data.id
                    },
                    success:function (result){
                        //判断删除结果
                        if (result.code==200){
                            //提示成功
                            layer.msg("删除成功")
                            //刷新表格
                            tableIns.reload();
                        }else {
                            //提示失败
                            layer.msg(result.msg);
                        }
                    }
                })
            })
            }

    });


    /**
     * 批量删除
     * @param datas
     */
    function delSaleChance(datas) {
        if(datas.length==0){
            layer.msg("请选择删除记录!", {icon: 5});
            return;
        }
        layer.confirm('确定删除选中的机会数据？', {
            btn: ['确定','取消'] //按钮
        }, function(index){
            layer.close(index);
            var ids= "ids=";
            for(var i=0;i<datas.length;i++){
                if(i<datas.length-1){
                    ids=ids+datas[i].id+"&ids=";
                }else {
                    ids=ids+datas[i].id
                }
            }
            $.ajax({
                type:"post",
                url:ctx+"/sale_chance/delete",
                data:ids,
                dataType:"json",
                success:function (data) {
                    if(data.code==200){
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg, {icon: 5});
                    }
                }
            })
        });
    }




});
