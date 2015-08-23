angular.module('otb').controller('recommendCtrl',['$rootScope','$scope',recommendCtrl]);

function recommendCtrl($rootScope,$scope){
    $scope.search_placeHolder = '您想去哪儿玩？';
    $scope.citySlector = 'pages/citySelector.html';
    $scope.domestic = true;

    $scope.searchShown = false;
    $scope.showSearch = function(state){
        $scope.searchShown = state;
    }

    $scope.filter = {
        destination: '',
        from: undefined,
        to: undefined
    }

    $scope.carousel = {
        interval : 3000,
        slides : [
            {
                image : "images/slide1.jpg",
            },
            {
                image : "images/slide2.jpg",
            }
        ]
    };

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
}