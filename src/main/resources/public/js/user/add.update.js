layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    var  formSelects = layui.formSelects;



    var userId=$("input[name='id']").val();
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx+"/role/queryAllRoles?userId="+userId,
        //自定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //自定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);


    form.on('submit(addOrUpdateUser)',function (data) {
        var index= top.layer.msg("数据提交中,请稍后...",{icon:16,time:false,shade:0.8});
        var url = ctx+"/user/add";
        if($("input[name='id']").val()){
            url=ctx+"/user/update";
        }
        $.post(url,data.field,function (res) {
            if(res.code==200){
                top.layer.msg("操作成功");
                top.layer.close(index);
                layer.closeAll("iframe");
                // 刷新父页面
                parent.location.reload();
            }else{
                layer.msg(res.msg);
            }
        });
        return false;
    });
    /*关闭弹出窗*/
    $("#closeBtn").click(function (){
        var index=parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });
    /**
     * 配置远程搜索，请求头，请求参数，请求类型等
     * formSelect.config(ID,Options,isJson);
     */
    var userId=$("input[name='id']").val();
    formSelects.config("selectId",{
        type:"post",//请求方式
        searchUrl: ctx+"/role/queryAllRoles?userId="+userId,//请求地址
        keyName: 'roleName',//下拉框的文本内容，要与返回的数据中对应的key一致
        keyVal: 'id'
    },true);
});