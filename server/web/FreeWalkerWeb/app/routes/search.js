import Ember from 'ember';

export default Ember.Route.extend({
    model() {
        return this.store.query('product', {
            term: '*',
            template: 1,
            pageNum: 0,
            pageSize: 10
        });
    }
});
