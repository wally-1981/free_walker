angular.module("otb").controller("summaryCtrl",['$rootScope','$scope',summaryCtrl]);

function summaryCtrl($rootScope,$scope){
    $scope.product = {
        options:[
            {
                content : "升级豪华家庭套房",
                price : 2000,
                quantity : 3,
                members:[
                    {
                        name:'张三',
                        identifier:'110210188836546213215',
                        quantity: 2
                    },
                    {
                        name:'李四',
                        identifier:'110210188836546213215',
                        quantity: 1
                    }
                ]
            },
            {
                content : "红磨坊歌舞表演",
                price : 2400,
                quantity : 8,
                members:[
                    {
                        name:'张三',
                        identifier:'110210188836546213215',
                        quantity: 2
                    },
                    {
                        name:'李四',
                        identifier:'110210188836546213215',
                        quantity: 2
                    },
                    {
                        name:'王五',
                        identifier:'110210188836546213215',
                        quantity: 2
                    },
                    {
                        name:'赵六',
                        identifier:'110210188836546213215',
                        quantity: 2
                    }
                ]
            },
            {
                content : "旅行意外险（赠送）",
                price : 0,
                quantity : 8,
                members:[
                    {
                        name:'张三',
                        identifier:'110110188836546113115',
                        quantity: 1
                    },{
                        name:'张三',
                        identifier:'110110188836546113115',
                        quantity: 1
                    },{
                        name:'张三',
                        identifier:'110110188836546113115',
                        quantity: 1
                    },{
                        name:'张三',
                        identifier:'110110188836546113115',
                        quantity: 1
                    },{
                        name:'张三',
                        identifier:'110110188836546113115',
                        quantity: 1
                    },{
                        name:'张三',
                        identifier:'110110188836546113115',
                        quantity: 1
                    },{
                        name:'张三',
                        identifier:'110110188836546113115',
                        quantity: 1
                    },{
                        name:'张三',
                        identifier:'110110188836546113115',
                        quantity: 1
                    }
                ]
            }
        ]
    };

    $scope.product.options.activeOption = -1;
}
