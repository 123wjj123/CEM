layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    /**
     * 表单Sumit监听
     */
    form.on('submit(addOrUpdateCusDevPlan)',function (data) {
        var index= top.layer.msg("数据提交中,请稍后...",{
            icon:16,
            time:false,
            shade:0.8
        });
        //得到所以的表单元素的值
        var formData=data.field;
        //请求的地址
        var url = ctx+"/cus_dev_plan/add";
        //判断计划项Id是否为空
        if ($('[name="id"]').val()){
            url=ctx+"/cus_dev_plan/update";
        }

        $.post(url,formData,function (result) {
            if(result.code==200){
                top.layer.msg("操作成功");
                top.layer.close(index);
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
});