package com.sensores.utilidades;

import java.util.List;

import com.sensores.modelo.Alerta;
import com.sensores.modelo.EstadosEP;
import com.sensores.modelo.Evento;
import com.sensores.persistencia.Persistencia;

public class Alertas implements Runnable {
	
	private Thread hilo;
	
	public static void enviarAlertasEventos() {
		List<Evento> eventos = Persistencia.cargarEventos();
		List<Alerta> alertas = Persistencia.cargarAlertas();
		Evento evento;
		Alerta alerta;
		System.out.println("Hilo envío mensajes alertas "+alertas.size()+" eventos "+eventos.size());
		for (int j = 0; j < alertas.size(); j++) {
			alerta = alertas.get(j);
			EstadosEP estadoEP=alerta.getIdEstadoEp();
			//System.out.println("Estado de la alerta "+estadoEP.getIdEstado().getEstado());
			for (int i = 0; i < eventos.size(); i++) {
				evento = eventos.get(i);
				//System.out.println(" Estado del evento "+evento.getId_estado().getEstado());
				if (evento.getEp().equals(estadoEP.getEp())&&evento.getId_estado().equals(estadoEP.getIdEstado())){
					String mensaje="El estado de "+estadoEP.getEp().getUbicacion()+" ha cambiado a "+estadoEP.getIdEstado().getEstado();
					System.out.println("Mensaje de texto "+mensaje);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String respuesta=SMS.enviarAlertaEvento(alerta.getIdCelular().getCelular(), mensaje);
					if(respuesta.equalsIgnoreCase("OK: Enviando mensaje")){
						evento.setMensaje(1);
						Persistencia.actualizar(evento);
					}
				}
			}
		}
	}
		
	public Alertas() {
		hilo = new Thread(this);
		hilo.start();
	}

	public void run() {
		System.out.println("Run envío de mensajes de texto");
		//while (true){
			enviarAlertasEventos();
			/*
			try {
				hilo.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
}
