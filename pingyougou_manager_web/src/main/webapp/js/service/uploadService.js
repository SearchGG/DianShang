app.service("uploadService",function ($http) {
    //文件上传
    this.uploadFile=function () {
        //基于AngularJs 需要结合html5的FormData对象 完成上传
        var formData=new FormData();
        //获取页面上传表单项中的文件对象 并提交给后端
        //1.提交给后端controller谁接收页面选中文件对象
        //2.获取页面选择的对象 file指页面id值
        formData.append("file",file.files[0]);
        //发起请求
       return $http({
           method:"post",
           url:"../upload/uploadFile.do",
           data:formData,
           headers: {'Content-Type':undefined},
           transformRequest: angular.identity
       });
    }

});