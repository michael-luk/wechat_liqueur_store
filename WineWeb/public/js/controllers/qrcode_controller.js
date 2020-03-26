/**
 * Created by yanglu on 15/11/16.
 */

var app = angular.module('QrCodeApp', []);

app.controller('QrCodeController', [
    '$scope',
    '$http',
    '$log',
    function ($scope, $http, $log) {
        var userId = GetQueryString('u')
        if (!userId) {
            alert('无法获取用户信息')
            return
        }
        $scope.user = {}
        $http.get('/users/' + userId).success(
            function (data, status, headers, config) {
                if (data.flag) {
                    $scope.user = data.data;
                } else {
                    alert(data.message);
                }
            });
        //$scope.headImg = GetQueryString('headImg')
        //$scope.nickName = GetQueryString('nickName')
        //$scope.resellerBarcode = GetQueryString('resellerBarcode')
    }
]);

function GetQueryString(name) {
    var url = decodeURI(window.location.search);
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = url.substr(1).match(reg);
    if (r != null)
        return unescape(r[2]);
    return null;
}