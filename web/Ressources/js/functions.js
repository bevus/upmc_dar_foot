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
            var date = new Date(day.date);
            var dateString = `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
            var html = `<div class="dayWeather text-center">
                                <p class="dayName">${day.name}</p>
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
            updateWeatherElement(data.days, element, false);
        },
        'json'
    )
}
function loadLocalWeather(matchId, nbDays, element) {
    $.get('/localWeather', {
        matchId: matchId,
        nbDays: nbDays,
    }, function (data) {
        updateWeatherElement(data, element, true);
    }, 'json');
}
/**
 * utilisée dans : rencontre.html
 * format la date (Le d/m/yy à h:m)
 * @param timestamp
 * @returns {string}
 */
function getDate(timestamp) {
    var date = new Date(timestamp);
    return "Le "+date.getDate()+"/"+(date.getMonth() + 1)+"/"+date.getFullYear()+" à "+date.getHours()+":"+date.getMinutes()
}
/**
 * utilisée dans : rencontre.html
 * retourn le code html d'un commentaire
 * @param comment
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
                  callback();
              }
          }, 'json'
      );
}
