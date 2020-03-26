/**
 * Created by yanglu on 15/11/16.
 */

var app = angular.module('OrdersBackendApp', ['ngGrid', 'angularFileUpload', 'fundoo.services']);
var cellEditableTemplate = "<input ng-class=\"'colt' + col.index\" ng-input=\"COL_FIELD\" ng-model=\"COL_FIELD\" ng-blur=\"updateEntity(col, row)\"/>";

app.filter('safehtml', function($sce) {
    return function(htmlString) {
        return $sce.trustAsHtml(htmlString);
    }
});

app.controller('OrdersBackendController', ['$scope', '$http', '$upload', 'createDialog', '$log', function ($scope, $http, $upload, createDialogService, $log) {
    $scope.teststr = 'hello'

    $scope.newProduct = {}
    $scope.productList = []
    $scope.page = 1;

    $scope.$watch('page', function(){
        refreshDate();
    }, false);

    $scope.goNextPage = function() {
        $scope.page = $scope.pageInfo.current +1;
    }

    $scope.goPrevPage = function() {
        $scope.page = $scope.pageInfo.current -1;
    }

    function refreshDate(){
//        var type, area;
//        if (!$scope.articleType)
//            type = -1;
//        else
//            type = $scope.articleType;
//
//        if (!$scope.articleArea) {
//            area = null;
//        }
//        else {
//            if ($scope.articleArea.name == '')
//                area = 'empty';
//            else
//                area = $scope.articleArea.name;
//        }

        $http.get('/products?page=' + $scope.page).success(function (data, status, headers, config) {
        	/*$log.log(data)*/
            if (data.flag) {
                $scope.productList = data.data;
                $scope.pageInfo = data.page;
            }
            else {
                alert(data.message);
            }
        });
    }
    
    $scope.gridOptions = { data: 'productList',
        rowHeight: 30,
        showSelectionCheckbox:true,
        enableCellSelection: true,
        enableRowSelection: true,
        selectedItems: [],
        multiSelect:true,
        enableCellEdit: false,
        plugins:[new ngGridFlexibleHeightPlugin()],
        columnDefs: [
//            {field: 'id', displayName: 'Id', enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
            {field: 'name', displayName: '公告标题', enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
            {field: 'alcoholDegree', displayName: '公告标题', enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
//            {field: 'content', displayName: '内容', width: 40, enableCellEdit: false, cellTemplate: '<div class="ngCellText" ng-class="col.colIndex()"><a href="\\articles\\details\\{{row.getProperty(\'id\')}}" target="_blank">查看</a></div>'},
//            {field: 'publicTime', displayName: '发布时间', width: 90, cellFilter: 'date:\"yyyy-MM-dd HH:mm:ss\"', enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
//            {field: 'type', displayName: '公告类型', width: 150, enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
//            {field: 'area', displayName: '区域', width: 60, enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
//            {field: 'productCatalog', displayName: '品目', width: 100, enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
//            {field: 'industry', displayName: '行业', width: 100, enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
//            {field: 'buyerName', displayName: '采购人', width: 100, enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
//            {field: 'url', displayName: 'Url', width: 40, enableCellEdit: false, cellTemplate: '<div class="ngCellText" ng-class="col.colIndex()"><a href="{{row.getProperty(col.field)}}" target="_blank">原站</a></div>'},
//            {field: 'hasAttachment', displayName: '附件', width: 50, enableCellEdit: false, editableCellTemplate: cellEditableTemplate},
//            {field: 'buyerName', displayName: '列表圖片', enableCellEdit: false, cellTemplate: uploadTemplate, width: '**'},
//            {field:'bigPic', displayName:'詳情圖片', enableCellEdit: false, cellTemplate: uploadTemplate, width: '**'},
//            {field:'tinypic', displayName:'圖片', enableCellEdit: false, cellTemplate: uploadTemplate, width: '**'}
        ] };

    $scope.deleteProduct = function (indexNo) {
        $http.delete('/products/' + $scope.productList[indexNo].id).success(function (data, status, headers, config) {
            if (data.flag) {
                $scope.productList.splice(indexNo, 1)
            }
            else {
                alert(data.message);
            }
        });
    };

    $scope.updateProduct = function (indexNo) {
        $http({method: 'PUT', url: '/products/' + $scope.productList[indexNo].id, data: $scope.productList[indexNo]}).success(function (data, status, headers, config) {
            if (data.flag) {
                //$scope.productList.splice(indexNo, 1)
            }
            else {
                alert(data.message);
            }
        });
    };

    $scope.addProduct = function () {
        $http({method: 'POST', url: '/products', data: $scope.newProduct}).success(function (data, status, headers, config) {
            if (data.flag) {
                $scope.productList.push(data.data)
                $scope.newProduct = {}
            } else {
                alert(data.message)
            }
        });
    };
}]);
