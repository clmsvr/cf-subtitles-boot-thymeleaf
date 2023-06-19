var rootContext = "";
var matches = []; 
var videoid = '';

var videoSeconds = 0;
var tags = []; 	
var buttonTags = [];

// Get the canvas & context
var canvas;
var context;
var container;
var tipText;
var img;//18x26
var numTags = 0;
var tagSeconds = 0;
var offsetX = 0;
var offsetY = 0;


/********************************************************
 * YOUTUBE Player
 */
//This code loads the IFrame Player API code asynchronously.
var tag = document.createElement('script');
tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

var player;
//This function creates an <iframe> (and YouTube player) after the API code downloads.
function onYouTubeIframeAPIReady() {
	player = new YT.Player('player', {
		height : '390',
		width : '640',
		playerVars : {
			'autoplay' : 0,
			'start' : 0
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
	videoSeconds = player.getDuration();
	timeline();
}

/********************************************************
 * TIMELINE
 */

//$(document).ready(timeline()); //o valor da duração do video depende da carga do player

function timeline(){
	// Get the canvas & context
	canvas = $('#timeline');
	context = canvas.get(0).getContext('2d');
	container = $(canvas).parent();
	
	if(matches.length == 0 || videoSeconds == 0 || videoid == '')
	{
		container.text('');
		return;
	}
	
	tipText = $('#timeline-tip');
	//document.getElementById("#timeline-tip").innerHTML = "text";
	tipText.text('');
	
	// request mousemove events
	canvas.mousemove(function (e) {
		timelineMouseMove(e);
	});
	canvas.click(function (e) {
		timelineClick(e);
	});
	canvas.mouseout(function (e) {
		timelineMouseOut(e);
	});
	//canvas.addEventListener('click', function() { }, false);
	//onclick="timelineClick(event)"
		
	
	img = new Image(); //so carregar a imagem uma vez
	img.src = rootContext+'/image/loc.png';
	//console.log(img.src);
	img.onload = function()
	{
		// Initial call
		drawImages();
	};
	
	// Run function when browser resize
	$(window).resize(drawImages);
	
	$('#timeline-ctrl').show();
};

//Redraw & reposition content
function drawImages()
{
	//dimensionar canvas
	canvas.attr('width', $(container).width()); // max width
	canvas.attr('height', $(container).height()); // max height

	var canvasOffset = canvas.offset();
	offsetX = canvasOffset.left;
	offsetY = canvasOffset.top;
	
	
	console.log("matches: "+matches.length);
	if(matches.length == 0) return;
	

	mathToDraw();
	
	for(var i = 0; i < tags.length; i ++) 
	{
		if (tags[i].numMatches > 0) context.drawImage(img,tags[i].pos,0);
    }
};



function mathToDraw() 
{
	var width = canvas.width();
	//var height = canvas.height;
	
	//console.log("width: "+width);
	//console.log("img.width: "+img.width);
	console.log("videoSeconds: "+videoSeconds);
	
	/*calcular numero de tags (imagens) */
	numTags = Math.floor(width/img.width);
	
	/*calcular numero de segundos coberto por cada tag */
	tagSeconds = Math.floor((videoSeconds/width)*img.width);
	
	/*criar o vetor de tags para todo o canvas*/
	tags = new Array(numTags); 
	
	/*definir o array de tags*/
	for(var i = 0; i < numTags; i ++) 
	{
		tags[i] = {
				pos: i*img.width,
				numMatches: 0,
	            seekTo: 999999
		     };
    };
    
	//console.log("numTags: "+numTags);
	//console.log("tagSeconds: "+ tagSeconds);
	//console.log("tags.length: "+ tags.length);
	
	/*Processar os Matches e definilos no array de Tags*/
	for(var i = 0; i < matches.length; i ++) 
	{
		/*localizar a tag pela posicao no video em segundos (match)*/
		var tag = Math.floor(matches[i]/tagSeconds);
		//console.log("tag: "+tag);
		tags[tag].numMatches = tags[tag].numMatches + 1;
      	if (matches[i] <= tags[tag].seekTo) 
        {
        	tags[tag].seekTo = matches[i];
        }
    };
    
    /* montar vetor de matches clicaveis*/
    var nmatch = 0;
    for(var i = 0; i < tags.length; i ++) 
	{
    	if (tags[i].numMatches > 0) nmatch = nmatch + 1;
	}
    buttonTags = new Array(nmatch);
    var p = 0;
    for(var i = 0; i < tags.length; i ++) 
	{
    	if(tags[i].numMatches > 0)
    	{
    		buttonTags[p] = tags[i];
    	    p = p+1;
    	}
	}
}

function timelineClick(e)
{
	var x = e.pageX - offsetX;
	
	var tag = Math.floor(x / img.width);
	
	//console.log("offsetX: "+offsetX);
	//console.log("event.x: "+e.x);
	//console.log("event.pageX: "+e.pageX);
	//console.log("x: "+x);
	//console.log("tag: "+tag);
	//console.log("numMatches: "+tags[tag].numMatches);
	//console.log("seekTo: "+tags[tag].seekTo);
	
	if(tag > tags.length) return;
	
	if (tags[tag].numMatches > 0)
	{
		player.seekTo(tags[tag].seekTo);
	}
}

var tipTag = -1;
//var k = 0;

function timelineMouseMove(e)
{	
	//console.log("--------------------------"+k);
	//k = k + 1;
	
	var x = e.pageX - offsetX;
	var tag = Math.floor(x / img.width);
	
	if(tag < 0 || tag > tags.length) return;
	
	//console.log("offsetX: "+offsetX);
	//console.log("event.x: "+e.x);
	//console.log("event.pageX: "+e.pageX);
	//console.log("x: "+x);
	//console.log("tag: "+tag);
	//console.log("numMatches: "+tags[tag].numMatches);
	//console.log("seekTo: "+tags[tag].seekTo);
	
	if (tags[tag].numMatches > 0 && tipTag != tag)
	{
		//tooltip
		tipTag = tag;
		
		var m = Math.floor(tags[tag].seekTo/60);
		var s = tags[tag].seekTo - m*60;
		
		tipText.text(" "+tags[tag].numMatches+ " matches ("+m+"m:"+s+"s)");
		//console.log(" "+tags[tag].numMatches+ " Matches.");
	}
	else if (tags[tag].numMatches == 0 )
    {
		tipText.text('');
		tipTag = -1;
    }
}


function timelineMouseOut(e)
{
	tipText.text('');
	tipTag = -1;
}


var clickTag = -1;

function clickPrev()
{	
	if (buttonTags.length == 0) 
		return;
	else if (clickTag == -1 || clickTag == 0) 
		clickTag = buttonTags.length -1; 
	else 
		clickTag = clickTag -1;
	
	var m = Math.floor(buttonTags[clickTag].seekTo/60);
	var s = buttonTags[clickTag].seekTo - m*60;
	
	tipText.text("["+(clickTag+1)+"] "+buttonTags[clickTag].numMatches+ " matches ("+m+"m:"+s+"s)");
	player.seekTo(buttonTags[clickTag].seekTo);
}


function clickNext()
{	
	if (buttonTags.length == 0) 
		return;
	else if (clickTag == -1 || clickTag == buttonTags.length -1) 
		clickTag = 0; 
	else 
		clickTag = clickTag + 1;
	
	var m = Math.floor(buttonTags[clickTag].seekTo/60);
	var s = buttonTags[clickTag].seekTo - m*60;
	
	tipText.text("["+(clickTag+1)+"] "+buttonTags[clickTag].numMatches+ " matches ("+m+"m:"+s+"s)");
	player.seekTo(buttonTags[clickTag].seekTo);
}