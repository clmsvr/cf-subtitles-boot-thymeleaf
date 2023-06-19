var contextPath = "";
var videoid = '';
var blockid = '';
var startTime = 0;
var subtitlesBegin = []; //array com os tempos de inicio de cada legenda
var subtitlesEnd = [];   //array com os tempos de fim de cada legenda

var intervalId = 0,
    currentIndex = -1,
    lastTime = 0,
    lastDisplayTime = 0;  //para controlar o tempo limite para legendar 1 bloco   


//inicialização JQuery
$(function() {
    // Clear any old interval.
    clearInterval(intervalId);

    // Start interval to sinchronize subtitles
    intervalId = setInterval(function () {
        intervalTik();
    }, 200);
   
    $('#subtitile-begin').text(formatTime(subtitlesBegin[0]));
    $('#subtitile-end').text(formatTime(subtitlesEnd[subtitlesEnd.length-1]));
});

function intervalTik(){

    //se nao ha legendas
    if(subtitlesBegin.length == 0 ){
        return;
    }
    
    //var time = new Date().getTime();
    var time =  Date.now();
    var timeElem = $('#subtitile-warning-time');
    
    if(time - lastDisplayTime > 1000)
    {
        var timediff = 120*60000-(time-startTime);
        
        if (timediff < 0 )
        {
            timediff = 0;
            if (timeElem.hasClass('red-text') == false)
            {
                timeElem.addClass('red-text');
                $('#subtitile-warning-time').text(formatTime(timediff));
            }
        }
        else
        {
            $('#subtitile-warning-time').text(formatTime(timediff));
        }
    }
}

function formatTime(time) //tempo do video em milisegundos
{
    time = Math.round(time/1000); // segundos
    var minutes = Math.floor(time / 60),
        seconds = time - minutes * 60;
    seconds = seconds < 10 ? '0' + seconds : seconds;
    return minutes + ":" + seconds;
}


/*********************************
 *         EVENTOS  https://developers.google.com/youtube/iframe_api_reference#Events
 *********************************/

function textOnChange(index, e)
{
    //http://api.jquery.com/jquery.ajax/
    $.ajax({
        type: 'POST',
        url: contextPath+'/s/legendas/worker/saveBlockItem',
        //data: form.serialize(),
        //data: { index: "John", location: "Boston" },
        data: { blockid: blockid, index: index, text: $('#text'+index).val() },
        dataType: 'json',  //data type da resposta. sobrescreve o que vier do servidor
        success: function( resp ) 
        {
            //console.log('resp=['+resp+']');
            if ( resp == 'ok' ) 
            {
                console.log('OK. Atualizada legenda ['+ ++index +']');
            }
            else
            {
                console.log('FALHA na atualização da legenda ['+ ++index +']');
            }
        },
        error: function() {
            console.log('ERROR...na atualização da legenda.');
         },        
      });
}

function finalizeBlock(formid, startTime)
{
    var time =  Date.now();
    var diff = time - startTime;
    
    if (diff > 0 && diff < 0 ) //4*60000)
    {
        //alert('Finalizar o trabalho requer um tempo mínimo de 4 minutos.');
        $( "#dialog" ).dialog();
    }
    else
    {
        var list = []; //ou...  new Array("Saab", "Volvo", "BMW");
        //capturar todos os textos
        $( "#subtitle textarea"  ).each(function( index ) {
            //console.log('\n' + index + ": " + $( this ).attr('id') + '\n' + $( this ).val() );
            list.push($( this ).val());
        });        
        
        //setar no campo textarea
        $('#'+formid + ' input[name=subtitle]').val(JSON.stringify(list));
        
        console.log($('#'+formid + ' input[name=subtitle]').val());
        
        document.forms[formid].submit();
    }
}