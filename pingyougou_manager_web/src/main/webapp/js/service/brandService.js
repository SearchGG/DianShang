//定义服务层
app.service('brandService',function ($http) {
    //分页查询
    this.findPage=function (page,rows) {
        return  $http.get('../brand/findPage.do?page=' + page + '&rows=' + rows);
    }
    //新增品牌
    this.add=function (entity) {
        return $http.post('../brand/add.do', entity)
    }
    //修改品牌
    this.update=function (entity) {
        return $http.post('../brand/update.do', entity)
    }
    //根据id查询
    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id=' + id)
    }
    //批量删除
    this.dele=function (ids) {
        return $http.get('../brand/delete.do?ids='+ids)
    }
    //条件分页查询
    this.search=function (page,rows,searchEntity) {
        return  $http.post('../brand/search.do?page=' + page + '&rows=' + rows,searchEntity);
    }
});
