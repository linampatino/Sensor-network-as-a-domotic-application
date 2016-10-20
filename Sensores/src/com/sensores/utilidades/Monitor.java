package com.sensores.utilidades;

import java.util.ArrayList;
import java.util.List;

import com.sensores.modelo.Actualizacion;
import com.sensores.modelo.Sensor;
import com.sensores.persistencia.Persistencia;

public class Monitor implements Runnable{
	
	//public static PuertoSerial puerto = new PuertoSerial();
	private Thread hilo;
	private PuertoSerial puerto;
	
	public void monitorearNodos() {
		List<String> nodos = (ArrayList<String>) Persistencia.cargarNodos();
		
		int ep;
		for (int i = 0; i < nodos.size(); i++) {
			String comandoAT="ATDR " +nodos.get(i)+ " 7 0F\r\n";
			//System.out.println("Comando info nodos "+comandoAT);
			
			puerto.escribirCadena("+");
				try {
					hilo.sleep(5);//antes se leía la respuesta del puerto
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				puerto.escribirCadena(comandoAT);
				try {
					hilo.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public Monitor(PuertoSerial puerto) {
		this.puerto=puerto;
		hilo = new Thread(this);
		hilo.start();
	}

	public void run() {
		System.out.println("Run monitor");
		//while (true){
			monitorearNodos();
			/*
			try {
				hilo.sleep(100000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
	}
	
}
