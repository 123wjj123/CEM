layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    form.on('submit(addOrUpdateSaleChance)',function (data) {
        var index= layer.msg("数据提交中,请稍后...",{
            icon:16,time:false,shade:0.8});
        var url = ctx+"/sale_chance/add";

        //通过营销机会ID来判断当前需要执行添加操作还是修改操操作
        //如果营销机会的ID为空，则表示执行添加操作;如果ID不为空,则表示执行更新操作
        //通过获取隐藏域中的Id
        var saleChanceId=$("[name='id']").val();
        //判断ID是否为空
        if (saleChanceId != null && saleChanceId !=''){
            //更新操作
            url=ctx + "/sale_chance/update";
        }
        $.post(url,data.field,function (result) {
            if(result.code==200){
                layer.msg("操作成功");
                layer.close(index);
                layer.closeAll("iframe");
                // 刷新父页面
                parent.location.reload();
            }else{
                layer.msg(result.msg);
            }
        });
        return false;
    });


    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function (){
        var index=parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });

    $.ajax({
        type:"get",
        url:ctx+"/user/queryAllSales",
        data:{},
        success: function (data) {
            if (data != null) {
                var assignManId=$("#assignManId").val();
                //遍历返回的数据
                for (var i = 0; i < data.length; i++) {
                    var opt="";
                    //设置下拉选项
                    if (assignManId==data[i].id){
                        opt = "<option value='"+data[i].id+"' selected>"+data[i].uname+"</option>";
                    }else {
                        opt = "<option value='"+data[i].id+"'>"+data[i].uname+"</option>";

                    }
                    //将下拉项设置到下拉框中
                    $("#assignMan").append(opt);
                }
            }
            // 重新渲染下拉框内容
            layui.form.render("select");
        }

    });

});