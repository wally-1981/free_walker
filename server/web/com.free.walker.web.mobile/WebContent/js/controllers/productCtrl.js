angular.module("otb").factory('productFactory', function(){
	
	var product = {};
	product.title = "巴黎卢瓦尔河城堡8日";
    
    product.prices = [];
    product.prices[0] = {};
    product.prices[0].price = "9700";
    product.prices[0].attdNum = "6";
    product.prices[1] = {};
    product.prices[1].price = "8300";
    product.prices[1].attdNum = "12";
    product.prices[2] = {};
    product.prices[2].price = "7100";
    product.prices[2].attdNum = "18";
    
    product.departDate = "2015-07-23";
    product.returnDate = "2015-08-01";
    product.itineraries = [];
    product.itineraries[0] = {};
    product.itineraries[0].startDate = "2015-07-23";
    product.itineraries[0].endDate = "2015-07-23";
    product.itineraries[0].startWeekday = "星期四";
    product.itineraries[0].lodging = "巴黎四星酒店";
    product.itineraries[0].description = "首都国际机场乘坐航班飞往巴黎。提前4小时抵达机场办理乘机手续。富裕时间可以在机场免税店购物。当地时间同日下午抵达，入住酒店。晚餐后游览香榭丽舍大街、埃菲尔铁塔及塞纳河畔夜景。";
    product.itineraries[0].transports = [];
    product.itineraries[0].transports[0] = "plane";
    product.itineraries[0].transports[1] = "bus";

    product.itineraries[1] = {};
    product.itineraries[1].startDate = "2015-07-24";
    product.itineraries[1].endDate = "2015-07-24";
    product.itineraries[1].startWeekday = "星期五";
    product.itineraries[1].lodging = "巴黎五星酒店";
    product.itineraries[1].description = "首都国际机场乘坐航班飞往巴黎。提前4小时抵达机场办理乘机手续。富裕时间可以在机场免税店购物。当地时间同日下午抵达，入住酒店。晚餐后游览香榭丽舍大街、埃菲尔铁塔及塞纳河畔夜景。";
    product.itineraries[1].transports = [];
    product.itineraries[1].transports[0] = "bus";
	
    product.options = [];
    product.options[0] = {};
    product.options[0].content = "升级豪华家庭套房";
    product.options[0].price = 2000;
    product.options[0].quantity = 1;
    
    product.options[1] = {};
    product.options[1].content = "红磨坊歌舞表演";
    product.options[1].price = 240;
    product.options[1].quantity = 3;

    product.options[2] = {};
    product.options[2].content = "旅行意外险（赠送）";
    product.options[2].price = 50;
    product.options[2].quantity = 3;


    var factory = {};
	factory.getProduct = function(){
		return product;
	};
	
	return factory;
});

angular.module("otb").controller("productCtrl",['$rootScope','$scope','$timeout',productCtrl]);

function productCtrl($rootScope,$scope,$timeout, productFactory){
	$scope.showSub = false;
	$scope.pageIndex = 0;
	$scope.subPage = '';
	$scope.product = {};
	init();
	function init(){
	    $scope.product = productFactory.getProduct();
	}
	
	$scope.toggleItiDetail = function (index){
		if (document.getElementById("iti"+index).style.display == "none"){
			document.getElementById("iti"+index).style.display = "block";
		}else{
			document.getElementById("iti"+index).style.display = "none";
		}
	};
	
	$scope.toSubPage = function(subPage, event){
        if(subPage == 'payment'){
            
        }
        $scope.showSubPage = true;
        $scope.subPage = subPage;
    };
}

