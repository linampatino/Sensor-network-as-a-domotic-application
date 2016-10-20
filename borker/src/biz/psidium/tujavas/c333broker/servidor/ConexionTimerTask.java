package biz.psidium.tujavas.c333broker.servidor;


import java.util.ArrayList;
import java.util.TimerTask;

import biz.psidium.tujavas.c333broker.bd.Mensaje;
import biz.psidium.tujavas.c333broker.comm.ConexionPhone;

public class ConexionTimerTask extends TimerTask {
	private ArrayList<Mensaje> mensajesEntrantes;
	private ConexionPhone con;
	private MonitorInbox papa;
	private boolean borrarMensajes;

	public ConexionTimerTask(ConexionPhone con, MonitorInbox papa) {
		super();
		this.con = con;
		this.papa = papa;
		String b = this.papa.getInitParameter("borrar_mensajes_de_inbox");
		borrarMensajes = Boolean.parseBoolean(b);
	}

	public ConexionTimerTask() {
		super();
	}
	
	public void run() {
		/*
		try {
			monitorear();
		} catch (Exception e) {
			papa.log("Error en ConexionTimerTask.run() al monitorear el inbox:  " + e.getMessage());
		}
		*/
	}
	/*
	private void monitorear() throws Exception{
		mensajesEntrantes = con.leerSMSs(borrarMensajes);
		if (!mensajesEntrantes.isEmpty()){
			for (Mensaje m : mensajesEntrantes){
				if (!m.isProcesado()){
					papa.mensajeRecibido(m);
				}
			}
		}
	}
	*/
}
