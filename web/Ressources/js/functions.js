/**
 * ajoute une modal a la page et l'afficher
 * @param title
 * @param body
 */
function showModal(title, body) {
    if(!document.getElementById("messageModal")){
        $("body").append('<div class="modal fade" id="messageModal"><div class="modal-dialog"><div class="modal-content"> <div class="modal-header"> <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button> <h4 class="modal-title"></h4> </div> <div class="modal-body"> </div> <div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">fermer</button></div></div></div></div>');
    }
    $("#messageModal").find(".modal-title").html("").append(title);
    $("#messageModal").find(".modal-body").html("").append(body)
    $("#messageModal").modal('show');
}
function updateWeatherElement(data, element, reverse) {
    if(!data.error){
        $.each(data, function(i, day){
            var date = new Date(day.dayDate);
            var dateString = `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
            var html = `<div clas   s="dayWeather text-center">
                                <p class="dayName">${day.dayName}</p>
                                <p class="dayDate">${dateString}</p>
                                <p class="dayTemp"><b>${day.dayT}°</b> <span class="text-muted">${day.nightT}°</span></p>
                                <img src="${day.icon}" alt="${day.description}"/>
                            </div>`;
            if(reverse){
                element.prepend(html).addClass("text-center");
            }else{
                element.append(html).addClass("text-center");
            }
        });
    }else{
        $(element).append(
            `<div class="col-md-12 text-center">
                  <p class="alert alert-danger">impossible de charger les données météo</p>
             </div>`
        );
    }
}
/**
 * utilisée dans : rencontre.html et organiserRencontre
 * charge les données météos depuis le serveur qui contact l'api openweather
 * @param lat
 * @param lon
 * @param nbDays
 * @param element
 */
function loadWeatherData(lat, lon, nbDays, element) {
    $.get(
        "/weather",
        {
            lat: lat,
            lon: lon,
            nbDays: nbDays
        },
        function(data){
            updateWeatherElement(data, element, false);
        },
        'json'
    )
}
/**
 * utilisée dans : rencontre.html
 * format la date (Le d/m/yy à h:m)
 * @param timestamp
 * @returns {string}
 */
function getDate(timestamp) {
    var date = new Date(timestamp);
    return "Le "+date.getDate()+"/"+(date.getMonth() + 1)+"/"+date.getFullYear()+" à " + (date.getHours() > 9 ? date.getHours() : "0" + date.getHours()) + ":" + (date.getMinutes() > 9  ? date.getMinutes() : "0" + date.getMinutes());
}
/**
 * utilisée dans : rencontre.html
 * retourn le code html d'un commentaire
 * @param comment
 * @param commentClass
 * @param user
 * @returns {string}
 */
function addComment(comment, commentClass, user) {
    var vote = "";
    if (user != undefined) {
        var voted = false;
        $.each(comment.upvoters, function (i, v) {
            if (v.id == user.id) {
                voted = true;
            }
        });
        if (!voted) {
            vote = `<a href=""><i data-id="${comment.id}" class="voteComment glyphicon glyphicon-thumbs-up"></i></a>`;
        }
    }
    return `<div class="col-md-12 ${commentClass}">
                    <div class = "col-md-1 text-center">
                        <img src = "/Ressources/images/${comment.author.img}" class="img-circle img-thumbnail" alt="">
                    </div>
                    <div class = "col-md-11">
                        <div class = "row">
                            <div class = "col-md-12">
                                <div class="pull-right">
                                    ${vote}
                                </div>
                                <h4>${comment.author.firstName} ${comment.author.lastName}</h4>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <p>${comment.textComment}</p>
                                <b class="pull-right">${getDate(comment.dateComment)}</b>
                            </div>
                            <div class="col-md-12">
                                <hr/>
                            </div>
                        </div>
                    </div>
                </div>`;
}
function getComments(stadeId, first, count, element, commentClass, user, callback) {
      $.get('/getComments',
          {
              stadeId: stadeId,
              first: first,
              count: count
          },function (data) {
              if(!data.error){
                  $.each(data, function (i, c) {
                      element.append(addComment(c, commentClass, user) );
                  });
                  $('html, body').animate({
                      scrollTop: element.offset().top
                  }, 500);
                  callback();
              }
          }, 'json'
      );
}
function addRencontre(rencontre) {
    return `<li class="match">
                        <div class="row">
                            <div class="col-md-2">
                                    <img src="Ressources/images/${rencontre.organizerPic}" class="img-thumbnail" width="80" height="80">
                            </div>
                            <div class="col-md-7">
                                <h4>${rencontre.organizerFirstName} ${rencontre.organizerLastName}</h4>
                                <p>${rencontre.description}</p>
                                <p><b> ${getDate(rencontre.dateTime)}</b></p>
                            </div>
                            <div class="col-md-3" style="text-align: right">
                                <h3 class="${(rencontre.playersCount < rencontre.maxPlayersCount) ? "text-success" : "text-danger"}">${rencontre.playersCount}/${rencontre.maxPlayersCount}</h3>
                                <b>à  ${rencontre.city}</b>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <a href="rencontre.html?id=${rencontre.id}" class="btn btn-primary pull-right">détails</a>
                            </div>
                        </div>
                    </li>`;
}
function listRencontre(url, args, element, emptyHtml, mode) {
    if(mode == 0){
        element.html(emptyHtml);
    }
    console.log(args);
    $.ajax({
        url: url,
        method: "GET",
        data: args,
        dataType: "json",
    }).done(function (data) {
        if(!data.error){
            var html = "";
            $.each(data, function (i, rencontre) {
                html += addRencontre(rencontre);
            });
            if(html.length)
                if(mode == 0)
                    element.html(html);
                else if(mode == 1)
                    element.append(html);
        }
    }).fail(function (xhr, status) {
        alert(status);
    });
}
function urlArgs() {
    var params = {};
    var queryString = window.location.search.substr(1);
    if(queryString.length){
        $.each(queryString.split('&'), function (i, v) {
            var tmp = v.split('=');
            if(tmp.length === 2)
                params[tmp[0]] = tmp[1]
        });
    }
    return params;
}