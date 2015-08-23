angular.module("otb").factory('reqlistFactotry', function(){
	
	var reqlist = {};
	
	reqlist.requirements = [
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
	
    // initialize mylist content here
	
	var factory = {};
	factory.getReqlist = function(){
		return reqlist;
	};
	
	return factory;
});

angular.module("otb").controller("reqlistCtrl",['$rootScope','$scope','$timeout',reqlistCtrl]);

function reqlistCtrl($rootScope,$scope,$timeout, reqlistFactotry){
	$scope.showSub = false;
	$scope.reqlist = {};
	init();
	function init(){
	    $scope.reqlist = reqlistFactotry.getReqlist();
	}
}