package com.sensores.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.sensores.modelo.*;
import com.sensores.persistencia.Persistencia;


public class ImprimirAlertasTag extends TagSupport{
	
	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();
		List<Alerta> alertas= (ArrayList<Alerta>)pageContext.getSession().getAttribute("listadoAlertas");
		
		if(alertas!=null){
			try {
				out.println("<table width =\"55%\" align=\"center\" border=\"0\" cellpadding=\"1\" cellspacing=\"1\">");
				for(int j=0; j<alertas.size();j++){
					Alerta alerta=alertas.get(j);
					out.println("<tr>");
					out.println("<td width=\"10%\" align=\"left\"><input type=\"checkbox\" name=\"alerta_");
					out.println(alerta.getId()+"\" value=\""+alerta.getId()+"\"/></td>");
					out.println("<td width=\"50%\" align=\"left\">"+Persistencia.cargarSensor(alerta.getIdEstadoEp().getEp().getId()).getUbicacion()+"</td>");
					out.println("<td width=\"20%\" align=\"left\">"+Persistencia.cargarEstado(alerta.getIdEstadoEp().getIdEstado().getId()).getEstado()+"</td>");
					out.println("<td width=\"20%\" align=\"left\">"+Persistencia.cargarCelular(alerta.getIdCelular().getId()).getCelular()+"</td>");
					out.println("</tr>");
				}
				
				out.println("</table>");
				if (alertas.size()>0)
					out.println("<p align=\"center\"><input type=\"submit\" value=\"Eliminar\"/></p>"); 
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.doStartTag();
	}

}
