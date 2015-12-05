import Ember from 'ember';

export default Ember.Component.extend({
    tagName: 'nav',

    searchRoute: '/search',
    customizeRoute: '/customize',
    mineRoute: '/mine',

    isSearchActive: Ember.computed.equal('searchRoute', window.location.pathname),
    isCustomizeActive: Ember.computed.equal('customizeRoute', window.location.pathname),
    isMineActive: Ember.computed.equal('mineRoute', window.location.pathname),

    click(e) {
        $('ul#nav-list').children().each(function(index, item) {
            $(item).removeClass('active');
        });
        $(e.target).parent().addClass('active');
    },

    didInsertElement() {
        $('div#application-root').unwrap();
    }
});
