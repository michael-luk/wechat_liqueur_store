var app = angular.module('MyOrderApp', []);

app.controller('MyOrderController', [
		'$scope',
		'$http',
		function($scope, $http) {
			$scope.teststr = 'hello'
				
			$scope.myOrders = []
			$scope.orderProducts = []
			$scope.images = []
			$scope.myOrderProducts = []
			$scope.index = {}
			$scope.id = {}
			$scope.userId = GetQueryString('userId')
			
/*			$scope.orderProduct = {}*/
			$http.get('/orders/user/' + $scope.userId).success(
					function(data, status, headers, config) {
						if (data.flag) {
							$scope.myOrders = data.data;
							for (var i = 0, len = $scope.myOrders.length; i < len; i++) {
							/*	$scope.orderProducts = $scope.myOrders[i].orderProducts;*/
							
									$scope.orderProducts.push($scope.myOrders[i].orderProducts[0]) ;
//									for(var j = 0, len = $scope.orderProducts.length; j < len; j++){
//										if($scope.orderProducts[j] != 2 && $scope.orderProducts[j] != 3){
//											$scope.myOrderProducts.push($scope.myOrders[j].orderProducts[0]) ;
//										}
//									}							
									$scope.images.push($scope.orderProducts[i].images.split(",", 1)[0]);
						
//								$scope.myOrderProducts.push($scope.myOrders[i].myOrderProducts[0]) ;
//								for(var j = 0, len = $scope.myOrderProducts.length; j < len; j++){
//									if($scope.myOrderProducts[j] != 2 && $scope.myOrderProducts[j] != 3){
//										$scope.orderProducts.push($scope.myOrders[j].myOrderProducts[0]) ;
//										$scope.images.push($scope.myOrderProducts[j].images.split(",", 1)[0]);
//									}
//								}
//								
								
							}
							
						} else {
							alert(data.message);
						}
					});
			
			$scope.Confirm = function(indexNo){
				$scope.index = indexNo
				$scope.myOrders[indexNo].status = 5
				$scope.id = $scope.myOrders[indexNo]
				$http({
					method : 'PUT',
					url : '/orders/' + $scope.myOrders[indexNo].id + '/status/5 ',
					data : $scope.myOrders[indexNo]
				}).success(function(data, status, headers, config) {
					if (data.flag) {
						// $scope.productList.splice(indexNo, 1)
					} else {
						alert(data.message);
					}
				});
				}
			
			
			$scope.quxiao = function(indexNo){
				$scope.index = indexNo
				$scope.myOrders[indexNo].status = 2
				$scope.id = $scope.myOrders[indexNo]
				$http({
					method : 'PUT',
					url : '/orders/' + $scope.myOrders[indexNo].id + '/status/2 ',
					data : $scope.myOrders[indexNo]
				}).success(function(data, status, headers, config) {
					if (data.flag) {
						// $scope.productList.splice(indexNo, 1)
					} else {
						alert(data.message);
					}
				});
				}
			
			$scope.goPay = function(indexNo){
				//alert($scope.myOrders[indexNo].id)
				window.location.href = window.location.protocol + '//' + window.location.host + '/wxpay/pay?oid=' + $scope.myOrders[indexNo].id
			}
			
		} ]);


function GetQueryString(name) {
	var url = decodeURI(window.location.search);
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = url.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}

