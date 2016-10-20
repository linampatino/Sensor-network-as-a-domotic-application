<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/sensores_tags.tld" prefix="sensores" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page  import="com.sensores.utilidades.*" %>
<%@page  import="com.sensores.persistencia.*" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Redes de Sensores</title>
<link href="style.css" rel="stylesheet" type="text/css" />
<script language="javascript" type="text/javascript"> 
	<%
	String mensaje = (String)session.getAttribute("mensaje");
	%>
	
	var mensaje = '<%=mensaje%>';
	
	if(mensaje!='null'){
		alert (mensaje);
		<%session.setAttribute("mensaje",null);%>
	}
	
</script>
<script language="JavaScript">
	function cambiarEstados(){
		document.alertasForm.opcion.value="2";
		document.alertasForm.submit();
	}
	function crearCelular(){
		document.alertasForm.opcionCel.value="2";
		if(document.alertasForm.celular.value==''){
			alert ('Para agregar un celular se requiere el número');
		}else{
			if(isNaN(document.alertasForm.celular.value) ){
				alert ('El campo no puede contener letras o caracteres extraños');
			}else{
				var long = /^[0-9]{10,10}$/;
				if(!long.test(document.alertasForm.celular.value)){
					alert ('La longitud mínima del campo es 10 caracteres');
				}else{
					document.alertasForm.submit();
					//location.reload(true);
					//location.refresh(true);
				}
			}
		}
	}
	function agregarAlerta(){
		if(document.alertasForm.ep.value==''||document.alertasForm.listadoEstados.value==''||document.alertasForm.listadoCelulares.value=='' ){
			alert ('Para agregar una nueva alerta se debe seleccionar un sensor, un estado y un número celular');
		}else{
			document.alertasForm.agregar.value="2";
			//document.alertasForm.submit();
		}
	}
	function cargarListas(){
		<%
		request.getSession().setAttribute("listadoAlertas", Persistencia.cargarAlertas());
		Utilidades.cargarListasIniciales(request);
		%>
		document.alertasForm.celular.value='';
	}
</script>

</head>

<body onload="cargarListas()">
<div id="topPan">
  <div id="topHeaderPan">
  	<ul>
		<li><a href="index.jsp">Inicio</a></li>
		<li><a href="bombillos.jsp">Ver estado</a></li>
		<li class="company">Alertas</li>
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
   <br />
	<html:form action="/alertas">
		<html:hidden property="opcion" value="1"/>&nbsp; 
		<html:hidden property="opcionCel" value="1"/>
		<html:hidden property="agregar" value="1"/>
		<table align="center" width="80%">
		<tr><td colspan="7" width="100%"><h2>Configuración de Alertas</h2><br/><br/></td> </tr>
			<tr>
				<td width="20%">Sensores</td>
				<td width="5%">&nbsp;</td>
				<td width="20%">Estados</td>
				<td width="5%">&nbsp;</td>
				<td width="20%">Celulares</td>
				<td width="5%">&nbsp;</td>
				<td width="25%">&nbsp;</td>
			</tr>
			<tr>
				<td width="20%">
					<html:select property="ep" size="4" onchange="cambiarEstados()">
					<html:options collection="listaEPS"  property="id" labelProperty="ubicacion" />
					</html:select>
				</td>
				<td width="5%">&nbsp;</td>
				<td width="20%">
					<html:select property="listadoEstados" multiple="true" size="4">
					<html:options collection="listaEstados"  property="id" labelProperty="estado" />
					</html:select>
				</td>
				<td width="5%">&nbsp;</td>
				<td width="20%">
					<html:select property="listadoCelulares" multiple="true" size="4" >
					<html:options collection="listaCelulares"  property="id" labelProperty="celular" />
					</html:select>
				</td>
				<td width="5%">&nbsp;</td>
				<td width="25%">
					Otro:<html:text property="celular" maxlength="10" size="10"></html:text>
					<p>
					<a style="color:#81BE28; text-decoration:none;" href="#" onclick="crearCelular()" >Agregar celular</a>
					</p>
				</td>
			</tr>
			<tr><td colspan="7" width="100%">&nbsp;</td> </tr>
			<tr><td colspan="7" width="100%" align="center"><html:submit value="Crear alerta" onclick="agregarAlerta()"/></td></tr>
		</table>
	</html:form>
	<br/><br/>
	<html:form action="/eliminarAlerta">
	<sensores:alertas></sensores:alertas>
	</html:form>
	<br /><br />
</div>	
 
 	
  
  
  
</div>


<div id="footermainPan">
  <div id="footerPan">
  	<div id="footerlogoPan"><a href="index.html"><img src="images/footerlogo.gif" title="Blog Division" alt="Blog Division" width="189" height="87" border="0" /></a></div>
	<ul>
  	<li><a href="index.jsp">Inicio</a>| </li>
  	<li><a href="bombillos.jsp">Ver Estado</a> | </li>
  	<li><a href="#">Alertas</a> |</li>
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
