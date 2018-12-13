 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,itemCatService,typeTemplateService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			//获取商品介绍中的html代码
            $scope.entity.goodsDesc.introduction= editor.html();
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//跳转商品管理

					//清除输入框
					$scope.entity={};
                    editor.html(""); //清空输入框
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //查询一级分类的列表功能
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(function (response) {
			$scope.itemCat1List=response;

        })
    }
    //基于一级分类变化查询二级
	//参数一 监控发生变化的模型变量 参数二 发生变化后 执行的参数 newValue 监控模板变量变化前的值 oldValue 监控模板变量变化后的值
 	$scope.$watch("entity.goods.category1Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List=response;
            $scope.itemCat3List={};


        })
    })
    //基于二级分类变化查询三级
    //参数一 监控发生变化的模型变量 参数二 发生变化后 执行的参数 newValue 监控模板变量变化前的值 oldValue 监控模板变量变化后的值
    $scope.$watch("entity.goods.category2Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List=response;
        })
    })
    //基于三级分类变化查询模板id
    //参数一 监控发生变化的模型变量 参数二 发生变化后 执行的参数 newValue 监控模板变量变化前的值 oldValue 监控模板变量变化后的值
    $scope.$watch("entity.goods.category3Id",function (newValue,oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId=response.typeId;
        })
    })
	//基于模板id变化查询品牌
    //参数一 监控发生变化的模型变量 参数二 发生变化后 执行的参数 newValue 监控模板变量变化前的值 oldValue 监控模板变量变化后的值
    $scope.$watch("entity.goods.typeTemplateId",function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
        	$scope.brandList=JSON.parse(response.brandIds);
        	//扩展属性
        	$scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems)
        });
        //查询模板关联的规格列表数据
        typeTemplateService.findSpecList(newValue).success(function (response) {
			$scope.specList=response;
            $scope.entity.itemList=[];
        })
    });
	//商品图片上传
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(function (response) {
			if (response.success){
				//上传图片 显示
			$scope.imageEntity.url=response.message;
			}else{
				alert(response.message);
			}
        })
    }
    //初始化
	$scope.entity={goods:{isEnableSpec:"1"},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]};

	//商品上传到列表中
	$scope.addImageEntity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
    }
	//从商品列表中移除商品图片
	$scope.deleImageEntity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }



    //规格选项勾选与取消
    $scope.updateSpecAttribute=function ($event,specName,specOption) {

	  var specObject =  $scope.getObjectByValue($scope.entity.goodsDesc.specificationItems,"attributeName",specName);
	  if(specObject!=null){//存在规格数据
            if ($event.target.checked){//勾选
                specObject.attributeValue.push(specOption);
            }else{//取消
               var index = specObject.attributeValue.indexOf(specOption);
                specObject.attributeValue.splice(index,1);

                //判断如果改规格全部取消
                if (specObject.attributeValue.length==0){
                    //从规格列表中移除改规格
                    var index1=$scope.entity.goodsDesc.specificationItems.indexOf(specObject);
                    $scope.entity.goodsDesc.specificationItems.splice(index1,1);
                }
            }
        }else{//不存在
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[specOption]});

	    }
    }
    //创建SKU列表
    $scope.createItemList=function () {
	    //初始化sku列表
        $scope.entity.itemList=[{spec:{},price:0,num:999,status:'1',isDefault:'0'}];

        var items=$scope.entity.goodsDesc.specificationItems;
       //全部取消
        if (items.length==0){
           $scope.entity.itemList=[];
       }

        for (var i=0;i<items.length;i++){
            $scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );
        }
    }
    //添加列值
    addColumn=function(list,columnName,conlumnValues){
        var newList=[];//新的集合
        for(var i=0;i<list.length;i++){
            var oldRow= list[i];
            for(var j=0;j<conlumnValues.length;j++){
                var newRow= JSON.parse( JSON.stringify( oldRow ) );//深克隆
                newRow.spec[columnName]=conlumnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

    //定义商品状态数组
    $scope.status=['未审核','已审核','审核未通过','关闭'];
});	
