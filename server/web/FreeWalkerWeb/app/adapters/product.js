import Ember from 'ember';
import ApplicationAdapter from './application';

export default ApplicationAdapter.extend({
    namespace: '/service/product',

    findRecord: function() {
    },

    createRecord: function() {
    },

    updateRecord: function() {
    },

    deleteRecord: function() {
    },

    findAll: function() {
    },

    query: function(store, type, query) {
        var url = this.host + this.namespace + '/products';
        var method = this.methods.PUT;
        var data = JSON.stringify(query);
        var dataType = this.data_type;
        var contentType = this.headers.content_type;
        return new Ember.RSVP.Promise(function(resolve, reject) {
            Ember.$.ajax({
                url: url,
                method: method,
                contentType: contentType,
                dataType: dataType,
                data: data
            }).then(function(data) {
                Ember.run(null, resolve, data);
            }, function(jqXHR) {
                jqXHR.then = null;
                Ember.run(null, reject, jqXHR);
            });
        });
    }
});
