 //控制层 
app.controller('seckillController' ,function($scope,$controller,$location,$interval,seckillService){
	
	$controller('baseController',{$scope:$scope});//继承

    //查询秒杀商品列表
    $scope.findSeckillGoodsList=function () {
        seckillService.findSeckillGoodsList().success(function (response) {
            $scope.seckillGoodsList=response;
        })
    }

    //查询秒杀商品详情
    $scope.findBySeckillGoodsId=function () {
        //获取秒杀商品id
        $scope.seckillGoodsId =$location.search()["seckillGoodsId"];
        seckillService.findBySeckillGoodsId( $scope.seckillGoodsId).success(function (response) {
            $scope.seckillGoods=response;

            //计算出剩余时间
            var endTime = new Date($scope.seckillGoods.endTime).getTime();
            var nowTime = new Date().getTime();

            //剩余时间
            $scope.secondes =Math.floor( (endTime-nowTime)/1000 );

            var time =$interval(function () {
                if($scope.secondes>0){
                    //时间递减
                    $scope.secondes--;
                    //时间格式化
                    $scope.timeString=$scope.convertTimeString($scope.secondes);
                }else{
                    //结束时间递减
                    $interval.cancel(time);
                }
            },1000);
        })
    }

    //时间格式化
    $scope.convertTimeString=function (allseconds) {
        //计算天数
        var days = Math.floor(allseconds/(60*60*24));

        //小时
        var hours =Math.floor( (allseconds-(days*60*60*24))/(60*60) );

        //分钟
        var minutes = Math.floor( (allseconds-(days*60*60*24)-(hours*60*60))/60 );

        //秒
        var seconds = allseconds-(days*60*60*24)-(hours*60*60)-(minutes*60);

        //拼接时间
        var timString="";
        if(days>0){
            timString=days+"天:";
        }

        if(hours<10){
            hours="0"+hours;
        }
        if(minutes<10){
            minutes="0"+minutes;
        }
        if(seconds<10){
            seconds="0"+seconds;
        }
        return timString+=hours+":"+minutes+":"+seconds;
    }


        /*  //$interval angularjs提供的定时处理服务对象
          $scope.count=10;
          //参数一：定时器每隔多长时间做的事情  参数二：定时器时间设定 单位是毫秒值  参数三：设定执行次数
          $interval(function () {
              $scope.count--;
          }, 1000);*/

   //秒杀下单
    $scope.saveSeckillOrder=function () {
        seckillService.saveSeckillOrder($scope.seckillGoodsId).success(function (response) {
            alert(response.message);
        })
    }
   
   
    
});
