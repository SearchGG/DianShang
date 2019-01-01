//服务层
app.service('seckillService',function($http){
	    	
	//查询秒杀商品列表
	this.findSeckillGoodsList=function(){
		return $http.get('seckill/findSeckillGoodsList.do');
	}

    //查询秒杀商品详情
    this.findBySeckillGoodsId=function(seckillGoodsId){
        return $http.get('seckill/findBySeckillGoodsId.do?seckillGoodsId='+seckillGoodsId);
    }

    //秒杀下单
	this.saveSeckillOrder=function (seckillGoodsId) {
        return $http.get('seckill/saveSeckillOrder.do?seckillGoodsId='+seckillGoodsId);
    }
});
