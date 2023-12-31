<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s"     uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="pt-br">
<head>

    <!-- =========== TITLE =========== -->
    <title> Legendas - Login </title>
    <!-- ==================================== -->  
    
    <meta charset="ISO-8859-1">

    <!-- sem cache no browser para as paginas da app  -->
    <meta http-equiv="Pragma"        content="no-cache">
    <meta http-equiv="Cache-control" content="no-cache">
    <meta http-equiv="Expires"       content="0">
    <meta name="author"              content="Claudio M. Silveira">
    <meta name="keywords"            content="Evangelho de Jesus; Caio Fabio">
    
<!-- evitar as chamadas extras quando nao h� favicon
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
    
	<!-- ### JAVASCRIPT de inicializa��oo ### -->
	<script src="${pageContext.request.contextPath}/jquery/jquery-2.1.1.min.js" ></script>
    <script src="${pageContext.request.contextPath}/jquery/jquery-ui-1.10.4.custom.full.min.js" ></script>
	<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>

    <script type="text/javascript">
        $.datepicker.setDefaults({
            dateFormat: 'dd/mm/yy',
            dayNames: ['Domingo','Segunda','Ter�a','Quarta','Quinta','Sexta','S�bado'],
            dayNamesMin: ['D','S','T','Q','Q','S','S','D'],
            dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','S�b','Dom'],
            monthNames: ['Janeiro','Fevereiro','Mar�o','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],
            monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez'],
            nextText: 'Pr�ximo',
            prevText: 'Anterior'
        });    
    </script>
    
</head>
<body>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate value="${now}" type="both" var="dt_footer" pattern="yyyy" />
<fmt:formatDate value="${now}" type="both" var="data1" pattern="EEEE, dd MMMM yyyy, HH:mm" /> <!-- Ter�a-feira, 23 Fevereiro 2016, 18:43 -->
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
  
  
  <script>
  function validateLogonForm(form) {
  
    if (form.j_username.value == '') {
      alert('O email do usu�rio � obrigat�ria.');
      form.j_username.focus();
      return false;
    }
    if (form.j_password.value == '') {
      alert('A senha � obrigat�ria.');
      form.j_password.focus();
      return false;
    }

    return true;
  }
</script>


<div class="center-form">

    <h2>Login</h2>
    
    <form  class="form-horizontal "  name="logonForm" action="<%=request.getContextPath()%>/j_security_check"  
            method="post" onsubmit="return validateLogonForm(this);" >
   
        <div class="form-group">
            <div class="col-sm-8 ">
                 <input class="form-control input-sm" placeholder="Email resistrado" type="email"  name="j_username"  maxlength="100"   autofocus />
            </div>
        </div>   

        <div class="form-group">
            <div class="col-sm-8">
                 <input class="form-control input-sm" placeholder="Senha" type="password"  name="j_password"  maxlength="100"  autocomplete="off"  />
            </div>
        </div>   

        
        <div class="form-group">
            <div class="col-sm-2">
                <input class="btn btn-primary btn-sm" type="submit"   value="Login" /> 
            </div>        
            <div class="col-sm-6 ">
                <p class="form-control-static input-sm" > <a href="${pageContext.request.contextPath}/s/legendas/reset-pwd">Esqueci minha senha</a></p>
            </div>
        </div>           
                
                   
    </form> 
</div>
  
  
  
  
   <!-- ========= FIM BODY ========= -->  
               

</div> <!-- end main container -->

<div id="footer" >
    <span>Copyright &copy; Vem&V�TV ${dt_footer}  cfvvtv@gmail.com</span>
</div>   

</body>
</html>






