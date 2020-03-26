var app = angular.module('PayApp', ['angularFileUpload', 'fundoo.services']);

app.filter('safehtml', function ($sce) {
    return function (htmlString) {
        return $sce.trustAsHtml(htmlString);
    }
});

app.controller('PayController', [
    '$log',
    '$scope',
    '$upload',
    '$http',
    function ($log, $scope, $upload, $http) {
        $scope.newOrder = {
            'orderNo': '',
            'refBuyerId': 0,
            'buyer': {},
            'refResellerId': 0,
            'reseller': {},
            'orderProducts': [],
            'refThemeId': GetQueryString('themeId'),
            'refThemeName': GetQueryString('theme'),
            'wishWord': GetQueryString('wishWord'),
            'image': GetQueryString('image').split(',')[0],
            'wishImage': GetQueryString('wishImage'),
            /* 'price' : GetQueryString('price'), */
            'quantity': GetQueryString('num'),
            'shipFee': 0,
            'price': GetQueryString('price'),
            /* 'productAmount' : GetQueryString('price') * $scope.newOrder.quantity, */
            'amount': GetQueryString('amount'),
            'invoiceTitle': '个人',
            'shipName': '',
            'shipPhone': '',
            'shipPostCode': '',
            'shipProvice': '',
            'shipLocation': '',
            'wineBody': GetQueryString('wineBody'),
            'wineWeight': 0,
            'decoration': GetQueryString('decoration'),
        }

        if (GetQueryString('wineWeight')) {
            $scope.newOrder.wineWeight = GetQueryString('wineWeight')
        }

        $scope.LocationIndex = GetQueryString('LocationId')
        $scope.pid = GetQueryString('pid')
        $scope.needInvoice = false
        /* $scope.invoiceType = '个人' */
        $scope.myLocations = []
        $scope.defaultLocation = null
        $scope.userId = GetQueryString('userId')
        $scope.newOrder.freight = true
        $scope.shipInfoList = []
        $scope.shipInfo = {}
        $scope.shipPrice = {}
        $scope.user = {}
        $scope.orderProduct = {}
        $scope.newOrder.refBuyerId = GetQueryString('userId')

        $http.get('/users/current/login').success(
            function (data, status, headers, config) {
                if (data.flag) {
                    $scope.user = data.data;
                    $scope.userId = $scope.user.id
                    $scope.newOrder.buyer = $scope.user
                    $scope.newOrder.refBuyerId = $scope.user.id
                    $scope.invoiceType = '个人'

                    if ($scope.user.invoiceTitle != '个人') {
                        /*
                         * if($scope.user.invoiceTitle === '个人'){
                         * $scope.invoiceType = '个人'
                         * $scope.newOrder.invoiceTitle =
                         * $scope.user.invoiceTitle }else{
                         */
                        $scope.needInvoice = true
                        $scope.invoiceType = '单位'
                        $scope.newOrder.invoiceTitle = $scope.user.invoiceTitle
                        /* } */

                    } else {
                        $scope.newOrder.invoiceTitle = null
                        if ($scope.needInvoice) {

                            /* $log.log($scope.invoiceType) */
                            if ($scope.invoiceType === '个人') {
                                $scope.newOrder.invoiceTitle = '个人'
                            } else {

                                if (!$scope.newOrder.invoiceTitle) {
                                    alert('请输入发票的单位抬头');
                                }
                                /* $log.log($scope.newOrder.invoiceTitle) */

                            }
                        }
                    }

                    $http.get('/users/' + $scope.userId + '/shipInfos').success(
                        function (data, status, headers, config) {
                            if (data.flag) {
                                $scope.myLocations = data.data;
                                if ($scope.LocationIndex) {
                                    $scope.defaultLocation = $scope.myLocations[$scope.LocationIndex]
                                }
                                else {
                                    $scope.defaultLocation = $scope.myLocations[0]

                                    for (var i = 0; i < $scope.myLocations.length; i++) {
                                        if ($scope.myLocations[i].isDefault) {
                                            $scope.defaultLocation = $scope.myLocations[i]
                                            break
                                        }
                                    }
                                }
                                $scope.newOrder.shipName = $scope.defaultLocation.name
                                $scope.newOrder.shipPhone = $scope.defaultLocation.phone
                                $scope.newOrder.shipPostCode = $scope.defaultLocation.postCode
                                $scope.newOrder.shipProvice = $scope.defaultLocation.provice
                                $scope.newOrder.shipLocation = $scope.defaultLocation.location

                                /* refreshData() */


                                $http
                                    .get('/shipAreaPrices/' + $scope.defaultLocation.provice)
                                    .success(
                                    function (data, status, headers,
                                              config) {
                                        if (data.flag) {

                                            $scope.shipInfo = data.data;
                                            $scope.shipPrice = $scope.shipInfo.shipPrice
                                        }
                                    });


                            } else {
                                $scope.myLocations = null
                                /* alert(data.message); */
                            }
                        });

                } else {
                    alert(data.message);
                }
            });

        refreshData()
        function refreshData() {

            $http.get('/products/' + GetQueryString('pid')).success(
                function (data, status, headers, config) {
                    if (data.flag) {
                        $scope.newOrder.orderProducts.push(data.data);
                        $scope.orderProduct = $scope.newOrder.orderProducts[0]
                        /*
                         * $http .get('/shipAreaPrices/' + $scope.defaultLocation.provice ) .success(
                         * function(data, status, headers, config) { if (data.flag) {
                         *
                         * $scope.shipInfo = data.data; $scope.shipPrice = $scope.shipInfo.shipPrice
                         * $scope.newOrder.productAmount =
                         * GetQueryString('price')*$scope.newOrder.quantity
                         *
                         * $scope.newOrder.amounts = $scope.newOrder.amount.toFixed(2) } });
                         */
                    }
                });
        }


        /*
         * $http.get('/users/current/login').success( function(data, status,
         * headers, config) { if (data.flag) { $scope.currentUser =
         * data.data; $scope.newOrder.refBuyerId = $scope.currentUser.id
         * $scope.newOrder.buyer = $scope.currentUser } else {
         * alert(data.message); } });
         */
        $scope.addOrder = function () {
            // 未登录不允许提交订单
            if (!$scope.user) {
                alert('用户未登录')
                return
            }

            /*
             * if ($scope.needInvoice) { $log.log($scope.invoiceType) if
             * ($scope.invoiceType === '个人') { $scope.newOrder.invoiceTitle =
             * '个人' } else { if (!$scope.newOrder.invoiceTitle) {
             * alert('请输入发票的单位抬头'); } $log.log($scope.newOrder.invoiceTitle) } }
             */

            // $log.log($scope.newOrder)
            if ($scope.newOrder.orderProducts == null) {
                alert('产品未获取,请重试')
                return
            }

            //productAmount 商品总额用来做分销用, 必须有数值
            /* if (!$scope.newOrder.productAmount || $scope.newOrder.productAmount <= 0){ */
            /* alert('订单商品总额有误,请重新下单') */
            /* return */
            /* } */

            if (!$scope.newOrder.price || $scope.newOrder.price <= 0) {
                alert('订单总额有误,请重新下单')
                return
            }

            //用户若有上线, 设置这个订单为分销订单
            if ($scope.user.refUplineUserId) {
                if ($scope.user.refUplineUserId > 0) {
                    $scope.newOrder.refResellerId = $scope.user.refUplineUserId
                }
            }

            $http({
                method: 'POST',
                url: '/orders',
                data: $scope.newOrder
            }).success(function (data, status, headers, config) {
                if (data.flag) {
                    window.location.href = window.location.protocol + '//' + window.location.host + '/wxpay/pay?oid=' + data.data.id
                } else {
                    alert(data.message)
                }
            });
        };

        $scope.jinggao = function (obj) {
            if (obj === null) {
                alert('请填写地址')
            }
        }


    }]);


function GetQueryString(name) {
    var url = decodeURI(window.location.search);
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = url.substr(1).match(reg);
    if (r != null)
        return unescape(r[2]);
    return null;
}
