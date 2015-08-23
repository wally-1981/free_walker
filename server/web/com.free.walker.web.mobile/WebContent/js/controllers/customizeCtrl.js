angular.module("otb").controller("customizeCtrl",['$rootScope','$scope','$timeout','$filter','$location','$anchorScroll','$window','City',customizeCtrl]);

function customizeCtrl($rootScope, $scope, $timeout, $filter,$location,$anchorScroll,$window,City){
    $scope.moment = $window.moment;
    $scope.isNaN = $window.isNaN;

    $scope.subPage = '';
    $scope.header='定制行程';
    $scope.domestic = true;

    $scope.today = moment().clone().startOf('day');
    $scope.datePickerParam = {
        current : $scope.today,
        from : $scope.today
    };

    $scope.itinerary = {
        starting : {
            cityCode : undefined,
            cityName : '定位中'
        },
        returnTo : {
            cityCode : undefined,
            cityName : '定位中'
        },
        destinations   : [],
        from : $scope.datePickerParam.current.format('YYYY.MM.DD'),
        to   : $scope.datePickerParam.current.format('YYYY.MM.DD'),
        tourists : 1,
        preferences:''
    };



    City.getCities(
        {isDomestic:'N'},
        function(data){
            $scope.domesticCities =data;
            if($scope.domestic)
                $scope.citiesCollections = $scope.domesticCities;
        }
    );

    City.getCities(
        {isHottest:'Y'},
        function(data){
            $scope.interCities =data;
            if(!$scope.domestic)
                $scope.citiesCollections = $scope.interCities;
        }
    );

    $scope.hotCities = [
        {
            cityCode: 'h1',
            cityName: '塞班岛'
        },
        {
            cityCode: 'h2',
            cityName: '巴黎'
        },
        {
            cityCode: 'h3',
            cityName: '柏林'
        },
        {
            cityCode: 'h4',
            cityName: '伦敦'
        },
        {
            cityCode: 'h5',
            cityName: '佛罗伦萨'
        },
        {
            cityCode: 'h6',
            cityName: '米兰'
        },
        {
            cityCode: 'h7',
            cityName: '威尼斯'
        },
        {
            cityCode: 'h8',
            cityName: '墨尔本'
        },
        {
            cityCode: 'h9',
            cityName: '马尔代夫'
        }
    ];

    function retrieveSuccess(position){
        var BGeo = new BMap.Geocoder();
        BGeo.getLocation(new BMap.Point(position.coords.longitude,position.coords.latitude),
            function(location){
                $scope.$apply(function(){
                    var currentCityName = location.addressComponents.city;
                    currentCityName = currentCityName.substring(0,currentCityName.length-1);
                    for(var i in $scope.citiesCollections){
                        var cities = $scope.citiesCollections[i].cities;
                        for(var j in cities)
                            if(cities[j].chinese_name == currentCityName){
                                $scope.itinerary.starting = $scope.itinerary.returnTo = cities[j];
                                var found = true;
                                break;
                            }
                        if(found)
                            break;
                    }
                })
            }
        )
    }

    var geo = navigator.geolocation;
    if(geo) {
        geo.getCurrentPosition(retrieveSuccess);
    };

    $scope.toSubPage = function(subPage, event){

        if(subPage == 'citySelector'){
            $scope.multiple = $(event.currentTarget).attr("data-multiple") == 'true';
            $scope.targetNode = $(event.currentTarget).attr("data-node");
            $scope.subPage = 'pages/citySelector.html';
            $scope.header = '选择城市';
        }else if(subPage == 'datePicker'){
            $scope.isFrom = $(event.currentTarget).attr("data-ng-model") == 'itinerary.from';
            if($scope.isFrom){
                $scope.datePickerParam.from = $scope.today;
                $scope.datePickerParam.current = moment($scope.itinerary.from,'YYYY.MM.DD').startOf('day');
            }else{
                $scope.datePickerParam.from = moment($scope.itinerary.from,'YYYY.MM.DD').startOf('day');
                $scope.datePickerParam.current = moment($scope.itinerary.to,'YYYY.MM.DD').startOf('day');
            }
            $scope.months = generateMonths($scope.datePickerParam.from.format('YYYY.MM.DD'),6);
            $scope.subPage = 'pages/datePicker.html';
            $scope.header = '选择日期';
        }
    };

    $scope.backward = function(){
        $scope.subPage = '';
        $scope.header = '定制需求';
    }

    $scope.selectDestination = function(city, isHotCity){
        if($scope.multiple || isHotCity){
            var index = $scope.itinerary.destinations.indexOf(city);
            if (index < 0)
                $scope.itinerary.destinations.push(city);
            else if(!isHotCity)
                $scope.itinerary.destinations.splice(index,1);
        }else{
            if(typeof $scope.targetNode == 'string'){
                $scope.temp_city = city;
                $scope.$eval($scope.targetNode + '= temp_city;');
                delete $scope.temp_city;
            }
            $scope.showSubPage = false;
        }
    };

    $scope.removeDestination = function(destination){
        var index = $scope.itinerary.destinations.indexOf(destination);
        $scope.itinerary.destinations.splice(index,1);
    };

    $scope.chooseRegion = function(domestic){
        $scope.domestic = domestic;
        if($scope.domestic)
            $scope.citiesCollections = $scope.domesticCities;
        else
            $scope.citiesCollections = $scope.interCities;
    }

    $scope.pickDate = function(day){
        if(moment(day,'YYYY.MM.DD').isBefore($scope.datePickerParam.from))
            return;
        else{
            if($scope.isFrom){
                $scope.itinerary.from = day;
                if(moment($scope.itinerary.to,'YYYY.MM.DD').isBefore(moment($scope.itinerary.from,'YYYY.MM.DD')))
                    $scope.itinerary.to = day;
            }
            else
                $scope.itinerary.to = day;
            $scope.datePickerParam.current = moment(day,'YYYY.MM.DD').startOf('day');
        }
    }

    $scope.scrollTo = function(hash){
        var old = $location.hash();
        $location.hash(hash);
        $anchorScroll();
        $location.hash(old);
    };

    function generateMonths(from, amount){
        var current_1st = moment(from,'YYYY.MM.DD').startOf('month').clone();
        var months = [];
        for(var i=0; i< amount; i++){
            var item = {
                year: current_1st.year(),
                month: current_1st.month(),
            };
            var next_1st = current_1st.clone().add(1,'M');
            var daysInMonth = next_1st.diff(current_1st,'days');
            var dayOfWeek = current_1st.day();
            var days = [];
            for(var m=0; m< dayOfWeek; m++){
                days.push(m);
            }

            for(var n=0; n<daysInMonth; n++){
                days.push(current_1st.clone().add(n,'d').format('YYYY.MM.DD'));
            }
            item.days = days;

            months.push(item);
            current_1st = next_1st;
        }

        return months;
    };

    $scope.$watch('itinerary.destinations.length',function(newValue){
        if( newValue > 0)
            $scope.destinationSelected = true;
        else
            $scope.destinationSelected = false;
    });

    $scope.saveProposal = function(){
        $location.search("awaitPublish",'true');
        $location.path("/requirement");
    };
}