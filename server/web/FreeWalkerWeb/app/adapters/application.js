import DS from 'ember-data';

export default DS.Adapter.extend({
    host: 'http://localhost:9000',

    data_type: 'json',

    headers: {
        content_type: 'application/json; charset=UTF-8'
    },

    methods: {
        GET: 'GET',
        POST: 'POST',
        PUT: 'PUT',
        DELETE: 'DELETE'
    },
});
