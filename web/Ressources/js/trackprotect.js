$(function () {

    window.onbeforeunload = function() {
        localStorage.clear();
        return '';
    };
    $(window).unload(function(){
        localStorage.clear();
    });
});


