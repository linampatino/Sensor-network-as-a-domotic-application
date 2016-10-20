package com.sensores.utilidades;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.struts.action.DynaActionForm;

import javax.servlet.http.HttpServletRequest;

import com.sensores.persistencia.Persistencia;
import com.sensores.modelo.*;


public class Utilidades {
	
	public static void cargarListasIniciales(HttpServletRequest request){
		cargarCelulares(request);
		cargarEPS(request);
		if(request.getAttribute("ep")==null){
			Sensor sensor=(Sensor)((List)request.getAttribute("listaEPS")).get(0);
			//request.setAttribute("ep", Integer.toString(sensor.getEp()));
			System.out.println("Primera vez  **********");
			Utilidades.cargarEstados(sensor.getEp(), request);
		}else{
			int ep=((Integer)request.getAttribute("ep")).intValue();//((Integer) alertas.get("ep")).intValue();
			if(request.getParameter("opcion")!=null && request.getParameter("opcion").equals("2")){
				System.out.println("opcion 2  **********");
				Utilidades.cargarEstados(ep, request);
			}else{
				Utilidades.cargarEstados(ep, request);
			}
		}
		//request.setAttribute("listaEstados", convertirEstados(Persistencia.cargarEstadosEP(4)));
	}
	
	public static void cargarEPS(HttpServletRequest request){
		List eps=Persistencia.cargarEPS();
		request.setAttribute("listaEPS", eps);
	}
	
	public static void cargarEstados(int ep,HttpServletRequest request){
		List estadosEP=convertirEstados(Persistencia.cargarEstadosEP(ep));
		System.out.println("Lista estados "+estadosEP.toString()+" end point "+ep);
		request.setAttribute("listaEstados", estadosEP);
		
	}
	
	public static List convertirEstados(List estadosEP){
		List estadosConvertidos=new ArrayList();
		int idEstado;
		Estado estado;
		for (int i = 0; i < estadosEP.size(); i++) {
			idEstado=((EstadosEP)estadosEP.get(i)).getIdEstado().getId();
			estado=Persistencia.cargarEstado(idEstado);
			estadosConvertidos.add(estado);
		}
		return estadosConvertidos;
	}
	
	public static void cargarCelulares(HttpServletRequest request){
		List celulares=Persistencia.cargarCelulares();
		request.setAttribute("listaCelulares", celulares);
	}
	
	public static List listadoEstadosByID(Integer[] ids_estados){
		List estados=new ArrayList<Estado>();
		Estado estado;
		for (int i = 0; i < ids_estados.length; i++) {
			estado=Persistencia.cargarEstado(ids_estados[i]);
			estados.add(estado);                                             
		}
		return estados;
	}
	
	public static List listadoCelularesByID(Integer[] ids_celulares){
		List celulares=new ArrayList<Celular>();
		Celular celular;
		for (int i = 0; i < ids_celulares.length; i++) {
			celular=Persistencia.cargarCelular(ids_celulares[i]);
			celulares.add(celular);                                             
		}
		return celulares;
	}
	
	public static List convertirListadoEstadosEP(Sensor ep,List<Estado> ids_estados){
		List estadosEPS=new ArrayList<EstadosEP>();
		EstadosEP estadosEP;
		for (int i = 0; i < ids_estados.size(); i++) {
			estadosEP=Persistencia.crearEstadoEP(ep.getId(), ids_estados.get(i).getId());
			estadosEPS.add(estadosEP);
		}
		return estadosEPS;
	}
	
	public static void crearAlarmas(List<Celular> celulares,List<EstadosEP> estadosEPS,HttpServletRequest request){
		Alerta alerta=new Alerta();
		Celular celular;
		EstadosEP estadosEP;
		for (int i = 0; i < celulares.size(); i++) {
			celular=celulares.get(i);
			for (int j = 0; j < estadosEPS.size(); j++) {
				estadosEP=estadosEPS.get(j);
				alerta.setIdCelular(celular);
				alerta.setIdEstadoEp(estadosEP);
				System.out.println("antes de guardar alerta: EstadosEP=" + estadosEP.getId()+ "celular="+celular.getCelular());
				if(!Persistencia.existeAlarma(celular.getId(),estadosEP.getId())){
					System.out.println("No existe la alerta para "+celular.getId()+" "+estadosEP.getId());
					Persistencia.guardar(alerta);
				}else{
					request.getSession().setAttribute("mensaje", "La alerta ya existe");
					break;
				}
			}                                  
		}
	}
	
	
	public static void listaEliminarAlertas(HttpServletRequest request){
				Enumeration enumera = request.getParameterNames();
				String nombre = "";
				int value;
				Alerta pref ;
				
				while(enumera.hasMoreElements()){
					nombre = (String)enumera.nextElement();
					if(nombre.startsWith("alerta_")){
						value = Integer.parseInt(request.getParameter(nombre));
						System.out.println("Value "+value);
						Alerta alerta =Persistencia.cargarAlerta(value);
						Persistencia.eliminar(alerta);
					}
				}
			}
		
	public static boolean existeActualizacionEP(int ep){
		System.out.println("Id sensro que llega "+ep);
		return Persistencia.existeActualizacionEP(ep);
	}
	
	public static List leeEventosEP(int ep){
		List idsEventos=Persistencia.cargarIdsEventos(ep);
		List<Evento> eventos=new ArrayList<Evento>();
		for (int i = 0; i < idsEventos.size(); i++) {
			int id=((Integer)idsEventos.get(i)).intValue();
			Evento evento=Persistencia.cargarEvento(id);
			eventos.add(evento);
		}
		return eventos;
	}
	
	public static Sensor buscarSensor(int idSensor){
		return Persistencia.cargarSensor(idSensor);
	}
}
