(function(){
    'use strict'
    angular.module('otb').factory('City',['$resource',City]);
    function City($resource){
        return $resource(basicConfig.baseURL+'platform/cities',{},{
            getCities:{
                method: 'GET',
                isArray: true,
                withCredentials: true
            }
        });
    }
})();