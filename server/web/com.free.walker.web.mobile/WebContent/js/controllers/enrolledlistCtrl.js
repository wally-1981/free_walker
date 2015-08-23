angular.module("otb").factory('enrolledlistFactory', function(){
	
	var enrolledlist = {};
	
	enrolledlist.products = [];
	
	enrolledlist.products[0] = {};
	enrolledlist.products[0].title = "巴黎卢瓦尔河城堡8日";
	enrolledlist.products[0].currentPrice = "6500";
	enrolledlist.products[0].lastPrice = "7100";
	enrolledlist.products[0].dealClosed = true;
	enrolledlist.products[0].currentAttdNum = "8";
	enrolledlist.products[0].nextAttdNum = "12";
	enrolledlist.products[0].departDate = "2015-07-23";
	enrolledlist.products[0].returnDate = "2015-08-01";
	enrolledlist.products[0].status = "即将启程";
	
	enrolledlist.products[1] = {};
	enrolledlist.products[1].title = "新加坡吉隆坡游轮新年游";
	enrolledlist.products[1].currentPrice = "5500";
	enrolledlist.products[1].lastPrice = "6100";
	enrolledlist.products[1].dealClosed = false;
	enrolledlist.products[1].currentAttdNum = "122";
	enrolledlist.products[1].nextAttdNum = "150";
	enrolledlist.products[1].departDate = "2016-01-23";
	enrolledlist.products[1].returnDate = "2016-01-31";
	enrolledlist.products[1].status = "待评价";
	
	enrolledlist.products[2] = {};
	enrolledlist.products[2].title = "巴黎卢瓦尔河城堡8日";
	enrolledlist.products[2].currentPrice = "6500";
	enrolledlist.products[2].lastPrice = "7100";
	enrolledlist.products[2].dealClosed = true;
	enrolledlist.products[2].currentAttdNum = "8";
	enrolledlist.products[2].nextAttdNum = "12";
	enrolledlist.products[2].departDate = "2015-07-23";
	enrolledlist.products[2].returnDate = "2015-08-01";
	enrolledlist.products[2].status = "已评价";
	
	enrolledlist.products[3] = {};
	enrolledlist.products[3].title = "巴黎卢瓦尔河城堡8日";
	enrolledlist.products[3].currentPrice = "6500";
	enrolledlist.products[3].lastPrice = "7100";
	enrolledlist.products[3].dealClosed = true;
	enrolledlist.products[3].currentAttdNum = "8";
	enrolledlist.products[3].nextAttdNum = "12";
	enrolledlist.products[3].departDate = "2015-07-23";
	enrolledlist.products[3].returnDate = "2015-08-01";
	enrolledlist.products[3].status = "已评价";
	
	enrolledlist.products[4] = {};
	enrolledlist.products[4].title = "巴黎卢瓦尔河城堡8日";
	enrolledlist.products[4].currentPrice = "6500";
	enrolledlist.products[4].lastPrice = "7100";
	enrolledlist.products[4].dealClosed = true;
	enrolledlist.products[4].currentAttdNum = "8";
	enrolledlist.products[4].nextAttdNum = "12";
	enrolledlist.products[4].departDate = "2015-07-23";
	enrolledlist.products[4].returnDate = "2015-08-01";
	enrolledlist.products[4].status = "已评价";

	enrolledlist.products[5] = {};
	enrolledlist.products[5].title = "巴黎卢瓦尔河城堡8日";
	enrolledlist.products[5].currentPrice = "6500";
	enrolledlist.products[5].lastPrice = "7100";
	enrolledlist.products[5].dealClosed = true;
	enrolledlist.products[5].currentAttdNum = "8";
	enrolledlist.products[5].nextAttdNum = "12";
	enrolledlist.products[5].departDate = "2015-07-23";
	enrolledlist.products[5].returnDate = "2015-08-01";
	enrolledlist.products[5].status = "已评价";
	
    // initialize mylist content here
	
	var factory = {};
	factory.getEnrolledlist = function(){
		return enrolledlist;
	};
	
	return factory;
});

angular.module("otb").controller("enrolledlistCtrl",['$rootScope','$scope','$timeout',enrolledlistCtrl]);

function enrolledlistCtrl($rootScope,$scope,$timeout, enrolledlistFactory){
	$scope.showSub = false;
	$scope.enrolledlist = {};
	init();
	function init(){
	    $scope.enrolledlist = enrolledlistFactory.getEnrolledlist();
	}
}