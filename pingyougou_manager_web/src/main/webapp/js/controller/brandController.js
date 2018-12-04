app.controller('brandController', function ($scope,$controller,brandService ) {
    //控制器继承代码
    $controller("baseController",{$scope:$scope});

    /* //读取列表数据绑定到表单
     $scope.findAll=function(){
         $http.get('../brand/findAll.do').success(
             function (response) {
                 $scope.list=response;
             }
         );
     }*/

    //分页
    $scope.findPage = function (page, rows) {
        brandService.findPage(page,rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
    //条件分页
    $scope.searchEntity={};

    $scope.search= function (page, rows) {
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
    //添加
    $scope.save = function () {

        var method =null;
        if ($scope.entity.id != null) {
            method =brandService.update($scope.entity);
        }else{
            method =brandService.add($scope.entity);
        }

        method.success(function (response) {
            if (response.success) {
                //重新查询
                $scope.reloadList();//重新加载
            } else {
                alert(response.message);
            }
        });
    }
    //根据id查询实体
    $, $scope.findOne = function (id) {
        brandService.findOne(id).success(function (repsonse) {
            $scope.entity = repsonse;
        });
    }

    //删除操作
    $scope.dele=function () {
        if (confirm("确定要删除吗？")){
            brandService.dele($scope.selectIds).success(function(response){
                if(response.success){
                    $scope.reloadList();//刷新列表
                }
            });
        }

    }
    //功能

});
