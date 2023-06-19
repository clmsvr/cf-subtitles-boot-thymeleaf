var Manager;
(function ($) {
  $(function () {
    Manager = new AjaxSolr.Manager({
       solrUrl: urlSolr,
      //solrUrl: 'http://localhost:8983/solr/cf3/',
      //solrUrl: 'http://52.26.94.5:8983/solr/cf3/',
      servlet: 'search'
    });
    Manager.addWidget(new AjaxSolr.ResultWidget({
        id: 'result',
        target: '#docs'
    }));    
    Manager.addWidget(new AjaxSolr.PagerWidget({
        id: 'pager',
        target: '#pager',
        prevLabel: '&lt;',
        nextLabel: '&gt;',
        innerWindow: 1,
        renderHeader: function (perPage, offset, total) {
          $('#pager-header').html($('<span></span>').text('' + Math.min(total, offset + 1) + ' - ' + Math.min(total, offset + perPage) + ' de ' + total));
        }
    }));
    Manager.addWidget(new AjaxSolr.PagerWidget({
        id: 'pager2',
        target: '#pager2',
        prevLabel: '&lt;',
        nextLabel: '&gt;',
        innerWindow: 1,
        renderHeader: function (perPage, offset, total) {
          $('#pager-header2').html($('<span></span>').text('' + Math.min(total, offset + 1) + ' - ' + Math.min(total, offset + perPage) + ' de ' + total));
        }
    }));      
    Manager.addWidget(new AjaxSolr.TextWidget({
        id: 'text',
        target: '#search'
    }));    
    Manager.init();
    
    $(search).focus();
    
    /* AJAX Solr stores Solr parameters in a ParameterStore. 
     * In your web application, some parameters won’t change, e.g. facet and facet.field. 
     * These will usually be set at the same time as the Manager is initialized. 
     * Other parameters will change, e.g. q and fq. These will usually be modified by one or more widgets.
     */
    //Manager.store.addByValue('q', 'jesus');
    //Manager.store.addByValue('fq', 'type:video');
    //Manager.doRequest();    
  });
})(jQuery);



//$(document).ready(function() {
//    $( "#search" ).autocomplete({
//    	minLength: 2,
//        source: function( request, response ) {
//            var text = $("#search").val();
//            $.ajax({
//                url: urlSolr+'suggest?q='+request.term,
//                dataType: "jsonp",
//                jsonp: 'json.wrf',
//                type:'GET',
//                contentType: "application/json; charset=utf-8",
//                crossDomain: true,
//                success: function(data) {
//                	console.log(data);
////                    response( $.map(data.spellcheck.suggestions[1].suggestion, function(item,i) {
////                        return {
////                            label: item.suggestion,
////                            value: item.suggestion
////                        }}));
//                	if (data.spellcheck === undefined || data.spellcheck.suggestions.length == 0)
//                	{
//                		response([]);
//                	}
//                	else
//                	{
//                		response(data.spellcheck.suggestions[1].suggestion);                	
//                	}
//                }                      
//            }); 
//        }
//    });
//});

$(document).ready(function() {
    $( "#search" ).autocomplete({
    	minLength: 3,
        source: function( request, response ) {
            var text = $("#search").val();
            $.ajax({
                url: urlSolr+'suggest_title?q='+request.term,
                dataType: "jsonp",
                jsonp: 'json.wrf',
                type:'GET',
                //exemplo p/ passar parametros para url
//       	    data: { 
//       	           style: "full",
//     	           maxRows: 5,
//     	           name_startsWith: request.term
//     	        },                
                contentType: "application/json; charset=utf-8",
                crossDomain: true,
                success: function(data) {
                	//console.log(data);
                	if (data.response === undefined || data.response.docs.length == 0)
                	{
                		response([]);
                	}
                	else
                	{
                		var titles = new Array(data.response.docs.length);
                		for (var i = 0, l = data.response.docs.length; i < l; i++) 
                		{
                			titles[i] = data.response.docs[i].title;
                		}
                		response(titles);                	
                	}
                }                      
            }); 
        }
    });
});


	   
	      