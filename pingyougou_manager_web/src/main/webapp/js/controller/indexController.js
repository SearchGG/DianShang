app.controller('indexController', function ($scope,$controller,loginService ) {
    //控制器继承代码
    $controller("baseController",{$scope:$scope});

    $scope.getName=function () {
        loginService.getName().success(function (response) {

            $scope.loginName=response.loginName;
        })
    };

});
