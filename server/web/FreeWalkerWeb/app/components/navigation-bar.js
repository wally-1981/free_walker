import Ember from 'ember';

export default Ember.Component.extend({
    tagName: 'nav',

    searchRoute: 'search\/?$',
    customizeRoute: 'customize\/?$',
    mineRoute: 'mine\/?$',

    isSearchActive: Ember.computed('searchRoute', function() {
        return window.location.pathname.match(this.get('searchRoute'));
    }),
    isCustomizeActive: Ember.computed('customizeRoute', function() {
        return window.location.pathname.match(this.get('customizeRoute'));
    }),
    isMineActive: Ember.computed('mineRoute', function() {
        return window.location.pathname.match(this.get('mineRoute'));
    }),

    click(e) {
        $('ul#nav-list').children().each(function(index, item) {
            $(item).removeClass('active');
        });
        $(e.target).parent().addClass('active');

        var target = $(e.target)[0];
        if (target.href.match(this.get('searchRoute'))) {
            this.didSearchElementInserted();
        } else if (target.href.match(this.get('customizeRoute'))) {
            this.didCustomizeElementInserted();
        } else if (target.href.match(this.get('mineRoute'))) {
            this.didMineElementInserted();
        } else {
            console.log('Unknown Nav Item Selected');
        }
    },

    didInsertElement() {
        $('div#application-root').unwrap();

        if (this.get('isSearchActive')) this.hookEventListeners4Search();
    },

    didSearchElementInserted() {
        this.hookEventListeners4Search();
    },

    didCustomizeElementInserted() {
        this.hookEventListeners4Customize();
    },

    didMineElementInserted() {
        this.hookEventListeners4Mine();
    },

    hookEventListeners4Search() {
        var ham = new Hammer($('div#adsCarousel').get(0));
        ham.on('swipeleft', function() {
            $('div#adsCarousel').carousel('next');
        });
        ham.on('swiperight', function() {
            $('div#adsCarousel').carousel('prev');
        });
    },

    hookEventListeners4Customize() {
    },

    hookEventListeners4Mine() {
    }
});
