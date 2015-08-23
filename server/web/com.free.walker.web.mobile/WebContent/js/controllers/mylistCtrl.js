angular.module("otb").factory('mylistFactory', function(){
	
	var mylist = {};
	
    // initialize mylist content here
	
	var factory = {};
	factory.getMylist = function(){
		return mylist;
	};
	
	return factory;
});

angular.module("otb").controller("mylistCtrl",['$rootScope','$scope','$timeout',mylistCtrl]);

function mylistCtrl($rootScope,$scope,$timeout, mylistFactory){
	$scope.showSub = false;
	$scope.mylist = {};
	init();
	function init(){
	    $scope.mylist = mylistFactory.getMylist();
	}
}