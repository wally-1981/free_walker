/**
 * The Main
 */
$(document).ready(function() {
    /*
     * load the i18n strings for localization support.
     */
    $.i18n.properties({
        name: 'nls_strings',
        path: '/i18n/',
        mode: 'both',
        callback: function() {
            $('title').html($.i18n.prop('main_title'));
            $('h3#main_title').html($.i18n.prop('main_title'));

            $('a#home_sec').html($.i18n.prop('home'));
            $('a#customize_sec').html($.i18n.prop('customize'));
            $('a#mine_sec').html($.i18n.prop('mine'));
        }
    });

    var navigatable_nav_item = {
        'a#home_sec' : 'div#home',
        'a#customize_sec' : 'div#customize',
        'a#mine_sec' : 'div#mine'
    };

    _.each(_.keys(navigatable_nav_item), function(element, index, list) {
        $(element).click(function() {
            _.each(list, function(element) {
                $(element).parent().removeClass('active');
                $(navigatable_nav_item[element]).addClass('hidden');
            });

            $(element).parent().addClass('active');
            $(navigatable_nav_item[element]).removeClass('hidden');
        });
    });
});
