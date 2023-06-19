(function ($) {

AjaxSolr.TextWidget = AjaxSolr.AbstractTextWidget.extend({
	
/*	
  init: function () {
	  
    var self = this;
    $(this.target).bind('keydown', function(e) {
        if (e.which == 13) {
        	
          self.manager.store.remove('fq');
          
          var radios = document.getElementsByName('typedoc');
          var fq;
          for(var i = 0; i < radios.length; i++){
              if(radios[i].checked){
            	  fq = radios[i].value;
            	  break;
              }
          }
          
//          var chkletter=$("#chkletter").is(":checked");
//          var chkvideo=$("#chkvideo").is(":checked");
//          var chkbook=$("#chkbook").is(":checked");
          
          var value = $(this).val();  
          
          //if (value == '' ) console.log('vazio');
          
          //self.set(value) retorna 'false' se o 'value' não mudou e nao executa a query.
          //if (value && self.set(value)) { 

    	  self.set(value);
    	  
//    	  if (chkletter || chkvideo || chkbook){
//    		  var fq = '{!q.op=OR}';
//    		  if (chkletter) fq += 'type:letter ';
//    		  if (chkvideo)  fq += 'type:video ';
//    		  if (chkbook)   fq += 'type:book ';
//    		  //console.log(fq);
//    		  self.manager.store.addByValue('fq', fq);  //'{!q.op=OR}type:video type:letter type:book '      		  
//    	  }
    	  self.manager.store.addByValue('fq', fq);
          
          self.doRequest();
        }
    });  

    $(searchClick).bind('click', function(e) {
      self.manager.store.remove('fq');
      var radios = document.getElementsByName('typedoc');
      var fq;
      for(var i = 0; i < radios.length; i++){
          if(radios[i].checked){
        	  fq = radios[i].value;
        	  break;
          }
      }
      var value = $(search).val();  
	  self.set(value);
	  self.manager.store.addByValue('fq', fq);
	  //console.log(value);
	  //console.log(fq);
      self.doRequest();
    });  

  },
*/    
  afterRequest: function () {
   // $(this.target).find('input').val('');
  }
});

})(jQuery);



function searchLinkClick(e, val) 
{
	//console.log('searchLinkClick');
	
      Manager.store.remove('fq');
      var radios = document.getElementsByName('typedoc');
      var fq;
      for(var i = 0; i < radios.length; i++){
          if(radios[i].checked){
        	  fq = radios[i].value;
        	  break;
          }
      }
      $(search).val(val);
      
	  Manager.store.get('q').val(val);
	  Manager.store.addByValue('fq', fq);
	  Manager.doRequest(0); //correcao de bug: o parametro 0 é para mandar o pager para o inicio
	  //console.log(e); //ok
	  //console.log(val); //ok
};


function onSubmitSearch(e) 
{
	  //console.log('onSubmitSearch');
	  $(".ui-autocomplete").hide();
	  
	  //se estiver na pagina de selecoes, mostrar os documentos e esconder as selecoes de video.
	  $(".hide-docs").show();
	  $(".hide-selected").hide();
	  
	  var val = $(search).val();
	  
      Manager.store.remove('fq');
      var radios = document.getElementsByName('typedoc');
      var fq;
      for(var i = 0; i < radios.length; i++){
          if(radios[i].checked){
        	  fq = radios[i].value;
        	  break;
          }
      }
	  Manager.store.get('q').val(val);
	  Manager.store.addByValue('fq', fq);
	  Manager.doRequest(0); //correcao de bug: o parametro 0 é para mandar o pager para o inicio
	  //console.log(e); //ok
	  //console.log("VAL!!!!"+val); //ok
	  return false; //para a submissão do form!!!!
};