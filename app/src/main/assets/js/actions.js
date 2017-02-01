/*
 *  Take a Look - Copyright (C) 2016 Riccardo Linares. All rights reserved.
 *  This file is subject to the terms and conditions defined in
 *  file 'LICENSE', which is part of this source code package,
 *  and online at https://takealook-extension.net/terms-conditions/.
 *
 */

var persona,
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
    arrayRisultati = [], //array dei risultati //TODO ho modificato con []
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


/*
        TODO
        NOTA BENE:
        da java usa l'interface per chiamare le funzioni javascript e da javascript usa il return per passare le info!

*/
var stringNomeRicerca;

/* FUNZIONI DA RICHIAMARE IN JAVA */

// indica il nome della persona da cercare
function nomeRicerca(stringNome){
    stringNomeRicerca = stringNome;
    inizializza();
}


function risultatiTrovati(){
    console.log("array" + arrayRisultati);
    return arrayRisultati;
}

function svuotaRisultati(){
    arrayRisultati = [];
}

function flagServer(b){
    flagServer = b;
}
/* END FUNZIONI DA RICHIAMARE IN JAVA */




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
         //Inserisce i risultati e controlla le corrispondenze
        $('.tickerActivityStories').append(stringa);
        $(".pam.tickerMorePager.stat_elem").hide();
        //Rimuove i "mostra altro" in eccesso (lascia solo l'ULTIMO)
        $(".pam.tickerMorePager.stat_elem").not("div:gt(-2)").remove();
        if (flagServer) {
            callAjaxTicker();
        }
}


/* ***** FUNZIONI INTERNE ***** */
// Nasconde gli elementi diversi dall'input: controlla le persone nella lista e se non corrispondono le elimina!
function nascondiDiversi() {
    $('.tickerActivityStories .fbFeedTickerStory').each(function(i, obj) {
        if (!($(this).hasClass('controllatoTAL'))) {
            persona = $(this).find('.fwb');
            numAggiornamenti = numAggiornamenti + 1;

            if (!(persona.text().toLowerCase().indexOf(stringNomeRicerca.toLowerCase()) >= 0)) {
                $(this).remove();
            } else {
                flagRisultati = true;
                $(this).addClass('controllatoTAL');
                objRisultati = {
                    varMessage: $(this).find('.tickerFeedMessage').text(),
                    varUrl: $(this).find('.tickerStoryLink').attr('href')
                };
                arrayRisultati.push(objRisultati);


                window.INTERFACE.riceviDati($(this).find('.tickerFeedMessage').text(), $(this).find('.tickerStoryImage').attr('src'), $(this).find('.tickerStoryLink').attr('href'));

                if (flagNotification) {
                    nuoviRisultati = nuoviRisultati + 1;
                }
                rimuoviPrimo();

            }
        }
    });
}


function aggiungiPrimo() {
    if (stringNomeRicerca.length > 0 ) {
        cerca_nome = stringNomeRicerca;
    }
    $('.tickerActivityStories').prepend('<div class="fbFeedTickerStory tickerStoryClickable _5f0v loading_take_a_look controllatoTAL" data-rel="async" tabindex="0" data-fte="1" data-ftr="1" aria-controls="js_77" aria-haspopup="true" role="null" aria-describedby="js_mf"><a class="tickerStoryLink" href="/" rel="ignore"><div class="fbFeedTickerBorder"><div class="clearfix tickerStoryBlock"><div class="lfloat _ohe"><img class="_s0 tickerStoryImage _54ru img" src="icon/icon128.png" alt=""><span class="img _55ym _55yq _55yo tickerSpinner" aria-label="Caricamento..." aria-busy="1"></span></div><div class="_42ef"><div class="tickerFeedMessage">' + stai_dando_una_occhiata + ' <span class="fwb">' + cerca_nome + '</span>! ' + attendi_qualche_minuto + '.</div> </div></div></div></a></div>');
}

function rimuoviPrimo() {
    $('.loading_take_a_look').remove();
}



// Funzione che ferma la ricerca
function stopRicerca() {
    clearInterval(intervalloFermo);
    //flagServer = false;
    //flagInizializza = false;
}

/* ***** INIZIALIZZAZIONE ***** */

function inizializza() {

      flagInizializza = true;
    $('.loading_take_a_look').remove();
    if (stringNomeRicerca.length > 0 ){
        aggiungiPrimo();
        callAjaxTicker();
        nascondiDiversi();

    } else {
        console.log("Inserisci un nome");

    }
};



// controlla quale ticker è attivo
function whichTicker(){

  flag_rhc_ticker_hidden = ($('#pagelet_rhc_ticker').hasClass('hidden_elem') || !($('#pagelet_rhc_ticker').length>0));

  if (!flag_rhc_ticker_hidden) {
      $(".controllo").prependTo(".home_right_column");

  } else {
      $(".controllo").insertAfter("#pagelet_canvas_nav_content");
  }
}



// Quando il DOM si carica
$(document).ready(function() {

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

});
