layui.use('form', function() {
	let form = layui.form;

	//通用弹出层表单提交方法
	form.on('submit(demo1)', function(data){
		// console.log(data.field);

		// 对手机号码、邮箱进行校验
		if(!checkPhone() || !checkEmail()){
			return false;
		}


		$.post($('form').attr("action"),data.field, function (e){
			// var data = JSON.parse(e);
			if (e.result == true) {
				location.reload();
				parent.closeLayer(e.msg);
				layer.msg('操作成功：' + e.msg, {icon: 1, time: 2000});
			}else {
				layer.msg('操作失败：' + e.msg, {icon: 2, time: 2000});
			}
		});
		return false;
	});
	
});