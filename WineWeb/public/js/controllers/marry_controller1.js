var app = angular.module('MarryApp', ['ui.bootstrap']);

app.filter('safehtml', function($sce) {
	return function(htmlString) {
		return $sce.trustAsHtml(htmlString);
	}
});

app.controller('MarryController', [
		'$scope',
		'$http',
		function($scope, $http) {

			$scope.newCatalog = {}
			$scope.ProductList = []
			$scope.images = []
			$scope.Cimages = []
//			$scope.Catalogs = []
			
			  $scope.myInterval = 2000;//轮播时间间隔, 毫秒
			  $scope.slides = [];
				
			var url = window.location.pathname			
			var id = url.substring(url.lastIndexOf("/") + 1);		
			
			$http.get('/catalogs/' + id).success(
					function(data, status, headers, config) {
						if (data.flag) {
							$scope.newCatalog = data.data;			
							   var imageList = $scope.newCatalog.images.split(",")
								for(i in imageList){
									$scope.slides.push({"id": i, "image": '/showimg/upload/' + imageList[i]})
								}			

								$http.get('/catalogs/' + id +'/products').success(
										function(data, status, headers, config) {
											if (data.flag) {
												$scope.ProductList = data.data;
												 for(var i=0,len=$scope.ProductList.length; i<len; i++){
													/* $scope.images = $scope.ProductList[i].images
														.split(",",1);*/
													 $scope.images.push($scope.ProductList[i].images
																.split(",",1)[0]);
													 }													 
													$scope.degree = '度'
											} else {
					//							alert(data.message);
											}
										});								
						} else {
//							alert(data.message);
						}
					});

			
//			$http.get('/catalogs').success(
//					function(data, status, headers, config) {
//						if (data.flag) {
//							$scope.Catalogs = data.data;														  					
//						}else {
//							alert(data.message);
//						}
//					});
			
			
			$http
			.get('/winebodys')
			.success(
					function(data, status, headers,
							config) {
						if (data.flag) {
							$scope.winebodys = data.data;						
						} else {
							alert(data.message);
						}

					});
			
			
			
			
			$scope.user = {}
			if(document.location.pathname.indexOf("w/") > 0) {
				$http.get('/users/current/login').success(
					function (data, status, headers, config) {
						if (data.flag) {
							$scope.user = data.data;
						} else {
							alert(data.message);
						}
					});
			}
//			alert(id)
			/*
			 * var url = window.location.search; //alert(url.length);
			 * //alert(url.lastIndexOf('=')); var loc =
			 * url.substring(url.lastIndexOf('=')+1, url.length);
			 */

		} ]);
