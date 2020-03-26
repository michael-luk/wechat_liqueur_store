var app = angular.module('newsApp', []);

app.controller('newsController', [
    '$scope',
    '$http',

    function ($scope, $http) {
        $scope.news = [];
        $scope.page = 1;
        $scope.currentObj = {}
        $scope.pageInfo = {}
        $scope.jumpPage = 1


        $scope.$watch('page', function () {
            refreshDate();
        }, false);


        $scope.goHomePage = function () {
            $scope.page = 1;
        }

        $scope.goPrevPage = function () {

            $scope.page = $scope.pageInfo.current - 1;

        }

        $scope.goNextPage = function () {

            $scope.page = $scope.pageInfo.current + 1;

        }

        $scope.goLastPage = function () {
            $scope.page = $scope.pageInfo.total;
        }

        $scope.goJumpPage = function () {
            if ($scope.pageInfo.total < $scope.jumpPage) {

                $scope.page = $scope.page;
            } else {
                $scope.page = $scope.jumpPage;
            }


        }

        function refreshDate() {
            $http.get('/infos?size=8&classify=新闻&page=' + $scope.page).success(
                function (data, status, headers, config) {
                    if (data.flag) {
                        $scope.news = data.data
                        $scope.pageInfo = data.page

                    } else {
                        //alert(data.message);
                    }
                });

        }
    }]);