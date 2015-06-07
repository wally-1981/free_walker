:: Please ensure install curl, http://curl.haxx.se, before executing this setup script.
::
:: This script will setup the foundation data for elasticsearch server.
echo "Please ensure install curl, http://curl.haxx.se, before executing this setup script. Otherwise, its execution will fail."
curl -X POST -d @query_template/test_template.mustache http://localhost:9200/_search/template/test_template --header "content-type:application/json"
curl -X POST -d @query_template/product_departure.mustache http://localhost:9200/_search/template/product_departure --header "content-type:application/json"
curl -X POST -d @query_template/product_destination.mustache http://localhost:9200/_search/template/product_destination --header "content-type:application/json"
curl -X POST -d @query_template/product_owner.mustache http://localhost:9200/_search/template/product_owner --header "content-type:application/json"
curl -X POST -d @query_template/proposal_owner.mustache http://localhost:9200/_search/template/proposal_owner --header "content-type:application/json"