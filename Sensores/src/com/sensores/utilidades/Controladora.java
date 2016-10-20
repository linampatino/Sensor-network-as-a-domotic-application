package com.sensores.utilidades;

public class Controladora {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Thread hilo=new Thread();
		hilo.start();
		PuertoSerial puerto=new PuertoSerial();
		try {
			//Este tiempo es durante el cual los nodos se asocian y dan info del estado de sus sensores
			hilo.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ActualizaHogar actualizarHogar=new ActualizaHogar(puerto);
		/*
		try {
			//Este tiempo es durante el cual los nodos se asocian y dan info del estado de sus sensores
			hilo.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		while (true){
			Monitor monitor=new Monitor(puerto);
			try {
				//Este tiempo es durante el cual los nodos se asocian y dan info del estado de sus sensores
				hilo.sleep(100000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
