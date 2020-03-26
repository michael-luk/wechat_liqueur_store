/**
 * Created by yanglu on 15/11/16.
 */

var app = angular.module('UserBackendApp', ['ngGrid', 'angularFileUpload', 'fundoo.services']);
var cellEditableTemplate = "<input ng-class=\"'colt' + col.index\" ng-input=\"COL_FIELD\" ng-model=\"COL_FIELD\" ng-blur=\"updateEntity(col, row)\"/>";

app.filter('safehtml', function($sce) {
    return function(htmlString) {
        return $sce.trustAsHtml(htmlString);
    }
});

app.controller('UserBackendController', ['$scope', '$http', '$upload', 'createDialog', '$log', function ($scope, $http, $upload, createDialogService, $log) {
    $scope.list = []
    $scope.currentObj = {}
    $scope.page = 1;
    $scope.pageInfo = {}

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
        $http.get('/users?page=' + $scope.page).success(function (data, status, headers, config) {
        /*	$log.log(data)*/
            if (data.flag) {
                $scope.list = data.data;
                $scope.pageInfo = data.page;
            }
            else {
            	bootbox.alert(data.message);
            }
        });
    }
    
    $scope.gridOptions = { data: 'list',
    		 rowHeight: 85,
    		 // showSelectionCheckbox:true,
    		 // enableCellSelection: false,
             enableRowSelection: true,
             selectedItems: [],
             multiSelect:false,
             // enableCellEdit: false,
        plugins:[new ngGridFlexibleHeightPlugin()],
        columnDefs: [
            {field: 'id', displayName: 'Id', width: '40'},
            {field: 'resellerCode', displayName: '分销码', width: '150'},
            {field: 'nickname', displayName: '用户名', width: '120', cellTemplate: '<div ng-bind-html="COL_FIELD | safehtml"></div>'},
            {field: 'headImgUrl', displayName: '头像', width: '85', cellTemplate: '<div ng-show="COL_FIELD"><img src="{{COL_FIELD}}" style="width:80px;height:80px" ></div>'},
            {field: 'createdAtStr', displayName: '注册/关注时间', width: '150'},
            {field: 'jifen', displayName: '用户积分', width: '80', enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
            {field: 'isReseller', displayName: '分销许可', width: '80', cellTemplate: '<div><input type="checkbox" ng-model="COL_FIELD" ng-click="updateReseller(row.entity)" /></div>'},
            {field: 'wxOpenId', displayName: '微信ID', width: '150', cellTemplate: '<div ng-bind-html="COL_FIELD | safehtml"></div>'},
            {field: 'resellerCodeImage', displayName: '分销二维码', width: '85', cellTemplate: '<div><a class="fancybox" ng-href="/showimg/barcode/{{COL_FIELD}}"><img ng-src="/showimg/barcode/{{COL_FIELD}}" style="width:80px;height:80px" ></a></div>'},
			{field: 'refUplineUserId', displayName: '上线用户ID'},
			{field: 'refUplineUserName', displayName: '上线用户名'},
			{field: 'refUplineUserHeadImgUrl', displayName: '上线头像', width: '85', cellTemplate: '<div ng-show="row.entity.refUplineUserId && row.entity.refUplineUserId > 0 && COL_FIELD"><img ng-src="{{COL_FIELD}}" style="width:80px;height:80px" ></div>'},
            {field: 'phone', displayName: '联系电话'},
            {field: 'descriptions', displayName: '备注', width: '80', enableCellEdit: true, editableCellTemplate: cellEditableTemplate},
            {field: 'userStatus', displayName: '用户管理', width: '220', cellTemplate: '<div><input type="radio" ng-model="COL_FIELD" value="0"/>正常&nbsp;<input type="radio" ng-model="COL_FIELD" value="1"/>冻结&nbsp;<input type="radio" ng-model="COL_FIELD" value="2"/>删除 &nbsp;<input type="button" value="提交" ng-click="updateUserStatus(row.entity)" /></div>'},
        ] };

    $scope.updateUserStatus = function(obj) {
        $scope.currentObj = obj;
        $scope.saveContent();
	};

    // 当前行更新字段
    $scope.updateEntity = function(column, row) {
        $scope.currentObj = row.entity;
        $scope.saveContent('/users/' + $scope.currentObj.id);
    };
    
 // 搜索
    $scope.findContent = function(){
    	var url = '/users?page=' + $scope.page
    	if($scope.findUser != null) url += '&keyword=' + $scope.findUser
    	
        $http.get(url).success(function (data, status, headers, config) {
        	$log.log(data)
            if (data.flag) {
                $scope.list = data.data;
                $scope.pageInfo = data.page;
            }
            else {
            	$scope.list = []
            	bootbox.alert(data.message)
            }
        });
    }
    
    // 分销许可
	$scope.updateReseller = function(obj) {
		obj.isReseller = !obj.isReseller
        $scope.currentObj = obj;
        $scope.saveContent('/users/' + $scope.currentObj.id);
	};
	
    // 新建或更新对象
    $scope.saveContent = function(url) {
        var content = $scope.currentObj;
        var isNew = !content.id
        if(isNew){
        	var http_method = "POST";
        	url = '/users'
        }else{
        	var http_method = "PUT";
            if(!url){
            	url = '/users/' + content.id + '/status/' + content.userStatus;
            }
            var pos = $scope.list.indexOf(content);
        }
        $http({method: http_method, url: url, data:content}).success(function(data, status, headers, config) {
                if(data.flag){
                    if(isNew){
                        $scope.list.push(data.data);
                        bootbox.alert('新建[' + data.data.loginName + ']成功');
                    }else{
                        $scope.list[pos] = data.data;
                    }
                }else{
                    bootbox.alert(data.message);
                }
            });
    };

    $scope.addContent = function(){
        $scope.currentObj = {};
        createDialogService("/assets/js/controllers/user_editortemplate.html",{
            id: 'editor',
            title: '新增用户',
            success:{
                label: '确定',
                fn: $scope.saveContent
            },
            scope: $scope
        });
    };

}]);
