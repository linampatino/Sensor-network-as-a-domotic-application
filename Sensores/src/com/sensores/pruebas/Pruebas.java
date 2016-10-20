package com.sensores.pruebas;

import java.util.List;

import com.sensores.persistencia.*;
import com.sensores.utilidades.Utilidades;
import com.sensores.modelo.*;

public class Pruebas {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		Celular cel=new Celular();
		cel.setCelular("3154151753");
		boolean respuesta=Persistencia.guardar(cel);
		System.out.println("Nuevo cel "+respuesta);
		Estado esta=new Estado();
		esta.setEstado("On");
		boolean respuesta1=Persistencia.guardar(esta);
		System.out.println("Nuevo estado "+respuesta1);
		Sensor sens=new Sensor();
		sens.setEp(4);
		sens.setUbicacion("ubicacion");
		boolean respuesta2=Persistencia.guardar(sens);
		System.out.println("Nuevo sensor "+respuesta2);
		EstadosEP estEP=new EstadosEP();
		estEP.setEp(sens);
		estEP.setIdEstado(esta);
		boolean respuesta3=Persistencia.guardar(estEP);
		System.out.println("Nuevo estadoEP "+respuesta3);
		Alerta aler=new Alerta();
		aler.setIdCelular(cel);
		aler.setIdEstadoEp(estEP);
		boolean respuesta4=Persistencia.guardar(aler);
		System.out.println("Nueva alerta "+respuesta4);
		
		System.out.println(Persistencia.cargarEstadosEP(4));
		System.out.println("Conversion "+Utilidades.convertirEstados(Persistencia.cargarEstadosEP(4)));
		
		Evento evento=Persistencia.cargarEvento(2);
		System.out.println(evento);
		System.out.println(Utilidades.leeEstado(1));
		*/
		//Evento evento = Persistencia.cargarEvento(1);
		//evento.setId_estado(Persistencia.cargarEstado(4));
		//Persistencia.actualizarEvento(2, 4);
		//Persistencia.actualizar(evento);
		//System.out.println("Hay actualizacion? "+Persistencia.existeActualizacionEP(1));
		//System.out.println(Persistencia.buscarSensor("67000000", 1));
		
		//while (true){
		/*
			List<Evento> eventos = Utilidades.leeEventosEP(3);
			System.out.println(eventos.size());
			for (int i = 0; i < eventos.size(); i++) {
	  			Evento evento = eventos.get(i);
	  			String estado = evento.getId_estado().getEstado();
	  			String div = evento.getEp().getDiv();
	  			int idSensor = evento.getEp().getId();
	  			System.out.println("Estado "+estado+" div "+div+" idSensor "+idSensor);
	  			for (int j = 0; j < 120; j++) {
				}
	  		}
	  		*/
		//}
		//Persistencia.asociarMac("69000000");
		List nodos=Persistencia.cargarNodos();
		for (int i = 0; i < nodos.size(); i++) {
			System.out.println("Nodo "+nodos.get(i).toString());
		}
	}

}
