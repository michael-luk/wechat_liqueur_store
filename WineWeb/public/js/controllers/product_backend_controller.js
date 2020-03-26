/**
 * Created by yanglu on 15/11/16.
 */

var app = angular.module('ProductBackendApp', ['ngGrid', 'angularFileUpload', 'fundoo.services']);
var cellEditableTemplateProduct = "<input ng-class=\"'colt' + col.index\" ng-input=\"COL_FIELD\" ng-model=\"COL_FIELD\" ng-blur=\"updateEntity(col, row)\"/>";
var uploadTemplateProduct = '<div> <input type="file" name="files[]" accept="image/*" ng-file-select="uploadImage($files, \'images\', row.entity)"/> <div ng-repeat="imageName in row.entity.imageList"> <a class="fancybox" data-fancybox-group="gallery" fancybox ng-if="isShowImg(imageName)" ng-href="/showImage/{{imageName}}"><img ng-src="/showimg/thumb/{{imageName}}" style="width:50px;height:50px;float:left"></a><input type="button" ng-if="isShowImg(imageName)" ng-click="deleteImage(row.entity, \'images\', imageName)" value="删除" style="float:left" /></div></div>';
var uploadDescription = '<div> <input type="file" name="files[]" accept="image/*" ng-file-select="uploadImage($files, \'description\', row.entity)"/> <div ng-repeat="descriptionName in row.entity.descriptionList"> <a class="fancybox" data-fancybox-group="gallery" fancybox ng-if="isShowImg(descriptionName)" ng-href="/showImage/{{descriptionName}}"><img ng-src="/showimg/thumb/{{descriptionName}}" style="width:50px;height:50px;float:left"></a><input type="button" ng-if="isShowImg(descriptionName)" ng-click="deleteDescription(row.entity, \'description\', descriptionName)" value="删除" style="float:left" /></div></div>';
var uploadProductPreview ='<div> <input type="file" name="files[]" accept="image/*" ng-file-select="uploadImage($files, col.field, row.entity)"/>   <a class="fancybox" data-fancybox-group="gallery" fancybox ng-if="isShowImg(COL_FIELD)" ng-href="/showImage/{{COL_FIELD}}"><img ng-src="/showimg/thumb/{{COL_FIELD}}" style="width:50px;height:50px" ></a></div>';
	
app.filter('safehtml', function($sce) {
    return function(htmlString) {
        return $sce.trustAsHtml(htmlString);
    }
});

app.controller('ProductBackendController', ['$scope', '$http', '$upload', 'createDialog', '$log', function ($scope, $http, $upload, createDialogService, $log) {

    $scope.currentObj = {}
    $scope.list = []
    $scope.list2 = []
    $scope.page = 1;
    $scope.page2 = 1;
    $scope.pageInfo = {}
    $scope.pageInfo2 = {}
    $scope.catalogs=[]
    $scope.selectCatalogId = 0// 0即选择"全部"
    $scope.findProduct = null
    
    $scope.$watch('page', function(){
        refreshDate();
    }, false);
    
    $scope.$watch('selectCatalogId', function(){
    	if($scope.catalogs.length > 0) refreshDate();
    }, false);

    $scope.$watch('findProduct', function(){
    	if($scope.findProduct != null) refreshDate();
    }, false);

    $scope.goNextPage = function() {
        $scope.page = $scope.pageInfo.current +1;
    }

    $scope.goPrevPage = function() {
        $scope.page = $scope.pageInfo.current -1;
    }
    
    $scope.goNextPage2 = function() {
    	$scope.page2 = $scope.pageInfo2.current +1;
    	fillGridWithCatalogs();
    }
    
    $scope.goPrevPage2 = function() {
    	$scope.page2 = $scope.pageInfo2.current -1;
    	fillGridWithCatalogs();
    }

    function refreshDate(){
    	var url = '/products?orderBy=id&sort=desc&size=5&page=' + $scope.page
    	if($scope.selectCatalogId != null) url += '&catalogId=' + $scope.selectCatalogId
    	$log.log('get products from api: ' + url)
    	
        $http.get(url).success(function (data, status, headers, config) {
        	$log.log(data)
            if (data.flag) {
            	for(x in data.data){
            		if(data.data[x].images){
            			data.data[x].imageList = data.data[x].images.split(',')
            		}
        			else{
        				data.data[x].imageList = ''
        			}
            		
            		if(data.data[x].description){
            			data.data[x].descriptionList = data.data[x].description.split(',')
            		}
            		else{
            			data.data[x].descriptionList = ''
            		}		
            	}
            		
                $scope.list = data.data;
                $scope.pageInfo = data.page;
            }
            else {
            	$scope.list = []
            	bootbox.alert(data.message)
            }
        });
    }
    
    $http.get('/catalogs').success(function (data, status, headers, config) {
    	$log.log('get catalogs from api')
    	if (data.flag) {
    		$log.log(data)
    		$scope.catalogs = data.data;
    	}
    	else {
    		bootbox.alert(data.message)
    	}
    });
    
    $scope.gridOptions = { data: 'list',
        rowHeight: 170,
        // showSelectionCheckbox:true,
        // enableCellSelection: false,
        enableRowSelection: true,
        selectedItems: [],
        multiSelect:false,
        // enableCellEdit: false,
        plugins:[new ngGridFlexibleHeightPlugin()],
        columnDefs: [
            {field: 'id', displayName: 'Id', width: '40'},
            {field: 'catalogs', displayName: '所属主题', width: '100',cellTemplate: '<div ng-repeat="catalog in COL_FIELD">{{catalog.name}},</div>'},
            {field: 'name', displayName: '名称', width: '160', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct, cellTemplate: '<div ng-bind-html="COL_FIELD | safehtml"></div>'},
            {field: 'imageList', displayName: '产品图', cellTemplate: uploadTemplateProduct, width: '190'},
            {field: 'productImage', displayName: '定制预览图', cellTemplate: uploadProductPreview, width: '170'},
            {field: 'descriptionList', displayName: '产品介绍图', cellTemplate: uploadDescription, width: '370'},
            {field: 'shortDesc', displayName: '产品简述', width: '200', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct, cellTemplate: '<div ng-bind-html="COL_FIELD | safehtml"></div>'},
            {field: 'alcoholDegree', displayName: '酒精度数', width: '70', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct},
            {field: 'ml', displayName: '容量', width: '60', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct},
            {field: 'bottleSpec', displayName: '酒瓶斤数规格', width: '130', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct},
            //{field: 'decoration', displayName: '工艺', width: '100', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct},
            {field: 'canPaint', displayName: '可打印', width: '70', cellTemplate: '<div><input type="checkbox" ng-model="COL_FIELD" ng-click="updatePaint(row.entity)" /></div>'},
            {field: 'canCarve', displayName: '可雕刻', width: '70', cellTemplate: '<div><input type="checkbox" ng-model="COL_FIELD" ng-click="updateCarve(row.entity)" /></div>'},
            //{field: 'price', displayName: '价格', width: '60', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct},
            //{field: 'resellerMark', displayName: '分销额', width: '60', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct},
            {field: 'isHotSale', displayName: '是否热推', width: '70', cellTemplate: '<div><input type="checkbox" ng-model="COL_FIELD" ng-click="updateHotSale(row.entity)" /></div>'},
            {field: 'soldNumber', displayName: '卖出数', width: '60'},
            {field: 'flavor', displayName: '风味', width: '80', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct},
            {field: 'productionCity', displayName: '产地', width: '100', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct, cellTemplate: '<div ng-bind-html="COL_FIELD | safehtml"></div>'},
            {field: 'brand', displayName: '酒的品牌', width: '70', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct},
            {field: 'source', displayName: '原料', width: '150', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct, cellTemplate: '<div ng-bind-html="COL_FIELD | safehtml"></div>'},
            {field: 'totalWeight', displayName: '毛重', width: '80', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct},
            {field: 'productionLicense', displayName: '生产许可证', width: '120', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct},
            {field: 'factoryLocation', displayName: '厂址', width: '200', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct, cellTemplate: '<div ng-bind-html="COL_FIELD | safehtml"></div>'},
            {field: 'spec', displayName: '规格参数', width: '120', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct, cellTemplate: '<div ng-bind-html="COL_FIELD | safehtml"></div>'},
            {field: 'notice', displayName: '购物须知', width: '150', enableCellEdit: true, editableCellTemplate: cellEditableTemplateProduct, cellTemplate: '<div ng-bind-html="COL_FIELD | safehtml"></div>'},
        ] };

	$scope.uploadImage = function($files, imageField, parentObj) {
        for (var i = 0; i < $files.length; i++) {
    		var file = $files[i];
    		
    		$log.log('start upload image file on id: '
    				+ parentObj.id + ', file: ' + file
    				+ ', property: ' + imageField)
    				
    		$scope.upload = $upload.upload({
    			url : '/upload/image',
    			data : {
    				cid : parentObj.id,
    				className : 'ProductModel',
    				property : imageField
    			},
    			file : file
    		})
    				.progress(
    						function(evt) {
    							$log.log('upload percent: '
    									+ parseInt(100.0 * evt.loaded
    											/ evt.total));
    						})
    				.success(function(data, status, headers, config) {
    					$log.log(data);
    					if (data.flag) {
    						if (imageField == 'images') {
    							if(parentObj[imageField])
        							parentObj[imageField] += ',' + data.data;
        						else
        							parentObj[imageField] = data.data;
        						parentObj.imageList = parentObj[imageField].split(',');
    						} else if (imageField == 'description') {
    							if(parentObj[imageField])
        							parentObj[imageField] += ',' + data.data;
        						else
        							parentObj[imageField] = data.data;
        						parentObj.descriptionList = parentObj[imageField].split(',');
    						} else {
    							bootbox.alert('字段不存在: ' + property)
    						}
    					} else {
    						bootbox.alert(data.message)
    					}
    				});
    		// .error(...)
    		// .then(success, error, progress);
        }
	};
	
	// 删除图片
	$scope.deleteImage = function(obj, property, imageName) {
		$scope.currentObj = obj;
		var index = obj.imageList.indexOf(imageName)
		obj.imageList.splice(index, 1)// 在数组中删掉这个图片文件名
		obj[property] = obj.imageList.join(",")// 数组转为字符串, 以逗号分隔
		$log.log('更新后的images字符串: ' + obj[property])
		
		$scope.saveContent();
	};

	// 删除商品详情
	$scope.deleteDescription = function(obj, property, descriptionName) {
        $scope.currentObj = obj;
        var index = obj.descriptionList.indexOf(descriptionName)
        obj.descriptionList.splice(index, 1)// 在数组中删掉这个图片文件名
        obj[property] = obj.descriptionList.join(",")// 数组转为字符串, 以逗号分隔
        $log.log('更新后的description字符串: ' + obj[property])
        
        $scope.saveContent();
	};
	
	$scope.isShowImg = function(url) {
		return (url) && (url.length > 0);
	};
	
	// 产品是否热销
	$scope.updateHotSale = function(obj) {
		obj.isHotSale = !obj.isHotSale
		$scope.currentObj = obj;
		$scope.saveContent();
	};
	
	$scope.updatePaint = function(obj) {
		obj.canPaint = !obj.canPaint
		$scope.currentObj = obj;
		$scope.saveContent();
	};
	
	$scope.updateCarve = function(obj) {
		obj.canCarve = !obj.canCarve
        $scope.currentObj = obj;
        $scope.saveContent();
	};
	
    // 当前行更新字段
    $scope.updateEntity = function(column, row) {
        $scope.currentObj = row.entity;
        $scope.saveContent();
    };

    // 新建或更新对象
    $scope.saveContent = function() {
        var content = $scope.currentObj;
        if(content){
    		if(content.resellerMark > content.price){
    			content.resellerMark = content.price;
    		}
        }
        var isNew = !content.id
        var url = '/products'
        if(isNew){
        	var http_method = "POST";
        	content.catalogs = $scope.gridCatalogs.selectedItems
        	content.images = ''
        	//content.description = ''	
        }else{
        	var http_method = "PUT";
        	var pos = $scope.list.indexOf(content);
        	url += '/' + content.id
        	
        	if($scope.list2.length > 0){
        		content.catalogs = [] 
        		for(x in $scope.list2){
        			if($scope.list2[x].refCatalog){
        				content.catalogs.push($scope.list2[x])
        			}
        		}
        	}
        }

		$log.log(content)
        $http({method: http_method, url: url, data:content}).success(function(data, status, headers, config) {
                if(data.flag){
                    if(isNew){
                        $scope.list.push(data.data);
                        bootbox.alert('新建[' + data.data.name + ']成功');
                    }else{
                        $scope.list[pos] = data.data;
                    }
                }else{
                    bootbox.alert(data.message);
                }
                
                // 记得把list2设为空, 否则会影响删除图片(catalogs被设空)
                $scope.list2 = []
            });
    };

    $scope.deleteContent = function(){
        var items = $scope.gridOptions.selectedItems;
        if(items.length == 0){
            bootbox.alert("请至少选择一个对象.");
        }else{
            var content = items[0];
            if(content.id){
                bootbox.confirm("您确定要删除这个对象[" + content.name + "]吗?", function(result) {
                    if(result) {
                        $http.delete('/products/' + content.id).success(function(data, status, headers, config) {
                            if (data.flag) {
                            	var index = $scope.list.indexOf(content);
                                $scope.gridOptions.selectItem(index, false);
                                $scope.list.splice(index, 1);
                                bootbox.alert("删除成功");
                            }
                            else {
                                bootbox.alert(data.message);
                            }
                        });
                    }
                });
            }
        }
    };
    
    $scope.addContent = function(){
    	$scope.currentObj = {
    			"name":"【】龙鑫浩然酱香型定制白酒",
    			"brand":"龙鑫浩然",
    			//"shortDesc":"定制酒酒体可自主选择【窖藏】【典藏】【珍藏】三种不同酒体，【窖藏】系列119元；【典藏】系列319元；【珍藏】系列519元。",
    			"alcoholDegree":53,
    			"bottleSpec":"1,3,5,10",
    			//"decoration":"打印,雕刻",
    			"flavor":"酱香型白酒",
    			"productionCity":"贵州省仁怀市茅台镇",
    			"source":"水、高粱、小麦",
    			"totalWeight":0.5,
    			"productionLicense":"QS5200 1501 0041",
    			"factoryLocation":"贵州市仁怀市茅台镇苍龙街道龙井村",
    			"spec":"80x230mm/盒",
    			"notice":"○ 关于售后：<br>酒水属于特殊商品，请务必当面签收，若有破损请签收完好的，破损的拒签返回，签收后的酒水概不退换，因无法保障酒水是否原装，是否真品，拆封后概不退还。<p>○ 温馨提示：<br>由于雕刻类型定制瓶属手工技术制作，加工前需往瓶身粘贴胶纸，加工完毕撕开胶纸时有个别瓶身造成瑕疵，瑕疵大的我们会直接淘汰，瑕疵直径在0.5~1mm范围内不会影响美观的属正常情况，不予退货，敬请谅解。<p>○ 关于快递：<br>众所周知，酒是特殊商品，国家禁止空运，只能全程陆运，所以运输会比较慢一些，还有请朋友们体谅，这也不在商家的控制范围内，如有介意，请慎拍哦！当然我们会对我们发出的每一瓶酒负责到底，保证把酒安安全全送到您的手中！目前来讲，因为国内快递公司整体的服务还不是很完善。为了战胜野蛮快递，我们每一瓶都是经过专业精心包装的，经得住快递员大力水手的卸载搬运。尽管如此，如果运输途中酒水出现破损还是全部由我们帮您承担，免您后顾之忧。<p>本商城默认快递为德邦物流，运输中快递来回晃动，倒置，会有少量渗漏，属正常现象，不影响正常饮用，不属于补偿范围。请接受不了的客户不要拍，谢谢合作！（如若送不到的区域，我们会改用天天快递或者宅急送）",
    			"ml":500,
    			"productImage":"yulan.jpg",
    			"description":"P1.jpg,P2.jpg,P3.jpg,P4.jpg,P5.jpg,P6.jpg,P7.jpg,P8.jpg,P9.jpg,P10.jpg,P11.jpg"
    			
    	};
    	$scope.list2 = [];
    	$scope.pageInfo2 = {}
    	$scope.page2 = 1;
    	
    	fillGridWithCatalogs();
    	
    	createDialogService("/assets/js/controllers/product_editortemplate.html",{
    		id: 'editor',
    		title: '新增产品',
    		scope: $scope,
            footerTemplate: '<div></div>'
    	});
    };
    
    $scope.updateContent = function(){
    	var items = $scope.gridOptions.selectedItems;
        if(items.length == 0){
            bootbox.alert("请至少选择一个对象.");
        }else{
            var content = items[0];
            if(content.id){
            	$scope.currentObj =  items[0];
            	}
           
        $scope.list2 = [];
        $scope.pageInfo2 = {}
        $scope.page2 = 1;

        fillGridWithCatalogs();

        createDialogService("/assets/js/controllers/product_editortemplate.html",{
            id: 'editor',
            title: '更新产品',
            scope: $scope,
            footerTemplate: '<div></div>'
        });
        }
    };

    $scope.formSave = function(formOk){
    	if(!formOk){
            bootbox.alert('验证有误, 请重试');
            return
    	}
        $scope.saveContent();
        $scope.$modalClose();
    };
    
    $scope.dialogClose = function(){
        $scope.gridCatalogs.selectedItems = [];
        $scope.$modalClose();
    };
    
    // 搜索
    $scope.findContent = function(){
    	var url = '/products?size=5&page=' + $scope.page
    	if($scope.findProduct != null) url += '&keyword=' + $scope.findProduct
    	
        $http.get(url).success(function (data, status, headers, config) {
        	$log.log(data)
            if (data.flag) {
                $scope.list = data.data;
                $scope.pageInfo = data.page;
            }
            else {
            	$scope.list = []
            	bootbox.alert(data.message)
            }
        });
    }

    function fillGridWithCatalogs(){
    	if($scope.page2){
    		$http.get('/catalogs?size=100&page=' + $scope.page2).success(function (data, status, headers, config) {
    			$log.log(data)
    			if (data.flag) {
    				$scope.list2 = data.data;
    				for(x in $scope.list2){
    					for(y in $scope.currentObj.catalogs){
    						if($scope.list2[x].id === $scope.currentObj.catalogs[y].id){
    							$scope.list2[x].refCatalog = true
    							$scope.gridCatalogs.selectedItems.push($scope.currentObj.catalogs[y])
    							break
    						}
							else{
								$scope.list2[x].refCatalog = false
							}
    					}
    				}
    				$scope.pageInfo2 = data.page;
    			}
    			else {
    				bootbox.alert(data.message)
    			}
    		});
    	}
    }
    
    $scope.gridCatalogs = { data: 'list2',
            rowHeight: 30,
             showSelectionCheckbox:true,
            // enableCellSelection: false,
// enableRowSelection: true,
            selectWithCheckboxOnly: true,
            enableRowSelection: true,
            multiSelect:false,
            selectedItems: [],
            // enableCellEdit: false,
            plugins:[new ngGridFlexibleHeightPlugin()],
            checkboxCellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ng-model="row.entity.refCatalog" /></div>',
            columnDefs: [
                {field: 'id', displayName: 'ID', width: '50'},
                {field: 'name', displayName: '所属主题', width: '200'},
            ] };
}]);
