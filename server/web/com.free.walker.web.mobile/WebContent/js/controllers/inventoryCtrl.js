angular.module("otb").controller("summaryCtrl",['$rootScope','$scope','$location',inventoryCtrl]);

function inventoryCtrl($rootScope, $scope, $location){
    $scope.completions = [
        {
            title : 'xxxxx',
            quantity: '25',
            turnover: 234320,
            rating: 8.9
        },
        {
            title : 'xxxxx',
            quantity: '25',
            turnover: 234320,
            rating: 8.9
        },
        {
            title : 'xxxxx',
            quantity: '25',
            turnover: 234320,
            rating: 8.9
        },
        {
            title : 'xxxxx',
            quantity: '25',
            turnover: 234320,
            rating: 8.9
        }
    ];

    $scope.requirements = [
        {
            srcCity:"北京",
            destCities:["巴黎", "罗马"],
            adultNum : "2",
            childNum : "1",
            departDate : "2015-07-23",
            returnDate : "2015-08-01",
            bufferBefore : "3",
            bufferAfter : "3",
            proposalNum : "2"
        },
        {
            srcCity:"北京",
            destCities:["新加坡"],
            adultNum : "2",
            childNum : "1",
            departDate : "2015-12-22",
            returnDate : "2016-01-03",
            bufferBefore : "0",
            bufferAfter : "4",
            proposalNum : "2"
        }
    ];

    $scope.products = [
        {

        }
    ];

    $scope.viewRequirement = function(){
        $location.search('isAgentView','1');
        $scope.switchView('requirement');
    }

    var activeMenu = 0;

    angular.element("ul.categories > li").eq(activeMenu).addClass("active");
}