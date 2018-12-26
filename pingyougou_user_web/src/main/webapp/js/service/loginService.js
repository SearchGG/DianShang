app.service("loginService",function ($http) {
    //获取用户名
    this.getName=function () {
        return $http.get("/login/getName.do");
    }
})