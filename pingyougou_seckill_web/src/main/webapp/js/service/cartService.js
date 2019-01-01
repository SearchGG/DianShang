//服务层
app.service('cartService',function($http){
	    	
	//读取购物车列表数据
	this.findCartList=function(){
		return $http.get('cart/findCartList.do');
	}

	//添加商品到购物车
	this.addItemToCartList=function (itemId,num) {
        return $http.get('cart/addItemToCartList.do?itemId='+itemId+"&num="+num);
    }
});
