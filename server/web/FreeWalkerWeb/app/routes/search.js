import Ember from 'ember';

export default Ember.Route.extend({
    model() {
        //return this.store.query('product', { criteria: '*' });
        return [
            {
                title: 'AAA',
                description: 'This is a product description for AAA, which could be a somewhat long statement.',
                capacity: 100
            },
            {
                title: 'BBB',
                description: '这是产品BBB的描述，有可能是一个很长的字符串。',
                capacity: 99
            }
        ];
    }
});
