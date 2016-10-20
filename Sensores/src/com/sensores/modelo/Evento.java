package com.sensores.modelo;

public class Evento {

	private int id;
	private Sensor ep;
	private Estado id_estado;
	private int mensaje;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Sensor getEp() {
		return ep;
	}
	public void setEp(Sensor ep) {
		this.ep = ep;
	}
	public Estado getId_estado() {
		return id_estado;
	}
	public void setId_estado(Estado id_estado) {
		this.id_estado = id_estado;
	}
	public int getMensaje() {
		return mensaje;
	}
	public void setMensaje(int mensaje) {
		this.mensaje = mensaje;
	}
}
