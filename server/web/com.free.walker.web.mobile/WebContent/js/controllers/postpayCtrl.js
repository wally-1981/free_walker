angular.module("otb").factory('postpayFactory', function(){
	
	var postpay = {};
	postpay.title = "巴黎卢瓦尔河城堡8日";
    
    postpay.prices = [];
    postpay.prices[0] = {};
    postpay.prices[0].price = "9700";
    postpay.prices[0].attdNum = "3";
    
    postpay.departDate = "2015-07-23";
    postpay.returnDate = "2015-08-01";
    postpay.itineraries = [];
    postpay.itineraries[0] = {};
    postpay.itineraries[0].startDate = "2015-07-23";
    postpay.itineraries[0].endDate = "2015-07-23";
    postpay.itineraries[0].startWeekday = "星期四";
    postpay.itineraries[0].lodging = "巴黎四星酒店";
    postpay.itineraries[0].description = "首都国际机场乘坐航班飞往巴黎。提前4小时抵达机场办理乘机手续。富裕时间可以在机场免税店购物。当地时间同日下午抵达，入住酒店。晚餐后游览香榭丽舍大街、埃菲尔铁塔及塞纳河畔夜景。";
    postpay.itineraries[0].transports = [];
    postpay.itineraries[0].transports[0] = "plane";
    postpay.itineraries[0].transports[1] = "bus";

    postpay.itineraries[1] = {};
    postpay.itineraries[1].startDate = "2015-07-24";
    postpay.itineraries[1].endDate = "2015-07-24";
    postpay.itineraries[1].startWeekday = "星期五";
    postpay.itineraries[1].lodging = "香波当地三星酒店";
    postpay.itineraries[1].description = "前往卢瓦尔河城堡景区，参观最著名的香波堡，舍农索堡。";
    postpay.itineraries[1].transports = [];
    postpay.itineraries[1].transports[0] = "bus";
	
    postpay.options = [];
    postpay.options[0] = {};
    postpay.options[0].content = "升级豪华家庭套房";
    postpay.options[0].price = 2000;
    postpay.options[0].minNum = 0;
    postpay.options[0].maxNum = 1;
    postpay.options[0].opts = [];
    for (var i = postpay.options[0].minNum; i<= postpay.options[0].maxNum; i++){
    	postpay.options[0].opts[i] = {};
    	postpay.options[0].opts[i].id = i;
    	postpay.options[0].opts[i].value = i;
    }
    
    postpay.options[1] = {};
    postpay.options[1].content = "红磨坊歌舞表演";
    postpay.options[1].price = 240;
    postpay.options[1].minNum = 0;
    postpay.options[1].maxNum = 3;
    postpay.options[1].opts = [];
    for (var i = postpay.options[1].minNum; i<= postpay.options[1].maxNum; i++){
    	postpay.options[1].opts[i] = {};
    	postpay.options[1].opts[i].id = i;
    	postpay.options[1].opts[i].value = i;
    }
    
	var factory = {};
	factory.getpostpay = function(){
		return postpay;
	};
	
	return factory;
});

angular.module("otb").controller("postpayCtrl",['$rootScope','$scope','$timeout',postpayCtrl]);

function postpayCtrl($rootScope,$scope,$timeout, postpayFactory){
	$scope.showSubPage = false;
	$scope.pageIndex = 0;
	$scope.subPage = '';
	$scope.postpay = {};
	init();
	function init(){
	    $scope.postpay = postpayFactory.getpostpay();
	}

}

