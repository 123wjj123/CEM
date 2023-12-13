layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
    // 进行登录操作
    form.on('submit(login)', function (data) {
       console.log(data.field);
        // data = data.field;
        // if ( data.username =="undefined" || data.username =="" || data.username.trim()=="") {
        //     layer.msg('用户名不能为空');
        //     return false;
        // }
        // if ( data.password =="undefined" || data.password =="" || data.password.trim()=="")  {
        //     layer.msg('密码不能为空');
        //     return false;
        // }
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data:{
                userName:data.field.username,
                userPwd:data.field.password
            },
            dataType:"json",
            success:function (result) {
                if(result.code==200){
                    layer.msg('登录成功', function () {
                        // var result =data.result;
                        if($("#rememberMe").prop("checked")){
                            // 写入cookie 7天  // 如果点击记住我 设置cookie 过期时间7天
                            $.cookie("userIdStr",result.result.userIdStr, { expires: 7 });
                            $.cookie("userName",result.result.userName, { expires: 7 });
                            $.cookie("trueName",result.result.trueName, { expires: 7 });
                        }else {
                            $.cookie("userIdStr", result.result.userIdStr);
                            $.cookie("userName", result.result.userName);
                            $.cookie("trueName", result.result.trueName);

                        }
                        window.location.href=ctx+"/main";
                    });
                }else{
                    layer.msg(data.msg);
                }
            }
        });
        return false;
    });
});