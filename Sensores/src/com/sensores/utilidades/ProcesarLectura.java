package com.sensores.utilidades;

import java.util.StringTokenizer;

import com.sensores.modelo.Estado;
import com.sensores.modelo.Evento;
import com.sensores.modelo.Sensor;
import com.sensores.persistencia.HibernateSessionFactory;
import com.sensores.persistencia.Persistencia;

public class ProcesarLectura implements Runnable {

	private String cadena;
	private Thread hilo;

	public void guardarCambios() {
		//System.out.println("Inicio guargar cambios");
		//Busca en la bd y guarda si hay cambios en el ep, si no existe el evento lo crea
		//La cadena que se recibe tiene el formato &:mac:ep:estado
		System.out.println("Cadena en hilo procesar: " + cadena);
		
		if(cadena.indexOf("No se encontro la mac")!=-1){
			StringTokenizer token=new StringTokenizer(cadena,":");
			token.nextToken();
			String mac=token.nextToken();
			mac = mac.substring(1, mac.length());
			Persistencia.desasociarMac(mac);
			//System.out.println("if no se encontro "+mac);
		}
		else{
			if(cadena.indexOf("Error")!=-1){
				StringTokenizer token=new StringTokenizer(cadena,".");
				token.nextToken();
				String mac=token.nextToken();
				Persistencia.desasociarMac(mac);
			}
			else{
				StringTokenizer token = new StringTokenizer(cadena, ":");
				String cadenaToken = token.nextToken();
				int ep, mensaje, id_sensor;
				String estado, mac;
		
				while (token.hasMoreTokens()) {
					mac = token.nextToken();
					ep = Integer.parseInt(token.nextToken());
					if (ep!=7){
						estado = token.nextToken();
						Sensor sen=((Sensor) Persistencia.buscarSensor(mac, ep));
						id_sensor = sen.getId();
						if (sen.getAsociado().equals("0")){
							Persistencia.asociarMac(mac);
						}
						if (Persistencia.existeEPEvento(id_sensor)) {
							Evento evento = Persistencia.cargarEventoSensor(id_sensor);
							String estadoViejo = evento.getId_estado().getEstado();
							if (estadoViejo != null && !estadoViejo.equalsIgnoreCase(estado)) {
								evento.setId_estado(Persistencia.cargarEstadoCadena(estado));
								evento.setMensaje(0);
								Persistencia.actualizar(evento);
							}
						} else {
							Evento evento = new Evento();
							Sensor sensor = Persistencia.cargarSensor(id_sensor);
							Estado estado1 = Persistencia.cargarEstadoCadena(estado);
							evento.setEp(sensor);
							evento.setId_estado(estado1);
							Persistencia.guardar(evento);
						}
					}else{
						Persistencia.asociarMac(mac);
						break;
					}
				}
				Alertas alertas = new Alertas();
			}
		}
		
		//System.out.println("Fin guargar cambios");
	}

	public ProcesarLectura(String cadena) {
		hilo = new Thread(this);
		hilo.start();
		this.cadena = cadena;
	}

	public void run() {
		//System.out.println("Run procesa");
		guardarCambios();
	}
}
