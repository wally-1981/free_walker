import Ember from 'ember';
import config from './config/environment';

const Router = Ember.Router.extend({
  location: config.locationType
});

Router.map(function() {
  this.route('search');
  this.route('customize');
  this.route('mine');
  this.route('products');
  this.route('ads');
});

export default Router;
