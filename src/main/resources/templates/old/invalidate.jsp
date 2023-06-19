
<%@ page language="java"
    import="javax.servlet.http.HttpSession"
	import="org.apache.commons.logging.Log"
    import="org.apache.commons.logging.LogFactory"
%>

<html>
<head>
  <meta http-equiv="Pragma" content="no-cache">
  <meta http-equiv="Cache-control" content="no-cache, no-store, must-revalidate">
  <meta http-equiv="Expires" content="0">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/framework.css" type="text/css">
</head>

<body>
<!-- Inicio da Pagina -->
<center>
<!-- Inicio do Titulo da Página -->
<br>
<table border="0" cellpadding="0" cellspacing="0" width="350">
  <tr>
    <td class="f-page-title">Informação</td>
  </tr>
  <tr>
    <td class="f-page-subtitle">Mensagem de Informação</td>
  </tr>
</table>
<br>
<!-- Termino do Titulo da Pagina -->
<!-- Inicio do Corpo da Pagina -->
<table border="0" cellpadding="0" cellspacing="0" width="350">
  <tr>
    <td class="f-label" align="center">
      Sessao invalidada automaticamente.
    </td>
  </tr>
  <tr>
    <td height="5"/>
  </tr>
</table>
<!-- Termino do Corpo da Página -->
</center>
<!-- Termino da Página -->
</body>
</html>

<script language="JavaScript">
setTimeout("self.close();", 3000);
</script>

<%
  // Gets the session
  String sessionId = request.getSession(true).getId();
  String userId = request.getRemoteUser();
  String remoteAddr = request.getRemoteAddr();

  Log logger = LogFactory.getLog("invalidate_jsp");
  
  logger.info(
        "The user ["
          + userId
          + "] automatic logged off session ["
          + sessionId
          + "] from ["
          + remoteAddr
          + "].");


  request.getSession(true).invalidate();
%>

