var relatedTargetModal = undefined;
var redirect = false;
$(function () {

    $('#login_form').submit(function(e) {
        e.preventDefault();
        var element = $(e.target);
        $('#login_submit').addClass('disabled');
        $.post('/login', $(this).serialize(), function (response) {
            if (response.ok) {
                redirect = true;
                $("#loginModal").modal('hide');
            } else {
                $('#login_submit').removeClass('disabled');
                $('#login_error').text("email ou mot de passe incorect");
            }
        }, 'json')
    });

    $("#loginModal").on('show.bs.modal', function (e) {
        relatedTargetModal = e.relatedTarget;
    }).on('hide.bs.modal', function (e) {
        if(relatedTargetModal !== undefined){
            var href = $(relatedTargetModal).attr("data-href");
            if(redirect){
                if(href){
                    window.location.replace(href);
                }else{
                    if(window.location.pathname == "/index.html" || window.location.pathname == "/"){
                        window.location.replace("/liste.html");
                    }else{
                        window.location.reload();
                    }
                }
            }
        }
    });

    $("#logout").click(function (e) {
        e.preventDefault();
        $.post('/logout', {}, function (data) {
            if(data.ok){
                window.location.href = "/";
            }
        }, 'json');
    });
});
       
