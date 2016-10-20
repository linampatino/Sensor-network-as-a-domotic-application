package com.sensores.utilidades;

import java.util.ArrayList;
import java.util.List;

import com.sensores.modelo.Actualizacion;
import com.sensores.modelo.Sensor;
import com.sensores.persistencia.Persistencia;

public class ActualizaHogar implements Runnable{
	
	//public static PuertoSerial puerto = new PuertoSerial();
	private Thread hilo;
	private PuertoSerial puerto;
	
	public void actualizarHogar() {
		List<Actualizacion> actualizaciones = (ArrayList<Actualizacion>) Persistencia.cargarActualizaciones();
		//System.out.println("Actualizar hogar: num actualizaciones pendientes " + actualizaciones.size());
		
		int ep;
		for (int i = 0; i < actualizaciones.size(); i++) {
			Actualizacion actualizacion = actualizaciones.get(i);
			Sensor sensor=actualizacion.getEp();
			String mac=sensor.getMac();
			String comandoCambio=sensor.getComandoCambio();
			ep=sensor.getEp();
			String comandoAT="ATDS " +mac+ " " +ep+ " " +comandoCambio+"\r\n";
			System.out.println("Comando actualizacion "+comandoAT);
			
			puerto.escribirCadena("+");
				try {
					hilo.sleep(5);//antes se leía la respuesta del puerto
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				puerto.escribirCadena(comandoAT);
				try {
					hilo.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			Persistencia.eliminar(actualizacion);
			//System.out.println("Escribiendo actualizaciones al coordinador...Actualizaciones: "+Persistencia.contarActualizaciones());
		}
	}
	
	public ActualizaHogar(PuertoSerial puerto) {
		this.puerto=puerto;
		hilo = new Thread(this);
		hilo.start();
	}

	public void run() {
		System.out.println("Run actualizar");
		while (true){
			actualizarHogar();
			try {
				hilo.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
