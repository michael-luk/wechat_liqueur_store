var app = angular.module('FrameApp', []);

app.filter('safehtml', function($sce) {
	return function(htmlString) {
		return $sce.trustAsHtml(htmlString);
	}
});

app.controller('FrameController', [
		'$scope',
		'$http',
		function($scope, $http) {
			// $scope.teststr = 'hello'  
			$scope.newCompany = {}
			//  $scope.CompanyList = []
			$http.get('/company').success(
					function(data, status, headers, config) {
						if (data.flag) {
							$scope.newCompany = data.data;
							//           alert($scope.newCompany.name)
						} else {
							alert(data.message);
						}
					});

		} ]);

angular.element(document).ready(function() {
	angular.bootstrap(document.getElementById("A2"), [ "FrameApp" ]);
});