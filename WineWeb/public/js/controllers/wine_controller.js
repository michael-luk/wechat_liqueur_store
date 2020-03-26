var app = angular.module('WineApp', ['ui.bootstrap']);

app.filter('safehtml', function($sce) {
	return function(htmlString) {
		return $sce.trustAsHtml(htmlString);
	}
});

app.filter('getFirstImageFromSplitStr', function () {
	return function (splitStr, position) {
		return '/showimg/upload/' + GetListFromStrInSplit(splitStr)[position];
	}
});

app.filter('lazyLoad', function($rootScope) {
  return function(items) {
    // set step to 2 to illustrate the scroll event problem
    return items.slice(0, 2 * $rootScope.counter);
  };
});

app.controller('WineController', [ '$scope', '$http', '$log', '$rootScope', '$window', '$document', '$timeout', function($scope, $http, $log, $rootScope, $window, $document, $timeout) {
	 //$scope.teststr = 'hello'
	$rootScope.counter = 1;
    
    $scope.loadMoreItems = function() {
      $rootScope.counter += 1;
    };
    
    // Fix 'scroll' event not trigger issue
    $rootScope.resetCounter = function() {
      $rootScope.counter = 1;
      adjustCounter();
    };
    
    function adjustCounter() {
//      $timeout(function() {
////    	  alert($document.find('body'))
//        if ($window.innerHeight > $document.find('body').outerHeight()) {
//          $rootScope.counter++;
//          adjustCounter();
//        }
//      }, 200);
    }
    
    $scope.$watch('predicate', function() {
      $rootScope.resetCounter();
    });
    
    $scope.$watch('reverse', function() {
      $rootScope.resetCounter();
    });
    
	$scope.openId = GetQueryString('openId')
	$scope.resellerCode = GetQueryString('resellerCode')
	$scope.newCatalog = {}
	$scope.CatalogList = []
	$scope.ProductList = []
	$scope.images = []
	$scope.Timages = []
	$scope.findProduct = null
	$scope.winebodys = []

	  $scope.myInterval = 2000;//轮播时间间隔, 毫秒
	  $scope.slides = [];

		if(document.location.pathname.indexOf('/allProduct') > 0){
				$http.get('/products').success(function(data, status, headers, config) {
					if (data.flag) {
						$scope.ProductList = data.data;
						for (var i = 0, len = $scope.ProductList.length; i < len; i++) {
							if($scope.ProductList[i].images){
								/*$scope.images = $scope.ProductList[i].images.split(",", 1);*/
								$scope.images.push($scope.ProductList[i].images.split(",", 1)[0]);
							}
						}
					} else {
//						alert(data.message);
					}
				});
		}
		else{
			$http.get('/infos?classify=广告').success(
					function(data, status, headers, config) {
						if (data.flag) {
							for(i in data.data){
								if(data.data[i].images)
									$scope.slides.push({"id": data.data[i].id, "image": '/showimg/upload/' + data.data[i].images.split(',',1)[0]})
							}
							
							$http.get('/catalogs').success(function(data, status, headers, config) {
								if (data.flag) {
									$scope.CatalogList = data.data;
									for (var i = 0, len = $scope.CatalogList.length; i < len; i++) {
										$scope.Timages.push($scope.CatalogList[i].images.split(",", 1)[0]);
									}
										
									$http.get('/products?size=8').success(function(data, status, headers, config) {
										if (data.flag) {
											$scope.ProductList = data.data;
											for (var i = 0, len = $scope.ProductList.length; i < len; i++) {
												if($scope.ProductList[i].images){
													/*$scope.images = $scope.ProductList[i].images.split(",", 1);*/
													$scope.images.push($scope.ProductList[i].images.split(",", 1)[0]);
												}
											}
										} else {
//											alert(data.message);
										}
									});
								} else {
//									alert(data.message);
								}
							});
						} else {
//							alert(data.message);
						}
					});
		}
		
		
		
		
//		$http
//		.get('/winebodys')
//		.success(
//				function(data, status, headers,
//						config) {
//					if (data.flag) {
//						$scope.winebodys = data.data;						
//					} else {
//						alert(data.message);
//					}
//
//				});
		
		
		
		
	
	$scope.user = {}
	if(document.location.pathname != '/p/wine' && document.location.pathname != '/p/themes'){
		$http.get('/users/current/login').success(
				function(data, status, headers, config) {
					if (data.flag) {
						$scope.user = data.data;
					} else {
						alert(data.message);
					}
				});
	}
	else{
			$http.get('/infos?size=8&classify=新闻&page=1').success(
			function(data, status, headers, config) {
				if (data.flag) {
					$scope.news = data.data
					$scope.pageInfo=data.page
				} else {
					//alert(data.message);
				}
			});
	}

	
	$scope.find = function(){
    	var url = '/products?size=1000&keyword=' + $scope.findProduct
	    	
	        $http.get(url).success(function (data, status, headers, config) {
	        	/*$log.log(data)*/
	            if (data.flag) {
	                $scope.ProductList = data.data;
	                for (var i = 0, len = $scope.ProductList.length; i < len; i++) {
	    				$scope.images.push($scope.ProductList[i].images.split(",", 1)[0]);
	    			}
	            }
	            else {
	            	$scope.ProductList = []
	            	//alert(data.message)
	            }
	        });
	    }
	  /* $scope.$watch('findProduct', function(){
	    	if($scope.findProduct != null) refreshDate();
	    }, false);
	 $scope.find = function(){
	    	var url = '/products'
	    	if($scope.findProduct != null) url += '?keyword=' + $scope.findProduct    	
	        $http.get(url).success(function (data, status, headers, config) {
	        	$log.log(data)
	            if (data.flag) {
	                $scope.ProductList = data.data;
	                $scope.pageInfo = data.page;
	                for (var i = 0, len = $scope.ProductList.length; i < len; i++) {
	    				$scope.images = $scope.ProductList[i].images.split(",", 1);
	    			}
	            }
	            else {
	            	$scope.ProductListList = []
	            	bootbox.alert(data.message)
	            }
	        });
	    }
*/
	
//	//处理微信用户
//	if($scope.openId){
//		$http.get('/users/check/weixin?openId=' + $scope.openId).success(function(data, status, headers, config) {
//			if (data.flag) {
//				$log.log('该微信用户已注册')
//				setReseller(data.id)
//			} else {
//				$log.log('该微信用户未注册,现在注册')
//		        $http({method: 'POST', url: '/users/add/weixin?openId=' + $scope.openId, data: {}}).success(function(data, status, headers, config) {
//		                if(data.flag){
//		    				$log.log('该微信用户注册成功:' + data.id)
//		    				setReseller(data.id)
//		                }else{
//		    				$log.log('该微信用户注册失败:' + data.message)
//		                }
//		            });
//			}
//		});
//	}
//
//	//设置分销上线
//	function setReseller(userId) {
//		if(userId && $scope.resellerCode){
//			$log.log('设置分销上线')
//			$http({method: 'POST', url: '/users/setreseller', data: {'userId':userId,'resellerCode':$scope.resellerCode}}).success(function(data, status, headers, config) {
//				if(data.flag){
//					$log.log('设置分销上线成功')
//				}else{
//					$log.log('已经存在分销上线:' + data.message)
//					//todo: 显示弹窗"已经关注过"
//				}
//			});
//		}
//	}

} ]);

function GetQueryString(name) {
	var url = decodeURI(window.location.search);
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = url.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}