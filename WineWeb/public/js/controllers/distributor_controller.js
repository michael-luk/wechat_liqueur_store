var app = angular.module('DistributorApp', []);

app.filter('safehtml', function($sce) {
	return function(htmlString) {
		return $sce.trustAsHtml(htmlString);
	}
});

app.controller('DistributorController', [
		'$scope',
		'$http',
		function($scope, $http) {
			$scope.resellerTotalAmount = 123
			$scope.drawing ={
					'phone':'',
					'yongJin':'',
					'refUserId':'',
			}
			$scope.allowFondout = false
			$scope.put = true
			$scope.checkFondout = function(obj){				
			
				if(obj == 0){
					alert('你还没有获得佣金,无法提款!')
					return 
				}
				
				$scope.allowFondout=true
		
				
			}
			
			$scope.save = function(uid) {
				$scope.put = false
				
			        // 利用对话框返回的值 （true 或者 false）
			        if (confirm("你确定要提款吗？金额:" + $scope.user.currentResellerProfit.toFixed(2))) {  
			            
			          // alert(uid)
			        	$scope.drawing.yongJin = $scope.user.currentResellerProfit
						$scope.drawing.refUserId = uid
						$http({
							method : 'POST',
							url : '/fondoutrequests',
							data : $scope.drawing
						}).success(function(data, status, headers, config) {
							if (data.flag) {
								$scope.allowFondout= false
								// $scope.user.currentResellerProfit=0
							} else {
								alert(data.message)
							}
						});
			        } else{
			        	$scope.allowFondout= false
			        	$scope.status = 1
			        	$scope.put = true
			        } 
			  
				};

			$scope.user = {}
			$scope.status = 1
			$scope.fondOutOrder = []
			
			$http.get('/users/current/login').success(
					function(data, status, headers, config) {
						if (data.flag) {
							$scope.user = data.data;
																					
							$http.get('/fondoutrequests?refUserId=' + $scope.user.id).success(
									function(data, status, headers, config) {
									
										if (data.flag) {
											$scope.fondOutOrder = data.data;
											for(var i=0 ; i < $scope.fondOutOrder.length ; i++){
												if($scope.fondOutOrder[i].status === 0){
													$scope.status = 0												
													}
											/* break */
											}
										} /*else {
											$scope.status = 1
											//alert(data.message);
										}*/
									});
							
							$http.get('/resellerorders/amount/users/' + $scope.user.id).success(function(data, status, headers, config) {
								if (data.flag) {
									$scope.resellerAmount = data.data;
								}
								else{
									$scope.resellerAmount = '未统计';
								}
							});
						} else {
							alert(data.message);
						}
					});
			
			
			
			
			
			
		
			
			
			
			
			
			
			
			
			
			
			
			
		} ]);
