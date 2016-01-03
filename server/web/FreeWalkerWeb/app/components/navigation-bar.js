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
        var target = Ember.$(e.target)[0];
        if (!target.href) {
            return;
        }

        Ember.$('ul#nav-list').children().each(function(index, item) {
            Ember.$(item).removeClass('active');
        });
        Ember.$(e.target).parent().addClass('active');

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
        Ember.$('div#application-root').unwrap();

        if (this.get('isSearchActive')) { this.hookEventListeners4Search(); }
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
        if (Ember.$('div#adsCarousel').length === 0) {
            return;
        }

        var ham = new Hammer(Ember.$('div#adsCarousel').get(0));
        ham.on('swipeleft', function() {
            Ember.$('div#adsCarousel').carousel('next');
        });
        ham.on('swiperight', function() {
            Ember.$('div#adsCarousel').carousel('prev');
        });
    },

    hookEventListeners4Customize() {
    },

    hookEventListeners4Mine() {
    }
});
