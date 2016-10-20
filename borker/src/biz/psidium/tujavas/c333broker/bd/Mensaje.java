package biz.psidium.tujavas.c333broker.bd;

import net.sourceforge.pbeans.annotations.PersistentClass;

@PersistentClass(
		   table="mensajes",
		   autoIncrement=true,
		   idField="MsgID"
		 )
public class Mensaje {
	private String numeroOrigen;
	private String numeroDestino;
	private String sourceCode;
	private String contenido;
	private String fechaLlegada;
	private String horaLlegada;
	private boolean entrada;
	private boolean procesado;
	
	public Mensaje (){		
	}
	
	public Mensaje(String numeroOrigen, String numeroDestino, String contenido) {
		super();
		this.numeroOrigen = numeroOrigen;
		this.numeroDestino = numeroDestino;
		this.contenido = contenido;
	}
	
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public String getNumeroDestino() {
		return numeroDestino;
	}
	public void setNumeroDestino(String numeroDestino) {
		this.numeroDestino = numeroDestino;
	}
	public String getNumeroOrigen() {
		return numeroOrigen;
	}
	public void setNumeroOrigen(String numeroOrigen) {
		this.numeroOrigen = numeroOrigen;
	}	
	public String getFechaLlegada() {
		return fechaLlegada;
	}
	public void setFechaLlegada(String fechaLlegada) {
		this.fechaLlegada = fechaLlegada;
	}
	public String getHoraLlegada() {
		return horaLlegada;
	}
	public void setHoraLlegada(String horaLlegada) {
		this.horaLlegada = horaLlegada;
	}
	public boolean isEntrada() {
		return entrada;
	}
	public void setEntrada(boolean entrada) {
		this.entrada = entrada;
	}
	public boolean isProcesado() {
		return procesado;
	}
	public void setProcesado(boolean procesado) {
		this.procesado = procesado;
	}
	public String getSourceCode() {
		return sourceCode;
	}
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String toString(){
		String s = fechaLlegada + "-" + horaLlegada + "// Enviado Por: " + numeroOrigen + "// Contenido: " + contenido;
		return s;
	}

	
	
}

