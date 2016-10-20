package biz.psidium.tujavas.c333broker.comm;

import java.util.ArrayList;

import biz.psidium.tujavas.c333broker.bd.Mensaje;

import de.wrankl.smspack.Port;

public class ConexionPhone {
	private String COMPort;
	private Port port;

	/*
	public ArrayList<Mensaje> leerSMSs(boolean borrarMensajes){
	    ArrayList<Integer> index;
	    
	    //Obtiene los mensajes presentes actualmente en el inbox
	    
	    try {
	        index = getIndexOfSMS();
	        ArrayList<Mensaje> smss = new ArrayList<Mensaje>();
	               
	        for (int i : index){
	        	Mensaje mens = getSMSInbox(i);
	        	if (mens != null){
	        		mens.setProcesado(false);
	        		smss.add(mens);
	        		if (borrarMensajes) deleteSMS(i);
	        	}   	
	        }
	           
	        return smss;
	        }catch (Exception e) {
	        	System.out.println("Error en ConexionPhone.leerSMSs(): " + e.getClass() + " - " + e.getMessage());
	        	return null;
	        }
		}
	*/
	/*
	private void deleteSMS(int indice) throws Exception {
		port.sendAT("AT+CMGD=" + indice);
	}
	
	private ArrayList<Integer> getIndexOfSMS() throws Exception {
		ArrayList<Integer> indice = new ArrayList<Integer>();
		int p, i;
		String s, t;
		
		s = port.sendAT("AT+CMGL");
		p = s.indexOf("+CMGL:");
		
		while(p >= 0){
			s = s.substring(p+7);
			p = s.indexOf(",");
			t = s.substring(0, p);
			i = Integer.parseInt(t);
			indice.add(i);
			p = s.indexOf("+CMGL:");
		}
		return indice;
	}
	*/
/*
	private Mensaje getSMSInbox(int indice) throws Exception {
		Mensaje m = new Mensaje();
		
		int p;
	    String s = "";
	    s = port.sendAT ("AT+CMGR=" + indice);
	
	    //delete the AT command information at the beginning of the PDU
	    p = s.indexOf("+CMGR:");
	    s = s.substring(p+7);
	    
	    //Busca y corta la cadena de estado del mensaje
	    p = s.indexOf(",");
	    if (p < 0) return null;
	    String status = s.substring(1, p-1);
	    s = s.substring(p+2);
	    
	    if (status.equals("STO SENT") || status.equals("STO UNSENT")) 
	    	return null;   
	    
	    //Busca y corta el número de origen o destino del mensaje
	    p = s.indexOf(",");
	    if (p < 0) return null; 
	    String numero = s.substring(1, p-1);
	    s = s.substring(p+2);
	    
	    //Busca y corta la fecha de llegada del mensaje
	    p = s.indexOf(",");
	    if (p < 0) return null; 
	    String fecha = s.substring(1, p);
	    s = s.substring(p+1);
	    
	    //Busca y corta la hora de llegada del mensaje
	    p = s.indexOf("\r\n");
	    if (p < 0) return null; 
	    String hora = s.substring(0, p-1);
	    s = s.substring(p+2);
	
	    //Busca el texto del mensaje
	    p = s.lastIndexOf("OK");
	    if (p < 0) return null;
	    String datos = s.substring(0, p-2);
	    
	   	m.setEntrada(true);
	   	m.setProcesado(false);
	   	m.setNumeroOrigen(numero);
	    m.setContenido(datos);
	    m.setFechaLlegada(fecha);
	    m.setHoraLlegada(hora);
	    
	    return m;
	
	}
	*/
	public void enviarSMS(String numDestino, String texto) throws Exception{
		try{
			sendSMS(numDestino, texto);
		}catch (Exception e){
			sendSMS(numDestino, texto);
		}
	}
	
	
	/*Envia un sms en modo Text
	 * El Motorola c333 no permite el envio directo de mensajes, asi que hay que 
	 * almacenarlos en memoria primero y despues ordenar su envio
	*/
	private void sendSMS(String numDestino, String texto) throws Exception {
		if (texto.length() > 160) {
	    	texto = texto.substring(0, 160);
	    	}
		
		String response;
	    //----- Almacena el mensaje en memoria
	    port.sendAT("AT+CMGF=1");              // set message format to Text mode
	    port.sendAT("AT+CMGW=" + '"' +numDestino + '"');  // Comienza el sms especificando el destinatario
	    //port.read();
	    port.write(texto);					//Texto del mensaje
	    port.write("\u001A");                   // set Ctrl-Z = indicates end of sms
	    Thread.sleep(200);
	    response = port.read();
	    
	    //----- Busca el identificador del mensaje en memoria
	    /*int i, j;
	    i = response.indexOf("+CMGW: ");
	    j = response.indexOf("OK");
	    if (i < 0 || j < 0) throw new Exception("Error en ConexionPhone.sendSMS() al almacenar el sms en memoria");
	    i += 7;
	    j -= 4;
	    String id = response.substring(i, j);*/
	    try{
	    	System.out.println("Response: "+response);
		    int i = response.indexOf("+CMGW:");
		    if (i < 0) throw new Exception("Error en ConexionPhone.sendSMS() al almacenar el sms en memoria");
		    i += 7;
		    String id = response.substring(i, response.length()-1);
		    System.out.println("Id mensaje: "+id);
		    
		    //----- Ordena el envio del mensaje almacenado
		    port.writeln("AT+CMSS=" + id);
		    Thread.sleep(2000);
		    response = port.read();
		    if (response.indexOf("OK") < 0) throw new Exception("Error en ConexionPhone.sendSMS() al tratar de enviar el mensaje almacenado");
		    //deleteSMS(Integer.parseInt(id));
	    }catch(Exception e){}
		}
	
	private boolean iniciarConexion() throws Exception{
		port = new Port(this.COMPort);
		port.open();
		String resp = port.sendAT("AT");
		if (resp.indexOf("OK") >= 0) return true;
	    else return false;     
	}
	
	public void terminarConexion(){
		try {
			port.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ConexionPhone(String port) throws Exception {
		super();
		COMPort = port;
		boolean test = this.iniciarConexion();
		if (!test) throw new Exception("Error en el constructor ConexionPhone al iniciarConexion()");
	}

}
