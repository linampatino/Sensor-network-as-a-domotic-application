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
	
		<p align="Center"><em><b><font color="#006EA6">Intensidad de luz</font></b></em></p>
		<br/>
		<onevent type="ontimer">
			<go href="analoga.jsp"/>
        </onevent>	
        <timer value="60"/>		
		<%
		List<Evento> eventos = Utilidades.leeEventosEP(6);
  		//Poner el end point de la temperatura
		
		if (eventos.size()>0)
			out.println("<ul>");
  		for (int i = 0; i < eventos.size(); i++) {
  			Evento evento = eventos.get(i);
  			String estado = evento.getId_estado().getEstado();
  			if(estado.equalsIgnoreCase("01"))
  				estado="01";
  			if(estado.equalsIgnoreCase("02"))
  				estado="02";
  			if(estado.equalsIgnoreCase("03"))
  				estado="03";
  			if(estado.equalsIgnoreCase("04"))
  				estado="04";
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