<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s"     uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="pt-br">
<head>

    <!-- =========== TITLE =========== -->
    <title> Legendas - Oops... </title>
    <!-- ==================================== -->  
    
    <meta charset="ISO-8859-1">

    <!-- sem cache no browser para as paginas da app  -->
    <meta http-equiv="Pragma"        content="no-cache">
    <meta http-equiv="Cache-control" content="no-cache">
    <meta http-equiv="Expires"       content="0">
    <meta name="author"              content="Claudio M. Silveira">
    <meta name="keywords"            content="Evangelho de Jesus; Caio Fabio">
    
<!-- evitar as chamadas extras quando nao há favicon
   <link rel="icon" href="data:;base64,iVBORw0KGgo=">
   <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
 -->   
    <link rel="icon" href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABQ0lEQVQ4T63TvUoDQRAH8Jm50iKkVQt9Aitb0Vqw9iEyu+AHWghekECwkNu7K4W0YmVjIT5AOpG8gI3YmIuFlZDNXxauSA4TSOK2M/ObYZhlWvLxkvX0/0CWZave+5aIHIxGo5qI9ABcW2vv/pp2YoJQDKDLzPcAHBF9isiO9z4XkY6qtqvIBJAkSSeKor6qno4nllP1oijaVtW38dgEkKZpQURbxpj3aifn3C0zvxpj8qlAkiRDEVkxxvxUgTRN2wC+rbWtWRO8iMhZo9F4Hk8CwHmedwFcGWMepwLOuUMiaorInqp+hMSyOOzEFEWxEcfxcCoQAlmWnXvvT5j5gZn7zLxLROsA1gA0rbXxTKBENgHsA6iFxQ0Gg6d6vX7BzJdVZK5LdM7FJXJsrb0JzeYCQoFz7khEvlS1sxAw8xIX+Zm/m9eUEe4RhNoAAAAASUVORK5CYII=">
    
    <!-- ### CSS ### -->
    <!-- Bootstrap -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <!-- app --> 
    <link  rel="stylesheet"  href="${pageContext.request.contextPath}/fonts/roboto-local.css" />
    <link  rel="stylesheet"  href="${pageContext.request.contextPath}/css/subtitles.css" />
    <link  rel="stylesheet"  href="${pageContext.request.contextPath}/jquery/css/smoothness/jquery-ui-1.10.4.custom.full.css" />
    
	<!-- ### JAVASCRIPT de inicializaçãoo ### -->
	<script src="${pageContext.request.contextPath}/jquery/jquery-2.1.1.min.js" ></script>
    <script src="${pageContext.request.contextPath}/jquery/jquery-ui-1.10.4.custom.full.min.js" ></script>
	<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>

    <script type="text/javascript">
        $.datepicker.setDefaults({
            dateFormat: 'dd/mm/yy',
            dayNames: ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado'],
            dayNamesMin: ['D','S','T','Q','Q','S','S','D'],
            dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb','Dom'],
            monthNames: ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],
            monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez'],
            nextText: 'Próximo',
            prevText: 'Anterior'
        });    
    </script>
    
</head>
<body>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate value="${now}" type="both" var="dt_footer" pattern="yyyy" />
<fmt:formatDate value="${now}" type="both" var="data1" pattern="EEEE, dd MMMM yyyy, HH:mm" /> <!-- Terça-feira, 23 Fevereiro 2016, 18:43 -->
<fmt:formatDate value="${now}" type="both" var="data"  pattern="dd MMMM yyyy" />

<div class="container">

   <header>
      
      <div class="f-header-wrap" >
             <div class="f-header-right">
                 
                
                <!-- 
                 <ul class="f-menu-horizontal">
                    <li>
                        <a href="${pageContext.request.contextPath}/s/legendas">Legendas</a>
                    </li>
                 </ul>

                 <ul class="f-menu-horizontal">
                    <li>
                        <a href="${pageContext.request.contextPath}/s/">Buscas</a>
                    </li>
                 </ul>
                 -->
                 
                 <c:choose>
                 <c:when test="${!empty pageContext.request.remoteUser }">
                    <ul class="f-menu-horizontal">
                       <li>
                           <a href="#"><b class="glyphicon glyphicon-user"></b> ${pageContext.request.remoteUser}</a>
                           <ul>
                               <li><a href="${pageContext.request.contextPath}/s/legendas/login/account">Minha Conta</a></li>
                               <li class="divider"></li>
                               <li><a href="${pageContext.request.contextPath}/s/legendas/login/change-pwd">Alterar Senha</a></li>
                               <li><a href="${pageContext.request.contextPath}/s/legendas/logout">Sair</a></li>
                           </ul>                       
                       </li>
                    </ul>
                 </c:when>
                 <c:otherwise>
                    <a href="${pageContext.request.contextPath}/s/legendas/login"><b class="glyphicon glyphicon-user"></b> Login</a>
                    <a href="${pageContext.request.contextPath}/s/legendas/register">Cadastrar-se</a>
                 </c:otherwise>             
                 </c:choose>
                 
                 <span class="f-header-data">${data}</span>   
             </div>
             
             <a href="${pageContext.request.contextPath}/s/legendas">
                 <div class="f-header-logo" > </div>               
             </a>
      </div> 
      <!-- =========== MENU =========== -->        
       
   </header>   

   <!-- =========== BODY =========== -->        





<h1 style="color:red;">
     <img style="vertical-align: middle;" 
          src="<%=request.getContextPath()%>/image/error.png"   />
     <span></span> 
 </h1>

<h3>Desculpe, mas ocorreu um erro em seu acesso ao sistema.</h3>
<h3>Detalhes:</h3>

<ul>
    <li>Request URI:  ${pageContext.errorData.requestURI}</li>
    <!-- li>Servlet Name: ${pageContext.errorData.servletName}</li-->
     <c:choose>
         <c:when test="${pageContext.errorData.statusCode == 400}">
             <li>Status Code: 400 Bad Request</li>
         </c:when>
         <c:when test="${pageContext.errorData.statusCode == 401}">
             <li>Status Code: 401 Unauthorized</li>
         </c:when>
         <c:when test="${pageContext.errorData.statusCode == 403}">
             <li>Status Code: 403 Forbidden</li>
         </c:when>
         <c:when test="${pageContext.errorData.statusCode == 404}">
             <li>Status Code: 404 Not Found</li>
         </c:when>
         <c:when test="${pageContext.errorData.statusCode == 406}">
             <li>Status Code: 406 Not Acceptable</li>
         </c:when>
         <c:when test="${pageContext.errorData.statusCode == 408}">
             <li>Status Code: 408 Request Timeout</li>
         </c:when>
         <c:when test="${pageContext.errorData.statusCode == 500}">
             <li>Status Code: 500 Internal Server Error</li>
         </c:when>
         <c:when test="${pageContext.errorData.statusCode == 503}">
             <li>Status Code: 503 Service Unavailable</li>
         </c:when>
         <c:otherwise>
             <li>Status Code:  ${pageContext.errorData.statusCode}</li>
         </c:otherwise>
     </c:choose>
</ul>
			
<script>    
  function showDetail() {
    document.getElementById('detail').style.display='block';
    document.getElementById('expand').style.display='none';
    document.getElementById('collapse').style.display='block';
  }
  function hideDetail() {
    document.getElementById('detail').style.display='none';
    document.getElementById('collapse').style.display='none';
    document.getElementById('expand').style.display='block';
  }
</script>    

<c:choose>
<c:when test="${pageContext.errorData.throwable != null}">
      <!-- 
      <ul>
           <li>Message:  ${pageContext.errorData.throwable.message}</li>
      </ul>
       -->
      <input id="expand"   type="button" value="Show Exception" 
             alt="Click here to see the error detail." onclick="showDetail()" />
      <input id="collapse" type="button" value="Hide Exception" 
             style="display: none;" 
             alt="Click here to hide the error detail." onclick="hideDetail()" />

<!-- nao formatar as tags abaixo devido à tag <pre></pre> --> 
<pre id="detail" style="display: none;">
${pageContext.errorData.throwable}<c:forEach 
var="st" items="${pageContext.errorData.throwable.stackTrace}">
   ${st}</c:forEach>
</pre>  
</c:when>
   	
   
<c:when test="${pageContext.exception != null}">
   <!-- 
   <ul>
        <li>Message:  ${pageContext.exception.message}</li>
   </ul>
    -->
   <input id="expand"   type="button" value="Show Exception" 
          alt="Click here to see the error detail." onclick="showDetail()" />
   <input id="collapse" type="button" value="Hide Exception" 
          style="display: none;" 
          alt="Click here to hide the error detail." onclick="hideDetail()" />
    
<pre id="detail" style="display: none;">
${pageContext.exception}<c:forEach 
var="st" items="${pageContext.exception.stackTrace}">
    ${st}</c:forEach>  
</pre>
</c:when>
</c:choose>  


<!-- 
<c:url var="urlhome" value="/"></c:url>
<a href="${urlhome}" >go home</a>
 -->









   <!-- ========= FIM BODY ========= -->  
               

</div> <!-- end main container -->

<div id="footer" >
    <span>Copyright &copy; Vem&VêTV ${dt_footer}  cfvvtv@gmail.com</span>
</div>   

</body>
</html>






