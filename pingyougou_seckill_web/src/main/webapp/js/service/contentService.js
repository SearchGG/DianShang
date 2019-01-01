//服务层
app.service('contentService',function($http){
	    	
	//根据广告分类id查询广告列表数据
	this.findByCategoryId=function (categoryId) {
        return $http.get('content/findByCategoryId.do?categoryId='+categoryId);
    }
});
