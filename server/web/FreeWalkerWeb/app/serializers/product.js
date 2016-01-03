import DS from 'ember-data';

export default DS.JSONAPISerializer.extend({
    normalizeResponse(store, primaryModelClass, payload, id, requestType) {
        var normalizedPayload = {
            meta: {},
            data: [],
            included: []
        };
        normalizedPayload.meta.total_hits_number = payload.total_hits_number;
        normalizedPayload.data = payload.hits.map(function(product) {
            var normalizedProduct = {};
            normalizedProduct.attributes = {};
            normalizedProduct.id = product.uuid;
            normalizedProduct.type = 'product';
            normalizedProduct.attributes.title = product.ref_entity.title + ' --- TITLE';
            normalizedProduct.attributes.description = product.ref_entity.title + ' --- DESC';
            normalizedProduct.attributes.capacity = product.group_capacity;
            return normalizedProduct;
        });
        return this._super(store, primaryModelClass, normalizedPayload, id, requestType);
    }
});
