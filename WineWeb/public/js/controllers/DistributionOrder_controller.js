var app = angular.module('MyResellerOrderApp', []);

app.controller('MyResellerOrderController', ['$log', '$scope', '$http', function($log, $scope, $http) {

    $scope.myOrders = [] 
	$scope.images = [] 
	$scope.user = {}
	$scope.resellerAmount = 0
	
    $http.get('/users/current/login').success(function(data, status, headers, config) {
        if (data.flag) {
            $scope.user = data.data;

            $http.get('/resellerorders/users/' + $scope.user.id).success(function(data, status, headers, config) {
                if (data.flag) {
                    $scope.myOrders = data.data;
                    for (var i = 0,
                    len = $scope.myOrders.length; i < len; i++) {
                    	if ($scope.myOrders[i].orderProducts.length > 0) {
	                        if ($scope.myOrders[i].orderProducts[0].images) {
	                            $scope.images.push($scope.myOrders[i].orderProducts[0].images.split(",", 1)[0]);
	                        }
                    	}
                    }

                    $http.get('/resellerorders/amount/users/' + $scope.user.id).success(function(data, status, headers, config) {
                        if (data.flag) {
                            $scope.resellerAmount = data.data;
                        }
						else{
                            $scope.resellerAmount = '未统计';
						}
                    });
                }
            });

        } else {
            alert(data.message);
        }
    });
}]);