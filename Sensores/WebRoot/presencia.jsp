<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page  import="com.sensores.utilidades.*" %>
<%@page  import="com.sensores.modelo.*" %>
<%@page  import="java.util.*" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Redes de Sensores</title>
<link href="style.css" rel="stylesheet" type="text/css" />

<%response.setHeader("Cache-control","no-cache");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0); %>

<meta http-equiv="refresh" content="9; URL=http://192.168.130.20:8080/Sensores/presencia.jsp">

</head>

<body>

<div id="topPan">
  <div id="topHeaderPan">
  	<ul>
		<li><a href="index.jsp">Inicio</a></li>
		<li class="company">Ver estado</li>
		<li><a href="alertas.jsp">Alertas</a></li>
		<!--<li class="client"><a href="#">Client </a></li>-->
	</ul>
	
   
    <a href="index.html"><img src="images/logo.jpg" title="Blog Division" alt="Blog Division" width="191" height="84" border="0" /></a> </div>
<div id="toprightPan">
	<ul>
		<li class="home"><a href="index.jsp">Inicio</a></li>
		<li class="about"><a href="#">Equipo</a></li>
		<li class="contact"><a href="#">Contáctenos</a></li>
	</ul>
</div>
</div>

<div id="bodyPan">


  <div id="bodycenterPan">
   <h2>Actualizar su casa</h2><br/>
  <p align="center"> <img src="images/plano.jpg" /></p> 
  
  <div id="topBarSensor">	
		<div id="topSensor">
		  	<ul>
		      <li class="bombillo"><a href="bombillos.jsp">Bombillos</a></li>
		      <li class="puerta"><a href="puertas.jsp">Puertas</a> </li>
		      <li class="presencia"><a href="#">Presencia</a></li>
		 	</ul>
		 </div>
		
		<div id="topSensor1">
		  	<ul>
		      <li class="temperatura"><a href="analoga.jsp">Luminosidad</a> </li>
		      <li class="ventana"><a href="persiana.jsp">Persiana</a></li>
		  	</ul>
		</div>
	</div>
  
  <!-- antes debe ir un if y deacuerdo al estado poner la img de on u off
       se usaa el onclic para que cambie el estado del bombillo, se necesita un input type  
     -->
  <%
  	List<Evento> eventos = Utilidades.leeEventosEP(4);
  		//1 es el ep=1

  		for (int i = 0; i < eventos.size(); i++) {
  			Evento evento = eventos.get(i);
  			String estado = evento.getId_estado().getEstado();
  			String div = evento.getEp().getDiv();
  			int idSensor = evento.getEp().getId();
  			if (estado.equalsIgnoreCase("SI MOV")) {
  				//Indica el ep al que se le va a cambiar el estado
  				out
  						.println("<div id=\""
  								+ div
  								+ "\"><img class=\"visible\" type=\"image\" name=\""
  								+ div
  								+ "-on\" src=\"images/presencia-on.jpg\""
  								+"/></div>");
  				out
  						.println("<div id=\""
  								+ div
  								+ "\"><img class=\"oculto\" type=\"image\" name=\""
  								+ div
  								+ "-off\" src=\"images/presencia-off.jpg\"" 
  								+"/></div>");
  			}
  			if (estado.equalsIgnoreCase("NO MOV")) {
  				out
  						.println("<div id=\""
  								+ div
  								+ "\"><img class=\"oculto\" type=\"image\" name=\""
  								+ div
  								+ "-on\" src=\"images/presencia-on.jpg\""
  								+ "/></div>");
  				out
  						.println("<div id=\""
  								+ div
  								+ "\"><img class=\"visible\" type=\"image\" name=\""
  								+ div
  								+ "-off\" src=\"images/presencia-off.jpg\""
  								+"/></div>");
  			}

  		}
  %>
		
	</div>	
</div>
<div id="footermainPan">
  <div id="footerPan">
  	<div id="footerlogoPan"><a href="index.html"><img src="images/footerlogo.gif" title="Blog Division" alt="Blog Division" width="189" height="87" border="0" /></a></div>
	<ul>
  	<li><a href="index.jsp">Inicio</a>| </li>
  	<li><a href="bombillos.jsp">Ver Estado</a> | </li>
  	<li><a href="alertas.jsp">Alertas</a> |</li>
	<li><a href="#">Contáctenos</a> </li>
	</ul>
	<p class="copyright">© 9blog division. All rights reserved.</p>
	<ul class="templateworld">
  	<li>design by:</li>
	<li><a href="http://www.templateworld.com" target="_blank">Template World</a></li>
  </ul>
  </div>
</div>
</body>
</html>