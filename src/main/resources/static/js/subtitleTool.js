var contextPath = "";
var videoid = '';
var blockid = '';
var startTime = 0;
var maxtime = 0;

var playerReady = false,
    timedout = false;
    intervalId = 0,
    subtitlesBegin = [], //array com os tempos de inicio de cada legenda
    subtitlesEnd = [],   //array com os tempos de fim de cada legenda
    currentIndex = -1,
    lastTime = 0,
    editIndex=-1, //para otimizar
    lastDisplayTime = 0;  //para controlar o tempo limite para legendar 1 bloco   

/********************************************************
 * YOUTUBE Player
 */
//This code loads the IFrame Player API code asynchronously.
var tag = document.createElement('script');
tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

var player_caption;
var player;
//This function creates an <iframe> (and YouTube player) after the API code downloads.
function onYouTubeIframeAPIReady() {
    player = new YT.Player('player', {
        height : '390',
        width : '640',
        playerVars : {
            'autoplay' : 0,
            'start' : 0,
            'fs' : 0,  //0 impede que o botão de tela inteira apareça.
            'rel' : 0, // nao exibir vídeos relacionados quando a reprodução do vídeo acaba.
            //'modestbranding' : 1,
            'iv_load_policy' : 3,  // 3 fará com que as anotações do vídeo não sejam exibidas 
            'disablekb': 1, //Definir como 1 desabilita os controles de teclado do player.
            'showinfo' : 0  //não exibirá informações como título do vídeo e remetente
        },
        videoId : videoid,
        events : {
            'onReady' : onPlayerReady
        }
    });
}
function onPlayerReady(event) {
    // player.seekTo(1,true);
    // event.target.playVideo();
    //videoSeconds = player.getDuration();
    
    // Clear any old interval.
    clearInterval(intervalId);

    // Start interval to sinchronize subtitles
    intervalId = setInterval(function () {
        intervalTik();
    }, 200);
    
    //player.seekTo(blockStart/1000,true); //nao finciona devido ao fato de que o YouTube relembra "playback position" do usuario.
    playerReady = true;
    
    $('#subtitile-begin').text(formatTime(subtitlesBegin[0]));
    $('#subtitile-end').text(formatTime(subtitlesEnd[subtitlesEnd.length-1]));
    
    player_caption = $('#player-captions-text');
}

function intervalTik(){

    //se nao ha legendas
    if(subtitlesBegin.length == 0 ){
        return;
    }
    
    if (maxtime > 0)
    {
        if(timedout) return;
        
        //var time = new Date().getTime();
        var time =  Date.now();
        var timeElem = $('#subtitile-warning-time');
        
        if(time - lastDisplayTime > 1000)
        {
            var timediff = maxtime*60000-(time-startTime);
            
            if (timediff < 0 )
            {
                timediff = 0;
                if (timeElem.hasClass('red-text') == false)
                {
                    timeElem.addClass('red-text');
                    $('#subtitile-warning-time').text(formatTime(timediff));
                }
                
                timedout = true;
                resetTimerEdition();
                $('.subtitile-warning').text('EXPIROU (!!!) o tempo para legendar este bloco de legendas.');
                $("#tool-form :input").attr("disabled", true);
                return;
            }
            else
            {
                $('#subtitile-warning-time').text(formatTime(timediff));
            }
        }
    }
    
    //se nao estiver reproduzindo, nao ha nada a fazer
    if (player.getPlayerState() != 1 ) return ;

    var currentTime = player.getCurrentTime()*1000;
    if (currentTime == lastTime)return;
    
    lastTime = currentTime;
    
    //se o player esta fora do intervalo do bloco
    if (currentTime < subtitlesBegin[0] || currentTime > subtitlesEnd[subtitlesEnd.length-1])
    {
        //console.log("FORA do bloco");
        
        //retirar a classe de .subtitle-line-focused da legenda corrente se != -1
        if (currentIndex >= 0){
            $("#subtitle"+currentIndex).removeClass("subtitle-line-highlight");
            player_caption.text('').hide();
        }
        currentIndex = -1;
        return;
    }    

    //se nao mudou de legenda
    if (currentIndex != -1 && currentTime <= subtitlesEnd[currentIndex] && currentTime >= subtitlesBegin[currentIndex]){
        //console.log("NAO MUDOU " );
        return;
    }
    
    //verificar se o player saiu da legenda atual (mas ainda dentro do bloco devido verificacao anterior)
    if (currentIndex != -1 && currentTime > subtitlesEnd[currentIndex] 
                           && currentTime < subtitlesEnd[currentIndex+1]){
    
        //console.log("localizando PROXIMO de  ["+(currentIndex)+"] " + currentTime);
        
        //retirar o foco de currentIndex
        $("#subtitle"+currentIndex).removeClass("subtitle-line-highlight");
        
        currentIndex = currentIndex + 1;
        //adicionar classe de foco
        var current = $("#subtitle"+currentIndex);
        current.addClass("subtitle-line-highlight");
        
        player_caption.text($('#text'+currentIndex).val()).show();  //!! val() e nao text()
        
        //se elemento nao está visivel, scrool do elemento para o topo da view.
        if(isFullVisible(current) === false)
        {
            //console.log("nao visivel ["+(currentIndex)+"]",current);
            scroolTop(current);
        }
        
        return;
    }
    
    // o player chegou aa primeira legenda
    if (currentTime >= subtitlesBegin[0] && currentTime < subtitlesEnd[0]){
        
        //console.log("ENTROU");
        
        //retirar a classe de .subtitle-line-focused da legenda atual se != -1
        if (currentIndex >= 0){
            $("#subtitle"+currentIndex).removeClass("subtitle-line-highlight");
        }
        currentIndex = 0;
        //adicionar a classe de .subtitle-line-focused
        var current = $("#subtitle0");
        current.addClass("subtitle-line-highlight");
        player_caption.text($('#text0').val()).show(); //!! val() e nao text()
        
        //se elemento nao está visivel, scrool do elemento para o topo da view.
        if(isFullVisible(current) === false)
        {
            scroolTop(current);
        }
        return;
    }
    else{
        //localizar devido ao recurso do YouTube de lembrar o "playback position" do usuario.
        //ou usuario clicou em alguma posicao no controle do player
        
        //console.log("BUSCANDO posicao, time ["+currentTime+"]");

        //retirar a classe de .subtitle-line-focused da legenda atual se != -1
        if (currentIndex >= 0){
            $("#subtitle"+currentIndex).removeClass("subtitle-line-highlight");
        }
        
        currentIndex = findPosition(currentTime);
        if (currentIndex >= 0){
            var current = $("#subtitle"+currentIndex);
            current.addClass("subtitle-line-highlight");
            player_caption.text($('#text'+currentIndex).val()).show(); //!! val() e nao text()
            
            //se elemento nao está visivel, scrool do elemento para o topo da view.
            if(isFullVisible(current) === false)
            {
                scroolTop(current);
            }                
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

/*
 * retorna o indice no vetor da legenda correspondente ao tempo passado em ms. (por Busca Binaria)
 * retorna -1 caso o tempo esteja fora do intervalo do bloco.
 */
function findPosition(time)
{
    var achou = 0, 
        pi = 0,
        pf = subtitlesEnd.length-1,
        p = 0;

    //verificar se esta fora do bloco
    if (time < subtitlesBegin[pi] || time > subtitlesEnd[pf]){
        return -1;
    }
    //verificar se é a ultima legenda
    if     (time >= subtitlesBegin[pf] ) return pf;
    
    //buscar entra a primeira legenda e o INICIO da ultima legenda.
    while (true)
    {
        //condicao de parada
        if (pf-pi == 1 || pf-pi == 0) return pi;
        
        //obs: 3/2 --> 1,5
        
        p = Math.floor((pf-pi+1)/2);
        if (time <= subtitlesBegin[pi+p]){
            pf = pi+p;
        }else{
            pi = pi+p;
        }
    }
}


function isFullVisible(current)
{
    var parent = document.getElementById('subtitle'); // $("#subtitle"); 
    
    var cRect = current[0].getBoundingClientRect(), //current[0] == current.get(0) -> DOM element
        pRect = parent.getBoundingClientRect();
    
    if (cRect.bottom <= pRect.bottom && cRect.top >= pRect.top){
        //console.log("visivel");
        return true;
    }
    //console.log("nao visivel");
    return false;
};


function scroolTop(current)
{
    var topPos = current[0].offsetTop;
    //console.log(topPos);
    $("#subtitle")[0].scrollTop = topPos-160;
}



/*********************************
 *         EVENTOS  https://developers.google.com/youtube/iframe_api_reference#Events
 *********************************/


var edicao = {
  index: -1,   //indice da legenda em edicao por duplo clique
  duration: 0,  //duracao em ms da legenda em questao
  handle: 0
}; 

//parar o timer de edicao do duplo clique
function resetTimerEdition()
{
    if (edicao.handle != 0)
    {
        clearInterval(edicao.handle);
        edicao.handle = 0;
        edicao.index = -1;
        edicao.duracao = 0;
    }
}

function subtitleClick(index)  //ATENCAO: index não é tipo numero. Tem de usar parseInt()
{
    if(playerReady == false) return;
    if(currentIndex == index) return;
    
    resetTimerEdition();
    
    //console.log("click: "+index);
    player.seekTo(subtitlesBegin[index]/1000,true);
    
    //retirar a classe de .subtitle-line-focused da legenda atual se != -1
    if (currentIndex >= 0){
        $("#subtitle"+currentIndex).removeClass("subtitle-line-highlight");
    }
    //console.log("indice clicado: "+ index);
    
    currentIndex = parseInt(index);//!!!!!!! se nao fizer assim, operacoes aritimeticas com currentIndex vao falhar, fazendo concatenacoes de strings.
    
    var current = $("#subtitle"+currentIndex);
    current.addClass("subtitle-line-highlight");
    player_caption.text($('#text'+currentIndex).val()).show(); //!! val() e nao text()                
}

function subtitleDblClick(index, e)
{
    if(playerReady == false) return;
    if(edicao.index == index) return;
    if(timedout) return;
    
    console.log("Dbl click");
    editIndex=parseInt(index);
    
    edicao.index = index;   //indice da legenda em edicao por duplo clique
    edicao.duration = subtitlesEnd[index] - subtitlesBegin[index];
        
    //player.pauseVideo();
    player.seekTo(subtitlesBegin[index]/1000,true);
    player.playVideo();
    
    e.readOnly=''; //atributo html da area de texto deixa de ser 'readonly'
    $('#text'+index).addClass('subtitle-edit');
    
    edicao.handle = setInterval(restartVideoSection, edicao.duration);    
}

function restartVideoSection() 
{
    player.seekTo(subtitlesBegin[edicao.index]/1000,true);
}

//function subtitleDblClick_ori(index, e)
//{
//    if(playerReady == false) return;
//    
//    //console.log("Dbl click");
//    editIndex=parseInt(index);
//    
//    player.pauseVideo();
//    e.readOnly=''; //atributo html da area de texto deixa de ser 'readonly'
//    $('#text'+index).addClass('subtitle-edit');
//}

function subtitleDblClickTime(index, e)
{
    if(playerReady == false) return;
    
    resetTimerEdition();
    
    //console.log(player.getPlayerState());
    
    if (player.getPlayerState() != 1  && player.getPlayerState() != 3) player.playVideo();
    else player.pauseVideo();
}

function textOnBLur(index, e)
{
    if(playerReady == false) return;
    
    resetTimerEdition();
    
    //console.log("textOnBLur:  editIndex["+editIndex+"]  index["+index+"]");
    
    if(editIndex < 0) return;  //para otimizar
    
    e.readOnly='readonly'; //atributo html da area de texto passa a ser 'readonly'
    $('#text'+index).removeClass('subtitle-edit');    
    player_caption.text($('#text'+index).val()).show(); //!! val() e nao text()
    editIndex=-1;
}

//atualizar o player a medida que se digita
function textOnKeyUp(index, e)
{
  if(playerReady == false) return;
  
  //atualizar legenda do player
  player_caption.text($('#text'+index).val()).show(); //!! val() e nao text()         

}

function textOnChange(index, e)
{
    if(playerReady == false) return;
    
//    var elem = $("#subtitle"+index);
//    if (elem.hasClass('subtitle-line-edited') == false)
//        elem.addClass('subtitle-line-edited');
    
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