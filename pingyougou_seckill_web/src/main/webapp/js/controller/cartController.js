 //控制层 
app.controller('cartController' ,function($scope,$controller   ,cartService){
	
	$controller('baseController',{$scope:$scope});//继承

    //读取购物车列表数据
	$scope.findCartList=function(){
        cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
				sum();//统计商品数量和总金额
			}			
		);
	}    
	//添加商品到购物车
	$scope.addItemToCartList=function (itemId,num) {
        cartService.addItemToCartList(itemId,num).success(function (response) {
			if(response.success){//添加购物车成功
                //读取购物车列表数据
                $scope.findCartList();
			}else {//添加购物车失败
				alert(response.message);
			}
        })
    }
    
    //统计商品数量和总金额功能
	sum=function () {
		//总数量和总金额
		$scope.totalNum=0;
		$scope.totalMoney=0.00;
		//遍历购物车列表
		for(var i=0;i<$scope.cartList.length;i++){
			//获取购物车对象
			var cart=$scope.cartList[i];
            var orderItemList =cart.orderItemList;//获取购物车商品明细列表
			//遍历购物车商品明细列表
			for(var j=0;j<orderItemList.length;j++){
                $scope.totalNum+=orderItemList[j].num;
                $scope.totalMoney+=orderItemList[j].totalFee;
			}
		}


    }
	
	

});	
