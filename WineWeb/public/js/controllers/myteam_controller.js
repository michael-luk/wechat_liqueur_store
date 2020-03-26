var app = angular.module('MyResellerTeamApp', []);

app.controller('MyResellerTeamController', ['$log', '$scope', '$http', function($log, $scope, $http) {

    $scope.downlineUsers = [];
    $scope.user = {};
    $scope.resellerAmount = 0;

    $http.get('/users/current/login').success(function(data, status, headers, config) {
        if (data.flag) {
            $scope.user = data.data;

            $http.get('/downlines/users/' + $scope.user.id + '?size=500').success(function(data, status, headers, config) {
                if (data.flag) {
                    $scope.downlineUsers = data.data;

                    $http.get('/resellerorders/amount/users/' + $scope.user.id).success(function(data, status, headers, config) {
                        if (data.flag) {
                            $scope.resellerAmount = data.data;
                        } else {
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