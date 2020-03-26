var app = angular.module('WproductApp', [ 'ui.bootstrap' ]);

app.filter('safehtml', function($sce) {
	return function(htmlString) {
		return $sce.trustAsHtml(htmlString);
	}
});

app
		.controller(
				'WproductController',
				[
						'$scope',
						'$http',
						function($scope, $http) {
							$scope.selectTab = 1
							$scope.description = []
							$scope.quantity = 1
							$scope.product = {}
							$scope.newOrder = {}
							
							$scope.selectedTheme = {}
							$scope.selectedThemeDiv = 0
							$scope.selectedWinebody = {}
							$scope.selectedWinebodyDiv = 0
							$scope.selectedBottleSpec = {}
							$scope.selectedBottleSpecDiv = 0
							$scope.selectedDecoration = {}
							$scope.selectedDecorationDiv = 0
							
							$scope.shipInfoList = []
							$scope.selectedShipInfo = {}
							
							$scope.favoriteProducts = []
							$scope.favoriteProduct = false
							
							$scope.images = []
							$scope.userId = GetQueryString('userId')
							$scope.myInterval = 2000;// 轮播时间间隔, 毫秒
							$scope.slides = []
							
							$scope.winebodys = []
							$scope.bottleSpecs = []
							$scope.canDecorations = []
							$scope.selectprice = 0

							function updatePrice() {
								var priceBottleSpec = 0
								if ($scope.selectedBottleSpec > 1)
									priceBottleSpec = 20
								var priceWinebody = ($scope.selectedWinebody.price + priceBottleSpec) * $scope.selectedBottleSpec
								var priceDecoration = 0
								if ($scope.selectedDecoration === "打印")
									priceDecoration = 30
								if ($scope.selectedDecoration === "雕刻"){
									priceDecoration = 69
									if($scope.product.catalogs.length > 0){
										if($scope.product.catalogs[0].id == 5){
											priceDecoration = 0	// hardcode 封坛收藏系列, 选雕刻不收费
										}
									}
								}
								$scope.selectprice = priceWinebody + priceDecoration
							}

							var url = window.location.pathname
							var id = url.substring(url.lastIndexOf("/") + 1);
							$http
									.get('/products/' + id)
									.success(
											function(data, status, headers,
													config) {
												if (data.flag) {
													$scope.product = data.data;
													$scope.description = $scope.product.description
															.split(",")
													$scope.bottleSpecs = $scope.product.bottleSpec
															.split(",")

													if($scope.product.canPaint){
														$scope.canDecorations.push("打印")
													}
													if($scope.product.canCarve){
														$scope.canDecorations.push("雕刻")
													}
													if($scope.canDecorations.length > 0){
														$scope.selectedDecoration = $scope.canDecorations[0]
													}
													
//													for (var i = 0; i < $scope.decorations.length; i++) {
//														if ($scope.product.canPaint
//																&& $scope.decorations[i] === '打印') {
//															$scope.canDecorations.push($scope.decorations[0])
//														}
//														if ($scope.product.canCarve
//																&& $scope.decorations[i] === '雕刻') {
//															$scope.canDecorations
//																	.push($scope.decorations[1])
//														}
//													}

													$scope.selectedBottleSpec = $scope.bottleSpecs[0]
													//$scope.selectedDecoration = $scope.decorations[0]
													if ($scope.product.themes.length > 0) {
														$scope.selectedTheme = $scope.product.themes[0]
													}
													var imageList = $scope.selectedTheme.images
															.split(",")
													for (i in imageList) {
														$scope.slides
																.push({
																	"id" : i,
																	"image" : '/showimg/upload/'
																			+ imageList[i]
																})
													}
													
													if($scope.product.id === 68){
														// hardcode 杯具
														$scope.winebodys = [{
																			id: 4,
																			name: "酒具",
																			price: 690,
																			resellerMark: 290,
																			comment: ''
																			}];
														$scope.selectprice = $scope.winebodys[0].price
													}
													else if($scope.product.id === 69){
														// hardcode 杯具
														$scope.winebodys = [{
																			id: 4,
																			name: "酒具",
																			price: 690,
																			resellerMark: 290,
																			comment: ''
																			}];
														$scope.selectprice = $scope.winebodys[0].price
													}
													else if($scope.product.id === 129){
														// hardcode 杯具
														$scope.winebodys = [{
																			id: 4,
																			name: "酒具",
																			price: 800,
																			resellerMark: 290,
																			comment: ''
																			}];
														$scope.selectedWinebody = $scope.winebodys[0]
														$scope.selectprice = $scope.winebodys[0].price
													}
													else if($scope.product.id === 70){
														// hardcode 杯具
														$scope.winebodys = [{
																			id: 4,
																			name: "酒具",
																			price: 800,
																			resellerMark: 290,
																			comment: ''
																			}];
														$scope.selectedWinebody = $scope.winebodys[0]
														$scope.selectprice = $scope.winebodys[0].price
													}
													else if($scope.product.id === 110){
														// hardcode 杯具
														$scope.winebodys = [{
																			id: 4,
																			name: "酒具",
																			price: 580,
																			resellerMark: 290,
																			comment: ''
																			}];
														$scope.selectprice = $scope.winebodys[0].price
													}
													else if($scope.product.id === 111){
														// hardcode 杯具
														$scope.winebodys = [{
																			id: 4,
																			name: "酒具",
																			price: 300,
																			resellerMark: 290,
																			comment: ''
																			}];
														$scope.selectprice = $scope.winebodys[0].price
													}
													else if($scope.product.id === 112){
														// hardcode 杯具
														$scope.winebodys = [{
																			id: 4,
																			name: "酒具",
																			price: 399,
																			resellerMark: 290,
																			comment: ''
																			}];
														$scope.selectprice = $scope.winebodys[0].price
													}
													else if($scope.product.id === 51){
														// hardcode 经典系列
														$scope.winebodys = [{
																			id: 3,
																			name: "珍藏",
																			price: 499,
																			resellerMark: 290,
																			comment: ''
																			}];
														$scope.selectedWinebody = $scope.winebodys[0]
														$scope.selectprice = $scope.selectedWinebody.price
														updatePrice()
													}
													else if($scope.product.id === 52){
														// hardcode 经典系列
														$scope.winebodys = [{
																			id: 2,
																			name: "典藏",
																			price: 299,
																			resellerMark: 180,
																			comment: ''
																			}];
														$scope.selectedWinebody = $scope.winebodys[0]
														$scope.selectprice = $scope.selectedWinebody.price
														updatePrice()
													}
													else if($scope.product.id === 53){
														// hardcode 经典系列
														$scope.winebodys = [{
																			id: 1,
																			name: "窖藏",
																			price: 99,
																			resellerMark: 49,
																			comment: ''
																			}];
														$scope.selectedWinebody = $scope.winebodys[0]
														$scope.selectprice = $scope.selectedWinebody.price
														updatePrice()
													}
													else{
														$http
														.get('/winebodys')
														.success(
																function(data, status, headers,
																		config) {
																	if (data.flag) {
																		$scope.winebodys = data.data;
																		$scope.selectedWinebody = $scope.winebodys[0]
																		$scope.selectprice = $scope.selectedWinebody.price

																		updatePrice()			
																	} else {
																		alert(data.message);
																	}

																});
													}
												} else {
													alert(data.message);
												}
											});

							$http
									.get('/shipAreaPrices')
									.success(
											function(data, status, headers,
													config) {
												if (data.flag) {
													$scope.shipInfoList = data.data;
													if (data.data.length > 0) {
														$scope.selectedShipInfo = data.data[0]
													}
												} else {
													alert(data.message);
												}
											});

							$scope.selectWinebody = function(indexNo) {
								$scope.selectedWinebodyDiv = indexNo
								$scope.selectedWinebody = $scope.winebodys[$scope.selectedWinebodyDiv]
								$scope.selectprice = $scope.selectedWinebody.price
								updatePrice()
							};
							/*
							 * $scope.user = {}
							 * $http.get('/users/current/login').success(
							 * function(data, status, headers, config) { if
							 * (data.flag) { $scope.user = data.data; } else {
							 * alert(data.message); } });
							 * 
							 */

							$http
									.get(
											'/users/' + $scope.userId
													+ '/favoriteProducts/' + id)
									.success(
											function(data, status, headers,
													config) {
												$scope.favoriteProduct = data.flag
											});
							refreshData()

							function refreshData() {
								$http
										.get(
												'/users/' + $scope.userId
														+ '/favoriteProducts')
										.success(
												function(data, status, headers,
														config) {
													if (data.flag) {
														$scope.images = []
														$scope.favoriteProducts = data.data
														for (var i = 0; i < $scope.favoriteProducts.length; i++) {
															$scope.images
																	.push($scope.favoriteProducts[i].images
																			.split(
																					",",
																					1)[0]);
														}
													} else {
														$scope.favoriteProducts = []
													}
												});
							}

							$scope.favorite = function() {
								$http(
										{
											method : 'PUT',
											url : '/users/' + $scope.userId
													+ '/favoriteProduct/'
													+ $scope.product.id + '/on',
											data : $scope.product
										})
										.success(
												function(data, status, headers,
														config) {
													if (data.flag) {
														/*
														 * $scope.Shipinfo = {}
														 * $log($scope.Shipinfo)
														 */
													} else {
														/* alert(data.message) */
													}
												});
								$scope.favoriteProduct = true
							};

							$scope.cancelFavorite = function() {
								$http(
										{
											method : 'PUT',
											url : '/users/' + $scope.userId
													+ '/favoriteProduct/'
													+ $scope.product.id
													+ '/off',
											data : $scope.product
										})
										.success(
												function(data, status, headers,
														config) {
													if (data.flag) {
														/*
														 * $scope.Shipinfo = {}
														 * $log($scope.Shipinfo)
														 */
													} else {
														/* alert(data.message) */
													}
												});
								$scope.favoriteProduct = false
							};

							$scope.cancelFavoriteFromMyFavoritePage = function(
									obj) {
								$http(
										{
											method : 'PUT',
											url : '/users/' + $scope.userId
													+ '/favoriteProduct/'
													+ obj.id + '/off',
											data : obj
										})
										.success(
												function(data, status, headers,
														config) {
													if (data.flag) {
														refreshData()
													} else {
														/* alert(data.message) */
													}
												});
							};

							$scope.setTab = function(tabNumber) {
								$scope.selectTab = tabNumber
							};
							$scope.setAdd = function() {
								$scope.quantity = $scope.quantity + 1
							};
							$scope.setMinus = function() {
								if ($scope.quantity > 1) {
									$scope.quantity = $scope.quantity - 1
								}
							};

							$scope.selectBottleSpec = function(indexNo) {
								$scope.selectedBottleSpecDiv = indexNo
								$scope.selectedBottleSpec = $scope.bottleSpecs[indexNo]
								if ($scope.selectedBottleSpec != '一斤') {
									$scope.selectprice = $scope.selectedWinebody.price + 20
								} else {
									$scope.selectprice = $scope.selectedWinebody.price
								}
								updatePrice()
							};
							$scope.selectDecoration = function(indexNo) {
								$scope.selectedDecorationDiv = indexNo
								$scope.selectedDecoration = $scope.canDecorations[indexNo]
								updatePrice()
							};
							/*
							 * $scope.select = function(indexNo) {
							 * alert(indexNo) $scope.selectedThemeDiv = indexNo
							 * $scope.selectedTheme =
							 * $scope.product.themes[indexNo] var imageList =
							 * $scope.selectedTheme.images.split(",")
							 * if(imageList.length > 0) $scope.slides = [] for(i
							 * in imageList){ $scope.slides.push({"id": i,
							 * "image": '/showimg/upload/' + imageList[i]}) } };
							 */

						} ]);

function GetQueryString(name) {
	var url = decodeURI(window.location.search);
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = url.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}
