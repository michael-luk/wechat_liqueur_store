var app = angular.module('OrderMessageApp', []);
/*'me-lazyload'*/
app.controller('OrderMessageController', [
		'$scope',
		'$http',
		function($scope, $http) {
			$scope.teststr = 'hello'
				
			$scope.myOrder = {}
			$scope.orderProduct = {}
			$scope.images = []
			$scope.myOrderProducts = []
			$scope.index = {}
			$scope.id = {}
			$scope.userId = GetQueryString('userId')
			$scope.myOrderId = GetQueryString('myOrderId')

			$http.get('/orders/' + GetQueryString('myOrderId')).success(
					function(data, status, headers, config) {
						if (data.flag) {
							$scope.myOrder = data.data;
							$scope.orderProduct = $scope.myOrder.orderProducts[0]
						
						} else {
							alert(data.message);
						}
					});
			

			
			$scope.Confirm = function(indexNo){
				$scope.index = indexNo
				$scope.myOrder.status = 5
				$scope.id = $scope.myOrder
				$http({
					method : 'PUT',
					url: '/orders/userupdate/' + $scope.myOrder.id + '/status/5 ',
					data : $scope.myOrder
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
				$scope.myOrder.status = 2
				$scope.id = $scope.myOrder
				$http({
					method : 'PUT',
					url: '/orders/userupdate/' + $scope.myOrder.id + '/status/2 ',
					data : $scope.myOrder
				}).success(function(data, status, headers, config) {
					if (data.flag) {
						// $scope.productList.splice(indexNo, 1)
					} else {
						alert(data.message);
					}
				});
				}
			
		/*	$scope.goPay = function(indexNo){
				//alert($scope.myOrders[indexNo].id)
				window.location.href = window.location.protocol + '//' + window.location.host + '/wxpay/pay?oid=' + $scope.myOrders[indexNo].id
			}*/
				$scope.goPay = function(){
				//alert($scope.myOrders[indexNo].id)
				window.location.href = window.location.protocol + '//' + window.location.host + '/wxpay/pay?oid=' + $scope.myOrder.id
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

