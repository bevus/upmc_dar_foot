var rencontre = null;
var stade = null;
var meteo = null;
var id = urlArgs().id;
var userComments = [];

var map;
var connectedPlayer = "connectedPlayer";

if(id === undefined){
    window.location.replace("/404.html");
}

$(function(){
    if(id !== undefined){
        $.get(
            "/detailRencontre", {id : id},
            function (data) {
                if(!data.error){
                    rencontre = data;
                    stade = rencontre.stade;
                    meteo = rencontre.meteo;
                    matchDescrition(rencontre, $('#matchDesc'));
                    playersCount(rencontre, $('#playersCount'));
                    teamsComposition(rencontre, $("#composition"), user);
                    matchWeather(rencontre,  $('#meteo'));
                    initMap();
                    initEventsMatch();
                }else{
                    window.location.replace("/404.html");
                }
            },
            'json'
        );
    }else{
        window.location.replace("/404.html");
    }
});

function matchDescrition(match, element) {
    element.html(`
        <div class="col-md-3">
            <img id="imgOrganiser" src="/Ressources/images/${match.organizer.img}" class="img-thumbnail" width="100" height="100">
        </div>
        <div class="col-md-9">
            <h4 id="fullNameOrganizer">${match.organizer.firstName} ${match.organizer.lastName}</h4>
            <p id="matchDescription">${match.description}</p>
            <p><b id="matchDate">${getDate(match.dateDebut)}</b> <b id="stadiumCity">${match.stade.commune}</b></p>
        </div>
    `);
}
function playersCount(match, element) {
    var closed = (match.players.length == match.nbJoueurs * 2);
    element.html(`
        <div class="col-md-12">
            <h2 id="playersNumber" class=" ${ closed ? "text-danger" : "text-success"} pull-right">${closed ? "Complet" : "Ouvert"} ${match.players.length} / ${match.nbJoueurs * 2}</h2>
        </div>
    `);
}
function initMap() {
    if(stade !== null && google){
        $("#stadiumName").text(stade.nom + ' ' + stade.commune + ' ' + stade.codePostal);
        if(user == null)
            $("#commentForm").hide();
        // if(user !== undefined){
        //     var voted = false;
        //     $.each(stade.stadeUpvoters, function (i, v) {
        //         if(v.id == user.id){
        //             voted = true;
        //         }
        //     });
        //     if(!voted){
        //         $("#note").html('<a href=""><i data-id="'+ stade.id +'" class="voteStade glyphicon glyphicon-thumbs-up"></i></a>');
        //     }
        // }
        $("#idStade").val(stade.id);
        var coords = {lat: stade.latitude, lng: stade.longitude};
        map = new google.maps.Map(document.getElementById("stadiumLocation"), {
            center: coords,
            zoom: 18,
            mapTypeId: google.maps.MapTypeId.SATELLITE
        });
        var marker = new google.maps.Marker({
            position: coords,
            map: map,
            title: stade.nom
        });
    }
}
function matchWeather(match, element) {
    var matchDate = new Date(match.dateDebut);
    var meteoDate = new Date(match.meteo.dayDate);
    if(matchDate.getDate() == meteoDate.getDate() && matchDate.getMonth() == meteoDate.getMonth() && matchDate.getFullYear() == meteoDate.getFullYear()){
        var dateString = `${meteoDate.getDate()}/${meteoDate.getMonth() + 1}/${meteoDate.getFullYear()}`;
        element.html(  `<div class="col-md-7">
                            <h4>${match.meteo.dayName} ${dateString}</h4>
                            <h4>${match.meteo.dayT}°</h4>
                            <h5>min ${match.meteo.min}° max ${match.meteo.max}°</h5>
                            <h5>matin ${match.meteo.morn}° soir ${match.meteo.eve}° nuit ${match.meteo.nightT}°</h5>
                        </div>
                        <div class="col-md-5">
                            <h4>${match.meteo.description} <img src="${match.meteo.icon}" alt="${match.meteo.description}"></h4>
                            <h5>Humidité : ${match.meteo.humidity}%</h5>
                            <h5>Vent : ${match.meteo.speed}m/s</h5>
                        </div>
                        <div class="col-md-12">
                             <hr>
                        </div>
    `);
    }else{
        var weatherAvailableDate = new Date(matchDate.getTime() - (1000 * 3600 * 24 * 15));
        element.append(`<p class="alert alert-warning">
            la metéo n'est pas encore disponible, elle le sera à partir du 
            ${weatherAvailableDate.getDate()}/${weatherAvailableDate.getMonth() + 1}/${weatherAvailableDate.getFullYear()}
            </p>`);
    }
}
function handleSubmitComment(data) {
    if(data.error){
        var errorString;
        $.each(data.error, function (key, val) {
            errorString += val + "\n";
        })
    }else{
        $('#comments').prepend(addComment(data, 'userComment', user));
        $('#showComments').text("afficher plus de commentaires");
        $('html, body').animate({
            scrollTop: $("#comments").offset().top
        }, 500);
        $("#textComment").val("");
    }
}
function joinGame(data) {
    if(data.error){
        modalBody = "<p class='alert alert-danger'>";
        $.each(data, function (key, value) {
            if(key != "error"){
                modalBody += value + "<br/>";
            }
        });
        modalBody += "</p>";
        showModal("", modalBody);
    }else{
        rencontre.players.push(data);
        playersCount(rencontre, $('#playersCount'));
        var team;
        console.log(data.team);
        if (data.team == "A") {
            team = $("#teamA");
        } else if (data.team == "B") {
            team = $("#teamB");
        }
        team.append(addPlayer(data, connectedPlayer));
        $('.'+connectedPlayer).hide().slideDown(400);
        $("#joinButtons").slideUp(200).html(
            `<div class="col-md-12">
                          <hr/>
                    </div>`).append(cancelParticipationButton(data.team, rencontre, user)).slideDown(200);
    }
}
function addPlayer(player, cls) {
    return `<p class="${cls}">${player.player.firstName} ${player.player.lastName}</p>`;
}
function cancelParticipationButton(userTeam, rencontre, user) {
    if(user != null && user.id == rencontre.organizer.id){
        return "";
    }
    switch (userTeam) {
        case "A":
            return `<div class="col-md-6 text-center">
                                    <a class="cancelReservation btn btn-warning" href="" data-id="${rencontre.id}">Je ne serai pas présent</a>
                                </div>`
                ;
        case "B":
            return `<div class="col-md-6 col-md-offset-6 text-center">
                                    <a class="cancelReservation btn btn-warning" href="" data-id="${rencontre.id}">Je ne serai pas présent</a>
                                </div>`
                ;
    }
    return "";
}
function cancelGameButton(user, rencontre) {
    if(user != null && user.id == rencontre.organizer.id){
        return `<div class="col-md-12 text-center">
                    <a href="" id="cancelGameButton" class="btn btn-danger">Annuler le Match</a>
                </div>`;
    }else{
        return "";
    }
    switch (userTeam) {
        case "A":
            return `<div class="col-md-6 text-center">
                                    <a class="cancelReservation btn btn-warning" href="" data-id="${rencontre.id}">Je ne serai pas présent</a>
                                </div>`
                ;
        case "B":
            return `<div class="col-md-6 col-md-offset-6 text-center">
                                    <a class="cancelReservation btn btn-warning" href="" data-id="${rencontre.id}">Je ne serai pas présent</a>
                                </div>`
                ;
    }
    return "";
}
function joinGameButtons(rencontre) {
    var countTeamA = 0;
    var countTeamB = 0;
    $.each(rencontre.players, function (i, v) {
        if(v.team == "A")
            countTeamA++;
        if(v.team == "B")
            countTeamB++;
    });
    var buttonA = "";
    var buttonB = "";
    if (countTeamA < rencontre.nbJoueurs) {
        buttonA =
            `<div class="col-md-6 text-center">
                                     <a class="team btn btn-success" href="" data-team="${'A'}" data-id="${rencontre.id}">Jouer en équipe A</a>
                                </div>`
        ;
    } else {
        buttonA = `<div class="col-md-6 text-center"></div>`;
    }
    if (countTeamB < rencontre.nbJoueurs) {
        buttonB =
            `<div class="col-md-6 text-center">
                                 <a class="team btn btn-success" href="" data-team="${'B'}" data-id="${rencontre.id}">Jouer en équipe B</a>
                            </div>`
        ;
    } else {
        buttonB = `<div class="col-md-6 text-center"></div>`;
    }
    return buttonA + buttonB;
}
function loginButton() {
    return `<div class="col-md-12  text-center">
                        <a href="" data-toggle="modal" data-target="#loginModal">Connectez vous pour pouvoir participer à ce match</a>
                </div>`;
}
function listPlayers(players, teamAElement, teamBelement, user, userClass) {
    var userTeam = undefined;
    $.each(players, function (i, player) {
        var team;
        var cls;
        if(player.team == "A"){
            team = teamAElement;
            if(user != null && player.player.id == user.id){
                cls = connectedPlayer;
                userTeam = "A";
            }
        }else if(player.team == "B"){
            team = teamBelement;
            if(user != null && player.player.id == user.id){
                cls = userClass;
                userTeam = "B";
            }
        }
        team.append(addPlayer(player, cls));
    });
    return userTeam;
}
function teamsComposition(rencontre, element, user) {
    element.append(
        `<div class="row">
                        <div class="col-md-6 text-center" id="teamA">
                        </div>
                        <div class="col-md-6 text-center" id="teamB">
                        </div>
                        <div class="row" id="joinButtons">
                            <div class="col-md-12">
                                <hr/>
                            </div>
                        </div>
                        <div class="col-md-12" id="cancelGame">
                        </div>
                        <div class="col-md-12">
                            <hr/>
                        </div>
                </div>`
    );

    var userTeam = listPlayers(rencontre.players, $("#teamA"), $("#teamB"), user, connectedPlayer );
    // if(rencontre){
    //     $("#cancelGame").append(`
    //             <p class="alert alert-danger text-center">Cette rencontre est annulée</p>
    //     `);
    //     return;
    // }
    var currentTime = new Date();
    if(rencontre.dateDebut - 3600 * 1000 * 2 < currentTime.getTime()){
        $("#cancelGame").append(`
                <p class="alert alert-danger text-center">les inscription pour ce match sont fermées</p>
        `);
        return;
    }
    if(user != null){
        if(userTeam != undefined){
            $("#joinButtons").append(cancelParticipationButton(userTeam, rencontre, user));
            $("#cancelGame").append(cancelGameButton(user, rencontre));
        }else{
            $("#joinButtons").append(joinGameButtons(rencontre));
        }
    }else{
        var countTeamA = rencontre.players.filter(p => p.team == 'A').length;
        var countTeamB = rencontre.players.filter(p => p.team == 'B').length;
        if(countTeamA < rencontre.nbJoueurs || countTeamB < rencontre.nbJoueurs){
            $("#joinButtons").append(loginButton());
        }
    }
}
function initEventsMatch() {
    // ajour d'un nouveau commentaire sur un stade
    $("#submitComment").click(function (e) {
        e.preventDefault();
        if(user != null){
            if($("#textComment").val()){
                $.post(
                    '/stadeCommentSubmit',
                    $("#commentForm").serialize(),
                    handleSubmitComment,
                    "json"
                );
            }
        }else{
            $('#loginModal').modal("show");
        }
    });
    //afficher commentaires
    $('#showComments').click(function (e) {
        e.preventDefault();
        if(stade != null) {
            var commentsCount = $(".comment").length;
            getComments(stade.id, commentsCount, 5, $('#comments'), "comment", user, function () {
                $('.userComment').hide();
                $('#showComments').text("afficher plus de commentaires");
                if(commentsCount == $(".comment").length){
                    $('#showComments').hide();
                }
            });
        }
    });
    // choisir une equipe
    $("body").on('click', '.team', function (e) {
        e.preventDefault();
        if(!$(this).hasClass("closed")){
            var params = {
                team : $(this).attr("data-team"),
                rencontreId : $(this).attr("data-id")
            };
            $.post(
                '/detailRencontre',
                params,
                joinGame,
                'json'
            );
        }
    }).on('click', '.cancelReservation', function (e) {
        // annuler participation
        e.preventDefault();
        if(user != null){
            var index = -1;
            for(var i = 0; i < rencontre.players.length; i++){
                if(rencontre.players[i].player.id == user.id){
                    index = i;
                    break;
                }
            }
            if(index != -1){
                $.post('/cancelParticipation', {
                    rencontreId : $(this).attr("data-id")
                }, function(data) {
                    if(!data.error){
                        rencontre.players.splice(index, 1);
                        playersCount(rencontre, $('#playersCount'));
                        listPlayers(rencontre.players, $('#teamA').empty(), $('#teamB').empty(), user, connectedPlayer);
                        $("#joinButtons").slideUp(200).html(
                            `<div class="col-md-12">
                                    <hr/>
                                </div>`).append(joinGameButtons(rencontre)).slideDown(200);
                    }else{
                        showModal("", data.error);
                    }
                }, 'json');
            }
        }
    }).on("click", ".voteComment", function (e) {
        // upvote comment
        e.preventDefault();
        var element = $(this);
        if(user != undefined){
            $.post('/upvoteComment', {commentId : element.attr("data-id")}, function (data) {
                if(data.ok){
                    element.hide();
                }
            }, 'json');
        }else{
            showModal("", "vous devez vous connecter pour pouvoir effectuer cette action")
        }
    }).on("click", ".voteStade", function (e) {
        e.preventDefault();
        // upvote stade
        // var element = $(this);
        // if(user != undefined){
        //     $.post('/upvoteStadium', {stadeId : element.attr("data-id")}, function (data) {
        //         if(data.ok){
        //             element.hide();
        //         }
        //     }, 'json');
        // }else{
        //     showModal("", "vous devez vous connecter pour pouvoir effectuer cette action")
        // }
    }).on('click', '#cancelGameButton', function (e) {
        e.preventDefault();
        if(rencontre !== null && confirm("êtes vous sûr de vouloir annuler cette rencontre")){
            $.post("/cancelGame", {id: rencontre.id}, function (response) {
                    if(!response.error){
                        window.location.replace("/liste.html");
                    }else{
                        showModal("", response.error);
                    }
                }, 'json'
            );
        }
    });
}