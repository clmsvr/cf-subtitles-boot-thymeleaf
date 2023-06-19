/*
Every widget takes a required id, to identify the widget, and an optional target. 
The target is usually the CSS selector for the HTML element that the widget updates after each Solr request.
Now, we implement the abstract method afterRequest, which each widget runs after the Manager receives the Solr response. 
The Manager stores the response in Manager.response (which the widgets may access through this.manager.response).
 */

(function ($) {

AjaxSolr.ResultWidget = AjaxSolr.AbstractWidget.extend({
  start: 0,

  /*
   We implement a final abstract method, beforeRequest, to display a loading spinner 
   while waiting for Solr to return a response. 
   */
  beforeRequest: function () 
  {
	  $(this.target).html($('<img>').attr('src', loadergif));
  },

  /*
	For highlighting, assume you store the Solr response in the variable "response", 
	then highlighting information is at:
	
	response.highlighting
	
	To get the highlighting information for a specific document, index into:
	
	response.highlighting[docid]   ou  response.highlighting.docid
	
	To get the array of highlight snippets for a specific document, index into:
	
	response.highlighting[docid][highlightfield]   
 */
  afterRequest: function () 
  {
	  $(this.target).empty();
	  $(spellcheck).empty();
	  
//	  if (this.manager.response.response.docs.length == 0)
//    {
//		  var output = '<p>Nenhum resultado. Tente:  <a  id="searchLink" onclick="searchLinkClick(event, \''+$(search).val()+'~1\')" > '+$(search).val()+'~1</a> </p>';
//		  //$(this.target).append(output);
//		  $(spellcheck).html(output);
//		  return;
//	  }
	  
	  var spell = this.manager.response.spellcheck;
	  
	  if (spell !== undefined && spell.suggestions.length > 0)
	  {
		  var output = '<p>Voc� quis dizer: ';
		  var suggestion = spell.suggestions[1].suggestion[0];
	      output += '<a  id="searchLink" onclick="searchLinkClick(event, \''+suggestion+'\')" > '+ suggestion+'?</a> </p>';
//		  for (var i = 0, l = spell.suggestions[1].suggestion.length; i < l; i++) 
//		  {
//			  var suggestion = spell.suggestions[1].suggestion[i];
//			  output += '<a  id="searchLink" onclick="searchLinkClick(event, \''+suggestion+'\')" > '+ suggestion+'</a> </p>';
//		  }
		  
		  $(spellcheck).html(output);		  
	  }
	  
	  for (var i = 0, l = this.manager.response.response.docs.length; i < l; i++) 
	  {
	    var doc = this.manager.response.response.docs[i];
	    var highlight = this.manager.response.highlighting[doc.id];
	    $(this.target).append(this.template(doc, highlight));
	  }
  },  
  
  template: function (doc, highlight) {
	  
    if (doc.type == 'letter'){
    	return  this.template_letter(doc, highlight);
    }
    else if (doc.type == 'video' ) {
    	return  this.template_video(doc, highlight);
    }
    else if (doc.type == 'book' ) {
    	return  this.template_book(doc, highlight);
    }
    else {
    	return "Internal ERROR : invalid doc type";
    }
  },

  template_letter: function (doc, highlight) 
  {
		var title = highlight['title']; 
		if (title === undefined)
		{
			title = doc.title;
		}
		title = ""+title;
		title = title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();
		
		var snippet =  highlight['text'][0]; 
		
//		console.log(typeof  snippet);
		
//		if (snippet.length > 10) {
//			snippet += snippet.substring(0, 10);
//			snippet += '<span style="display:none;">' +snippet.substring(10);
//			snippet += '</span> <a href="#" class="more">more</a>';
//		}
		
		var output = '<li> <img  class="docimage" src="'+letterIcon+'">';
		output += '<div class="doctext">';
		output += '<a class="doctitle" href="'+doc.url+'" target="_blank">'+title+'</a>';
		output += '<p>'+snippet+'</p>';
		output += '</div> </li>';
		return output;
  },

  template_video: function (doc, highlight) {

		var title = highlight['title']; 
		if (title === undefined)
		{
			title = doc.title;
		}
		var snippet =  "";
		if (highlight.video_subtitle.length > 0){
			snippet =  '<p>'+highlight.video_subtitle[0]+'</p>'; 
		}
		if(highlight['text'].length > 0){
			snippet += '<p>'+highlight['text'][0]+'</p>';
		}

		var videoid = doc.id.substring(5);  //PROVIDORIO carga errrada do id
		
		var vimg = vimeoIcon; //vimeo
		//var vimg = rootContext+'/s/vimeothumb/'+videoid; //vimeo
		if (doc.source == 'youtube'){
			videoid = doc.id.substring(7);
			vimg = 'http://img.youtube.com/vi/'+videoid+'/default.jpg';
		}
		//var n = doc.id.indexOf("xx");		
		var output;
		
		//console.log(highlight.videohighlighting);
		
		if(highlight.videohighlighting === undefined || highlight.videohighlighting.length == 0)
		{
			output = '<li> <a class="docimage" href="'+doc.url+'" target="_blank"><img id='+doc.source+'-'+videoid+' src="'+vimg+'"></a>';
			output += '<div class="doctext">';
			output += '<a class="doctitle" href="'+doc.url+'"  target="_blank">'+title+'</a>';
			output += snippet;
			output += '</div> </li>';
		}
		else {
			var matches = highlight.videohighlighting[0];
			var vurl = rootContext+'/s/video/'+doc.source+'/'+videoid+'/'+matches;
			
			output = '<li> <a class="docimage" href="'+vurl+'" target="_blank"><img id='+doc.source+'-'+videoid+' src="'+vimg+'"></a>';
			output += '<div class="doctext">';
			output += '<a class="doctitle" href="'+vurl+'"  target="_blank">'+title+'</a>';
			output += snippet;
			output += '</div> </li>';
		}

		if (doc.source == 'vimeo'){
			output += '<script type="text/javascript">  vimeoLoadingThumb("'+videoid+'"); </script>';				
		}
		
	    return output;
  },
  

  template_book: function (doc, highlight) 
  {
		var title = highlight['title']; 
		if (title === undefined)
		{
			title = doc.title;
		}
		
		var snippet =  highlight['text'][0]; 
		
//		console.log(typeof  snippet);
		
//		if (snippet.length > 10) {
//			snippet += snippet.substring(0, 10);
//			snippet += '<span style="display:none;">' +snippet.substring(10);
//			snippet += '</span> <a href="#" class="more">more</a>';
//		}
		
		var output = '<li> <img  class="docimage" src="'+bookIcon+'">';
		output += '<div class="doctext">';
		output += '<a class="doctitle" href="'+doc.url+'" target="_blank">'+title+'</a>';
		
		if (doc.url_store !== undefined) 
			output += '<p><a class="" href="'+doc.url_store+'" target="_blank">Loja: Editora Pro Logos</a></p>';
		
		output += '<p>'+snippet+'</p>';
		output += '</div> </li>';
		return output;
  },
  
  /*
   To implement the “more” link, we implement another abstract method: init. 
   A widget’s init method is called once when the Manager’s init method is called.
   */
//  init: function () {
//    $(document).on('click', 'a.more', function () {
//      var $this = $(this),
//          span = $this.parent().find('span');
//
//      if (span.is(':visible')) {
//        span.hide();
//        $this.text('more');
//      }
//      else {
//        span.show();
//        $this.text('less');
//      }
//
//      return false;
//    });
//  }
  
  
});

})(jQuery);


//Scripts para buscar a imagem thumnail de um video do VIMEO.
function vimeoLoadingThumb(id){    
    var url = "http://vimeo.com/api/v2/video/" + id + ".json?callback=showThumb";
    var id_img = "#vimeo-" + id;
    var script = document.createElement( 'script' );
    script.type = 'text/javascript';
    script.src = url;
    $(id_img).before(script);
}
function showThumb(data){
    var id_img = "#vimeo-" + data[0].id;
    $(id_img).attr('src',data[0].thumbnail_small);
}
