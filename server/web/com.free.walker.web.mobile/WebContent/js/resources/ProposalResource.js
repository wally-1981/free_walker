angular.module('otb').factory('Proposal',['$resource',Proposal]);

function Proposal($resource){

    function transformDate(data){

    }

    return $resource(
        config.baseURL+"/",
        {},
        {
            submitProposal:{
                method:'POST',
                params:{

                },
                isArray: false,
                withCredentials: true,
                transformRequest: transformDate
            }
        }
    )
}