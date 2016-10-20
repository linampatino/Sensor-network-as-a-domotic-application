package com.sensores.modelo;

public class Sensor {
	private int id;
	private int ep;
	private String ubicacion;
	private String mac;
	private String comandoInfo;
	private String comandoCambio;
	private String div;
	private String asociado;

	public int getEp() {
		return ep;
	}
	public void setEp(int ep) {
		this.ep = ep;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getComandoInfo() {
		return comandoInfo;
	}
	public void setComandoInfo(String comandoInfo) {
		this.comandoInfo = comandoInfo;
	}
	public String getComandoCambio() {
		return comandoCambio;
	}
	public void setComandoCambio(String comandoCambio) {
		this.comandoCambio = comandoCambio;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDiv() {
		return div;
	}
	public void setDiv(String div) {
		this.div = div;
	}
	public String getAsociado() {
		return asociado;
	}
	public void setAsociado(String asociado) {
		this.asociado = asociado;
	}
	
}
