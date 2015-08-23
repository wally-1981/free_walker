angular.module("otb").controller("mainCtrl",['$rootScope','$scope','$location','$animate',mainCtrl]);

function mainCtrl($rootScope,$scope,$location){
    var viewProjection = {
        recommend: 'recommend',
        customize: 'customize',
        myList: 'myList',
        enrolledList: 'myList',
        product: 'myList',
        reqList: 'myList',
        requirement: 'myList',
        preProduct: 'myList',
        postPay: 'myList'
    }

    $scope.view = viewProjection[$location.path().replace("/","")];

    $scope.switchView = function(view){
        $location.path("/"+view);
        $scope.view = viewProjection[view];
    }
}