angular.module("otb").controller("proposalCtrl",['$rootScope','$scope',proposalCtrl]);

function proposalCtrl($rootScope,$scope){
    var itinerary_item = {
        duration:{
            begin:undefined,
            end:undefined
        },
        scenery:"",
        transport:undefined,
        lodging:undefined,
        options:[

        ]
    };

    $scope.proposal = {
        title:"",
        itineraries:[
        ]
    };

    $scope.proposal.itineraries.push(angular.copy(itinerary_item));

    $scope.appendItinerary = function(){
        $scope.proposal.itineraries.push(angular.copy(itinerary_item));
    };

    $scope.spliceItinerary = function(index){
        $scope.proposal.itineraries.splice(index,1);
    }
}