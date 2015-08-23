angular.module("otb").factory('requirementFactotry', ['$rootScope',function($rootScope){
	
	var requirement = {};
		
	requirement = {};
	requirement.isPublished = $rootScope.reqPublished? true : false;
	requirement.srcCity="北京";
	requirement.returnTo="北京";
	requirement.destCities=[];
	requirement.destCities[0] = "巴黎";
	requirement.destCities[1] = "罗马";
	requirement.destCities[2] = "佛罗伦萨";
	requirement.destCities[3] = "威尼斯";
	requirement.adultNum = "2";
	requirement.childNum = "1";
	requirement.departDate = "2015-07-23";
	requirement.returnDate = "2015-08-01";
	requirement.bufferBefore = "3";
	requirement.bufferAfter = "3";
	requirement.extra = "四个城市必须都有至少一个整天的游览时间；威尼斯必须游览murano和burano岛"
	requirement.proposalNum = "2";
	
	requirement.proposals = [];
	requirement.proposals[0] = {};
	requirement.proposals[0].title = "私人定制：法意五城十二日游";
	requirement.proposals[0].currentPrice = "17900";
    requirement.proposals[0].lastPrice = "17900";
    requirement.proposals[0].currentAttdNum = "0";
    requirement.proposals[0].nextAttdNum = "6";
    requirement.proposals[0].departDate = "2015-07-23";
    requirement.proposals[0].returnDate = "2015-08-04";
	
	requirement.proposals[1] = {};
	requirement.proposals[1].title = "私人定制：法意四城十一日游";
	requirement.proposals[1].currentPrice = "16900";
    requirement.proposals[1].lastPrice = "16900";
    requirement.proposals[1].currentAttdNum = "0";
    requirement.proposals[1].nextAttdNum = "6";
    requirement.proposals[1].departDate = "2015-07-23";
    requirement.proposals[1].returnDate = "2015-08-03";
	
    // initialize mylist content here
	
	var factory = {};
	factory.getRequirement = function(){
		return requirement;
	};
	
	return factory;
}]);

angular.module("otb").controller("requirementCtrl",['$rootScope','$scope','$location',requirementCtrl]);

function requirementCtrl($rootScope, $scope, $location, requirementFactotry){
	$scope.showSub = false;
	$scope.isAgentView = !!$location.search().isAgentView;;
	$scope.requirement = {};
	init();
	function init(){
	    $scope.requirement = requirementFactotry.getRequirement();
		$scope.requirement.awaitPublish = !!$location.search().awaitPublish;
	}
	$scope.publishRequirement = function(){
		$location.search("awaitPublish",null);
		$location.path('/reqList');
	}
}

