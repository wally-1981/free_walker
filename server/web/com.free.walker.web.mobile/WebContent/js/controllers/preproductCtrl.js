angular.module("otb").factory('preproductFactory', function(){
	
	var preproduct = {};
	preproduct.title = "巴黎卢瓦尔河城堡8日";
    
    preproduct.prices = [];
    preproduct.prices[0] = {};
    preproduct.prices[0].price = "9700";
    preproduct.prices[0].attdNum = "0";
    
    preproduct.departDate = "2015-07-23";
    preproduct.returnDate = "2015-08-01";
    preproduct.itineraries = [];
    preproduct.itineraries[0] = {};
    preproduct.itineraries[0].startDate = "2015-07-23";
    preproduct.itineraries[0].endDate = "2015-07-23";
    preproduct.itineraries[0].startWeekday = "星期四";
    preproduct.itineraries[0].lodging = "巴黎四星酒店";
    preproduct.itineraries[0].description = "首都国际机场乘坐航班飞往巴黎。提前4小时抵达机场办理乘机手续。富裕时间可以在机场免税店购物。当地时间同日下午抵达，入住酒店。晚餐后游览香榭丽舍大街、埃菲尔铁塔及塞纳河畔夜景。";
    preproduct.itineraries[0].transports = [];
    preproduct.itineraries[0].transports[0] = "plane";
    preproduct.itineraries[0].transports[1] = "bus";

    preproduct.itineraries[1] = {};
    preproduct.itineraries[1].startDate = "2015-07-24";
    preproduct.itineraries[1].endDate = "2015-07-24";
    preproduct.itineraries[1].startWeekday = "星期五";
    preproduct.itineraries[1].lodging = "香波当地三星酒店";
    preproduct.itineraries[1].description = "前往卢瓦尔河城堡景区，参观最著名的香波堡，舍农索堡。";
    preproduct.itineraries[1].transports = [];
    preproduct.itineraries[1].transports[0] = "bus";
	
    preproduct.options = [];
    preproduct.options[0] = {};
    preproduct.options[0].content = "升级豪华家庭套房";
    preproduct.options[0].price = 2000;
    preproduct.options[0].minNum = 0;
    preproduct.options[0].maxNum = 1;
    preproduct.options[0].opts = [];
    for (var i = preproduct.options[0].minNum; i<= preproduct.options[0].maxNum; i++){
    	preproduct.options[0].opts[i] = {};
    	preproduct.options[0].opts[i].id = i;
    	preproduct.options[0].opts[i].value = i;
    }
    
    preproduct.options[1] = {};
    preproduct.options[1].content = "红磨坊歌舞表演";
    preproduct.options[1].price = 240;
    preproduct.options[1].minNum = 0;
    preproduct.options[1].maxNum = 3;
    preproduct.options[1].opts = [];
    for (var i = preproduct.options[1].minNum; i<= preproduct.options[1].maxNum; i++){
    	preproduct.options[1].opts[i] = {};
    	preproduct.options[1].opts[i].id = i;
    	preproduct.options[1].opts[i].value = i;
    }
    
	var factory = {};
	factory.getpreproduct = function(){
		return preproduct;
	};
	
	return factory;
});

angular.module("otb").controller("preproductCtrl",['$rootScope','$scope','$timeout',preproductCtrl]);

function preproductCtrl($rootScope, $scope, $timeout, $location, preproductFactory){
	$scope.showSubPage = false;
	$scope.pageIndex = 0;
	$scope.subPage = '';
	$scope.preproduct = {};
    $scope.btnName = '';
    $scope.checkedOptions = [
        {
            content: '红磨坊歌舞表演',
            price:240,
            quantity:1
        },
        {
            content: '升级豪华家庭套房',
            price:888,
            quantity:1
        }
    ];
    var type = $location.search().type;
    delete $location.search().type;
    switch(type){
        case 'enrolling':
            $scope.btnName = '算我一个';
            break;
        case 'adoptiong':
            $scope.btnName = '采纳';
            break;
    }
	init();
	function init(){
	    $scope.preproduct = preproductFactory.getpreproduct();
	}

    $scope.decrease = function(option){
        if(option.quantity>0){
            option.quantity--;
            calculateFee();
        }
    }
    $scope.increase = function(option){
        option.quantity++;
        calculateFee();
    }

    function calculateFee(){
        $scope.totalFee = 0;
        $scope.checkedOptions.forEach(function(option){
            $scope.totalFee+=option.price*option.quantity;
        })
    }

    $scope.toggleCheckStatus = function($event){
        var target = $event.currentTarget;
        var iEle = $("i",target).eq(0);
        iEle.toggleClass("fa-square-o fa-check-square-o checked");
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
            calculateFee();
        }
        $scope.showSubPage = true;
        $scope.subPage = subPage;
    };
}

