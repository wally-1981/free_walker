(
    function() {
        'use strict';
        var otb = angular.module('otb',['ngRoute','ngAnimate','ngTouch','ui.bootstrap','mgcrea.ngStrap.datepicker','mgcrea.ngStrap.collapse','mgcrea.ngStrap.tooltip','ngResource']);

        function otbConfig($routeProvider){
            $routeProvider.
                when('/recommend',{
                    templateUrl : "pages/recommend.html",
                    controller  : recommendCtrl
                }).
                when('/customize',{
                    templateUrl : "pages/customize.html",
                    controller  : customizeCtrl
                }).
                when('/product',{
                    templateUrl : "pages/product.html",
                    controller  : productCtrl
                }).
                when('/myList',{
                    templateUrl : "pages/mylist.html",
                    controller  : mylistCtrl
                }).
                when('/enrolledList',{
                    templateUrl : "pages/enrolledlist.html",
                    controller  : enrolledlistCtrl
                }).
                when('/reqList',{
                    templateUrl : "pages/reqlist.html",
                    controller  : reqlistCtrl
                }).
                when('/requirement',{
                    templateUrl : "pages/requirement.html",
                    controller  : requirementCtrl
                }).
                when('/preProduct',{
                    templateUrl : "pages/preproduct.html",
                    controller  : preproductCtrl
                }).
                when('/postPay',{
                    templateUrl : "pages/postpay.html",
                    controller  : postpayCtrl
                }).
                when('/teamSummary',{
                    templateUrl : "pages/teamSummary.html",
                    controller  : summaryCtrl
                }).
                when('/agentInventory',{
                    templateUrl : "pages/agentInventory.html",
                    controller  : inventoryCtrl
                }).
                otherwise({
                    redirectTo:'/recommend'
                });
        };
        otb.config(['$routeProvider',otbConfig]);

        function otbRun($rootScope,$anchorScroll){
            $rootScope.$on('$routeChangeSuccess',function(event, next, current){

                }
            );
            $anchorScroll.yOffset = 69;
        }

        otb.run(['$rootScope','$anchorScroll',otbRun]);
    }
)();