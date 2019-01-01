app.controller("indexController",function ($scope,$controller,loginService) {
//控制器继承代码  参数一：继承的父控制器名称  参数二：固定写法，共享$scope对象
    $controller("baseController",{$scope:$scope});

    //获取用户名
    $scope.getName=function () {
        //response接收响应结果
        loginService.getName().success(function (response) {
            $scope.loginName=response.loginName;
            //浏览器端输出打印数据到控制台操作
            console.log(loginName);
        })
    }
})