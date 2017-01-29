/*
 *  Take a Look - Copyright (C) 2016 Riccardo Linares. All rights reserved.
 *  This file is subject to the terms and conditions defined in
 *  file 'LICENSE', which is part of this source code package,
 *  and online at https://takealook-extension.net/terms-conditions/.
 *
 */

var persona,
    myinput,
    numAggiornamenti = 0,
    numeroRisultati = 0,
    caricamento = 10,
    numProvv,
    intervalloFermo,
    flagInizializza = false,
    flagRisultati = false,
    flagNotification = false,
    flagEmail = false,
    flagMessaggioErrore = false,
    flagServer = true,
    flag_rhc_ticker_hidden = ($('#pagelet_rhc_ticker').hasClass('hidden_elem') || !($('#pagelet_rhc_ticker').length>0)),
    flagSidebar = $(".fbChatSidebar._5pr2").hasClass("hidden_elem"),
    flagTutorial_step2 = false,
    numErrorLimit = 2,
    numError = 0,
    arrayRisultati = new Array(), //array dei risultati
    arrayTuttiRisultati = new Array(), //array che contiene tutti i risultati
    nuoviRisultati = 0, //nuovi risultati per notifiche
    objRisultati = {}, //oggetto dei risultati contiene varMessage=messaggio e varUrl=url
    numero_risultati_notifiche = -1,
    numero_risultati_email = -1,
    limite_risultati_visualizzati = 1000,
    indirizzo_email = null,
    stai_dando_una_occhiata = "stai_dando_una_occhiata",
    attendi_qualche_minuto = "attendi_qualche_minuto",
    aggiornamenti_analizzati = "aggiornamenti_analizzati",
    risultati_trovati = "risultati_trovati",
    error_print = "error_print",
    limite_risultati_vis_error_print = "limite_risultati_vis_error_print",
    chi_vuoi_cercare = "chi_vuoi_cercare",
    lingua_corrente = "currentLang";


var stringNomeRicerca

function nomeRicerca(stringNome){
    stringNomeRicerca = stringNome;
}

/* ***** CHIAMATE SERVER ***** */
// Funzione che fa le chiamate al server
function callAjaxTicker() {

    var oldest = $('.pam.uiBoxLightblue.tickerMoreLink.uiMorePagerPrimary').attr('ajaxify').match("oldest=(.*)&")[1];
    var my_fb_dtsg = $('input[name="fb_dtsg"]').attr('value');
    var userId = $('input[name="xhpc_targetid"]').attr('value');

    //Le chiamate al server con method post e parametri
    var jqxhr = $.ajax({
        method: "POST",
        url: "https://www.facebook.com/ajax/ticker_entstory.php?oldest=" + oldest + "&source=fst_sidebar&dpr=1&",
        data: {
            __user: userId,
            __a: "1",
            __dny: "",
            __af: "k",
            __req: "i",
            __be: "-1",
            __pc: "PHASED:ADEFAULT",
            __rev: "2695967",
            fb_dtsg: my_fb_dtsg,
            ttstamp: ""
        },
        dataType: 'text'
    })

    // In caso di risultato positivo -> richiama la funzione checkNuoviFeed(stringa)
    jqxhr.done(function(data) {
        var myJsonResult = null;
        var jsmodsVar = jQuery.parseJSON(data.substring(9)).jsmods;
        if (!jQuery.isEmptyObject(jsmodsVar)) {
            myJsonResult = jsmodsVar.markup["0"][1].__html;
        } else {
            console.log(jQuery.parseJSON(data.substring(9)).errorSummary + " " + jQuery.parseJSON(data.substring(9)).errorDescription);
        }
        checkNuoviFeed(myJsonResult);
    })

    // In caso di risultato negativo -> messaggio di errore!
    jqxhr.fail(function(xhr, status, error) {
        console.log(xhr.responseText);
    })

    /*jqxhr.always(function() {
        console.log("CHIAMATA CON IL SERVER CONCLUSA");
    })*/
}

function checkNuoviFeed(stringa) {
    //limite_risultati_visualizzati
    if (arrayTuttiRisultati.length < limite_risultati_visualizzati) {
        //Inserisce i risultati e controlla le corrispondenze
        $('.tickerActivityStories').append(stringa);
        $(".pam.tickerMorePager.stat_elem").hide();
        //Rimuove i "mostra altro" in eccesso (lascia solo l'ULTIMO)
        $(".pam.tickerMorePager.stat_elem").not("div:gt(-2)").remove();
        //Rimuove i "mostra altro" in eccesso (lascia solo il PRIMO)
        //$(".pam.tickerMorePager.stat_elem").remove("div:gt(0)");
        if (flagServer) {
            callAjaxTicker();
        }
    } else {
        // stampa messaggio errore
        $('.controllo').append('<p style="color: red;">' + limite_risultati_vis_error_print + '</p>');
        // ferma il check degli errori controllaFermo
        stopRicerca();
        //superato il limite si elimina il mostra altro e si ferma la ricerca!
        $(".pam.tickerMorePager.stat_elem").remove();
    }

}

/* ***** NOTIFICHE ***** */
// Richiede il permesso di inviare notifiche
document.addEventListener('DOMContentLoaded', function() {
    if (Notification.permission !== "granted")
        Notification.requestPermission();
});

// Funzione che genera le notifiche
function notifyMe(quanteNotifiche) {

}

/* ***** EMAIL ***** */
// Invia l'email al destinatario
function sendEmail(listaRisultati, toEmail) {
    $.post("https://takealook-extension.net/sendemail.php", {
        myArray: listaRisultati,
        miaEmail: toEmail,
        miaRicerca: $('.cerca_nome').val(),
        lingua: lingua_corrente
    }, function(result) {

        console.log(result);
    });
}


function userSettings() {


}

// Controlla le impostazioni dopo ogni risultato aggiunto
function checkRisultati() {

    if (flagNotification && flagInizializza && nuoviRisultati >= numero_risultati_notifiche) {
        notifyMe(numero_risultati_notifiche);
        nuoviRisultati = 0;
        //  console.log("FLAG NOTIFICATION: " + flagNotification);
    }
    if (flagEmail && flagInizializza && arrayRisultati.length >= numero_risultati_email) {
        sendEmail(arrayRisultati, indirizzo_email);
        arrayRisultati = [];
        //  console.log("FLAG EMAIL: " + flagEmail);
    }
}


/* ***** FUNZIONI INTERNE ***** */
// Nasconde gli elementi diversi dall'input: controlla le persone nella lista e se non corrispondono le elimina!
function nascondiDiversi() {
    $('.tickerActivityStories .fbFeedTickerStory').each(function(i, obj) {
        if (!($(this).hasClass('controllatoTAL'))) {
            persona = $(this).find('.fwb');
            myinput = $('.cerca_nome').val();
            numAggiornamenti = numAggiornamenti + 1;

            if (!(persona.text().toLowerCase().indexOf(myinput.toLowerCase()) >= 0)) {
                $(this).remove();
            } else {
                flagRisultati = true;
                $(this).addClass('controllatoTAL');
                objRisultati = {
                    varMessage: $(this).find('.tickerFeedMessage').text(),
                    varUrl: $(this).find('.tickerStoryLink').attr('href')
                };
                arrayRisultati.push(objRisultati);
                arrayTuttiRisultati.push(objRisultati);

                if (flagNotification) {
                    nuoviRisultati = nuoviRisultati + 1;
                }
                checkRisultati();
                rimuoviPrimo();
            }
        }
    });
}


function aggiungiPrimo() {
    var nome = $.trim($('.cerca_nome').val().toLowerCase());
    if (nome.length > 0 ) {
        cerca_nome = $('.cerca_nome').val();
    }
    $('.tickerActivityStories').prepend('<div class="fbFeedTickerStory tickerStoryClickable _5f0v loading_take_a_look controllatoTAL" data-rel="async" tabindex="0" data-fte="1" data-ftr="1" aria-controls="js_77" aria-haspopup="true" role="null" aria-describedby="js_mf"><a class="tickerStoryLink" href="/" rel="ignore"><div class="fbFeedTickerBorder"><div class="clearfix tickerStoryBlock"><div class="lfloat _ohe"><img class="_s0 tickerStoryImage _54ru img" src="icon/icon128.png" alt=""><span class="img _55ym _55yq _55yo tickerSpinner" aria-label="Caricamento..." aria-busy="1"></span></div><div class="_42ef"><div class="tickerFeedMessage">' + stai_dando_una_occhiata + ' <span class="fwb">' + cerca_nome + '</span>! ' + attendi_qualche_minuto + '.</div> </div></div></div></a></div>');
}

function rimuoviPrimo() {
    $('.loading_take_a_look').remove();
}

// Controlla che la ricerca non si sia fermata e in caso la fa ripartire
function controllaFermo() {
    intervalloFermo = setInterval(function() {
        numProvv = numAggiornamenti;
        setTimeout(function() {
            if (numProvv == numAggiornamenti) {
                if (numError < numErrorLimit) {
                    numError = numError + 1;
                    // console.log('Error n: ' + numError);

                } else {
                    if (!flagMessaggioErrore) {
                        $('.controllo').append('<p style="color: red;">' + error_print + '</p>');
                        flagMessaggioErrore = true;
                    }
                    console.log('Too much error, try again later')
                }
            }
        }, 29000)
    }, 30000);
}

// Funzione che ferma la ricerca
function stopRicerca() {
    clearInterval(intervalloFermo);
    //flagServer = false;
    //flagInizializza = false;
}

/* ***** INIZIALIZZAZIONE ***** */

function inizializza() {

    var nome = $.trim($('.cerca_nome').val().toLowerCase());
      flagInizializza = true;
    $('.loading_take_a_look').remove();
    if (nome.length > 0 ){
        aggiungiPrimo();
        callAjaxTicker();
        nascondiDiversi();

        //controllaFermo();
    } else {
        alert("Inserisci un nome");

    }
};


 function tutorial() {
    /*chrome.storage.local.set({
        'tutorial': true,
    });

    if (!flagSidebar) {
      $("._51x_").ready(function() {
          flagTutorial_step2 = true;
          $(".fbChatSidebar._5pr2").removeClass("fixed_always");
          $("#pagelet_bluebar").css({
              "position": "fixed",
              "z-index": "300"
          });
          $("._48gf.fbDockWrapper.fbDockWrapperRight").css({
              "z-index": "100"
          });
          $("#u_0_1r").css({
              "z-index": "100"
          });
          $("._51x_").prepend('<div id="coverTAL"></div>');

          $("#coverTAL").click(function() {
              $("#coverTAL").remove();
              $(".fbChatSidebar._5pr2").addClass("fixed_always");
              $("#pagelet_bluebar").css({
                  "z-index": "301"
              });
          });
      });
    }*/
}


// controlla quale ticker è attivo
function whichTicker(){

  flag_rhc_ticker_hidden = ($('#pagelet_rhc_ticker').hasClass('hidden_elem') || !($('#pagelet_rhc_ticker').length>0));

  if (!flag_rhc_ticker_hidden) {
      $(".controllo").prependTo(".home_right_column");
      $(".controllo").css({
        "background-color": "#fff",
      });

      $(".controllo").each(function () {
        this.style.setProperty( 'z-index', 'inherit', 'important' );
      });

      $(".cerca_nome").css({
          "border": "1px solid #f0f0f2",
          "height": "24px",
      });

  } else {
      $(".controllo").insertAfter("#pagelet_canvas_nav_content");
      $(".controllo").css({
        "background-color" : "#e9ebee",
        "z-index" : "10000"
      });

      $(".cerca_nome").css({
          "border": "none",
      });
  }
}

// Apre il tab con le impostazioni dell'user
function tabSettings(){
}

// Quando il DOM si carica
$(document).ready(function() {

    //aggiunta elementi grafici
    //controllo sidebar (ORGINIALE)
    $("#pagelet_canvas_nav_content").after('<div class="controllo"><input id="search_TAL" class="button_TAL search_TAL" type="submit" value=""/><span id="controlloCerca"><input type="text" class="cerca_nome" placeholder="' + chi_vuoi_cercare + '"/></span><p class="printRisultati"></p></div>');


    // mostra riquadro aggiornamenti originale se è nascosto
    /*if(flag_rhc_ticker_hidden && (($("#pagelet_ticker").height()/$("#pagelet_ticker").parent().height())*100)<30){
      $("#pagelet_ticker").css({"height": "30%"});
    }*/

    // nasconde/mostra i ticker in base a quale dei due è visibile
    // considera anche il resize dello schermo
    whichTicker();
    $(window).resize(function() {
        whichTicker()
    });

    $('.printRisultati').html(risultati_trovati + ': 0<br>' + aggiornamenti_analizzati + ': 0');

    // Controlla inserimento di nuovi aggiornamenti
    $('.tickerActivityStories').on('DOMNodeInserted', function() {
        if (flagInizializza) {
            nascondiDiversi();
        }
    });

    // Aggiorna il numero di risultati
    $('.tickerActivityStories').on('DOMSubtreeModified', function() {
        numeroRisultati = $('.tickerActivityStories .fbFeedTickerStory').length;

        if (flagInizializza && flagRisultati) {
            $('.printRisultati').html(risultati_trovati + ': ' + numeroRisultati + '<br>' + aggiornamenti_analizzati + ': ' + numAggiornamenti);
        } else if (flagInizializza && !flagRisultati) {
            $('.printRisultati').html(risultati_trovati + ': 0<br>' + aggiornamenti_analizzati + ': ' + numAggiornamenti);
        }

    });

    // Inizio ricerca con tasto grafico
    $('#search_TAL').click(function() {
        if (!flagInizializza) {
            inizializza();
        }
    });

    // Inizio ricerca premendo tasto ENTER
    $('.cerca_nome').keypress(function(e) {
        if (e.which == 13) {
            if (!flagInizializza) {
                inizializza();
            }
        }
    });

    // Setta le impostazioni dell'UTENTE
    userSettings();


});
