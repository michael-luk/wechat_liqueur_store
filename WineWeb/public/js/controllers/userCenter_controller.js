var app = angular.module('UserApp', []);

app.controller('UserController', [
		'$scope',
		'$http',
		function($scope, $http) {
			$scope.user = {}
			$http.get('/users/current/login').success(
					function(data, status, headers, config) {
						if (data.flag) {
							$scope.user = data.data;
						} else {
							alert(data.message);
						}
					});
		} ]);
