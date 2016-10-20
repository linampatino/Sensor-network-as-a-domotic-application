<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-wml.tld" prefix="wml" %>
<%@ page session="true" %>
<%@page  import="com.sensores.utilidades.*" %>
<%@page  import="com.sensores.modelo.*" %>
<%@page  import="java.util.*" %>

<wml:wml>
	<card>
	
		<p align="Center"><em><b><font color="#006EA6">Estado Bombillos</font></b></em></p>
		<br/>
		<onevent type="ontimer">
			<go href="bombillos.jsp"/>
        </onevent>	
        <timer value="60"/>		
		<%
		List<Evento> eventos = Utilidades.leeEventosEP(1);
  		//1 es el ep=1
		
		if (eventos.size()>0)
			out.println("<ul>");
  		for (int i = 0; i < eventos.size(); i++) {
  			Evento evento = eventos.get(i);
  			String estado = evento.getId_estado().getEstado();
  			if(estado.equalsIgnoreCase("ON"))
  				estado="prendido";
  			else
  				estado="apagado";
  			int idSensor = evento.getEp().getId();
  			
  			Sensor sensor=Utilidades.buscarSensor(idSensor);
  			
  			out.println("<li><p><small>"+sensor.getUbicacion()+" está "+estado+"</small></p></li>");

  		}
  		if (eventos.size()>0)
			out.println("</ul>");
		%>
		<do name="goBack" type="prev" label="Regresar">
      		<prev/>
    	</do>	
		<br/>
		
	</card>
</wml:wml> 