 //控制层 
app.controller('indexController' ,function($scope,$controller   ,contentService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    $scope.findByCategoryId=function(catrgoryId) {
		contentService.findByCategoryId(catrgoryId).success(function (response) {
			$scope.contentList=response;
        })
    }
    //搜素方法
    $scope.search=function () {
        location.href="http://search.pingyougou.com/search.html#?keywords="+$scope.keywords;
    }
    
    
});	
