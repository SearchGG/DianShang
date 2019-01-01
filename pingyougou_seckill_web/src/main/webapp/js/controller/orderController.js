 //控制层 
app.controller('orderController' ,function($scope,$controller   ,cartService,addressService,orderService){
	
	$controller('baseController',{$scope:$scope});//继承

	//展示收件人地址列表
	$scope.findAddressListByUserId=function () {
        addressService.findAddressListByUserId().success(function (response) {
        	//收件人地址列表
            $scope.addressList=response;

            for(var i=0;i< $scope.addressList;i++){
            	if($scope.addressList[i].isDefault=='1'){//默认收件人地址
                    $scope.address=$scope.addressList[i];
                    break;
				}
			}

			//如果没有设置默认收件人地址，取第一个地址为默认地址
			if($scope.address==null){
                $scope.address=$scope.addressList[0];
			}

        })
    }

    //定义寄送至的收件人地址对象
	$scope.address=null;

	//勾选默认收件人地址
	$scope.isSelected=function (addr) {
		if($scope.address==addr){
			return true;
		}else {
			return false;
		}
    }

    //切换收件人地址信息
	$scope.updateSelected=function (addr) {
        $scope.address=addr;
    }

    //声明订单实体对象  支付方式默认为：在线支付
	$scope.entity={paymentType:"1"};

    //切换支付方式功能
	$scope.updatePaymentType=function (type) {
        $scope.entity.paymentType=type;
    }


    //读取购物车列表数据
	$scope.findCartList=function(){
        cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
				sum();//统计商品数量和总金额
			}			
		);
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
    
    //保存订单
	$scope.save=function () {
        $scope.entity.receiver=$scope.address.contact;//联系人
        $scope.entity.receiverAreaName=$scope.address.address;//联系人地址
        $scope.entity.receiverMobile=$scope.address.mobile;//联系电话

		orderService.add($scope.entity).success(function (response) {
			if(response.success){
				//跳转支付页面
				location.href="pay.html";
			}else {
				alert(response.message);
			}
        })
    }
	
});
