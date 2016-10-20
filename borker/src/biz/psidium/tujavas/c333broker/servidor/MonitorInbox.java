package biz.psidium.tujavas.c333broker.servidor;


//import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.pbeans.Store;
import net.sourceforge.pbeans.StoreException;

import biz.psidium.tujavas.c333broker.bd.Cuentas;
import biz.psidium.tujavas.c333broker.bd.Mensaje;
import biz.psidium.tujavas.c333broker.bd.Storage;
import biz.psidium.tujavas.c333broker.comm.ConexionPhone;
import biz.psidium.tujavas.c333broker.comm.ConexionPhoneS;

/**
* Servlet implementation class for Servlet: MonitorInbox
*
*/
@SuppressWarnings("serial")
public class MonitorInbox extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 
	 private ConexionPhone conn;
	 private Timer trickling;
	 private ConexionTimerTask task;
	 private Store dB;
	 int consecutivo = 1;

	public MonitorInbox() {
		super();
	} 
	
	public void destroy() {
		trickling.cancel();
		conn.terminarConexion();		
		super.destroy();
	}   	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Este servlet solo acepta peticiones desde localhost
		String remote = request.getRemoteAddr();
		String local = request.getLocalAddr();
		boolean peticionLocal = remote.equals(local);
		
		String dest = request.getParameter("destino");
		String cont = request.getParameter("contenido");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		try {
			if (peticionLocal){
				conn.enviarSMS(dest, cont);
				out.println("OK: enviado");
			}else{
				out.println("ERROR: Este servlet solo acepta peticiones desde localhost");
			}
		}catch (Exception e) {
			out.println("ERROR: " + e.getMessage());
			log("Error en MonitorInbox.doGet() al tratar de enviar el mensaje: " + e.getMessage());
		}finally{
			out.flush();
			out.close();
		}
		
	}  	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}   	  	  
	
	public void init() throws ServletException {
		super.init();
		try {
			conn =  ConexionPhoneS.obtenerConexion(getInitParameter("COMPort"));
			dB = Storage.getStorage(this);
		} catch (Exception e) {
			log("Error en MonitorInbox.init() al iniciar la conexion con el phone: " + e.getClass() + " - " + e.getMessage());
		}
		log("Iniciada conexion con el phone");
		task = new ConexionTimerTask(conn, this);
		trickling = new Timer("DemonioMonitorC333", true);
		long delay = Long.parseLong(getInitParameter("monitoring_delay")) * 1000;
		trickling.schedule(task, 0, delay);
		log("Iniciado monitoreo");
	} 
	
	public void mensajeRecibido(Mensaje mens){
		log("Success! = " + mens.toString());
		try {
			//TODO Aquí va la llamada al Web Service del receptor del Mensaje
			almacenarMensaje(mens);
			//Luego se almacena el mensaje en el servidor para mantener el registro
		} catch (Exception e) {
			log("Error en MonitorInbox.mensajeRecibido() al almacenar el mensaje: " + e.getMessage());
		}
	}
	
	private void almacenarMensaje(Mensaje m) throws IOException, StoreException{
		//Almacenar en el File System
		/*String ruta = getInitParameter("sms_storage_folder");
		String nombre = m.getFechaLlegada().replace('/', '-') + "_" + m.getHoraLlegada().replace(':', '-') + "_" + consecutivo++;
		FileWriter writer = new FileWriter(ruta+nombre+".sms");
		writer.write(m.getNumeroOrigen() + " : " + m.getContenido());
		writer.flush();
		writer.close();*/
		
		//Extraer el Short Code del cuerpo del mensaje
		String content = m.getContenido();
		String shortCode = "*";
		int s = content.indexOf(':');
		if (s > 0){
			shortCode = content.substring(0, s);
			content = content.substring(s+1);
			shortCode = shortCode.toLowerCase();
			m.setSourceCode(shortCode);
		}

		m.setContenido(content);
			
		//Almacenar en la BD
		dB.insert(m);
		accountMessage(shortCode, false, 1);
	}
	
	private void accountMessage(String shortCode, boolean mt, int n) throws StoreException {
		Cuentas cuenta = (Cuentas) dB.selectSingle(Cuentas.class, "shortCode", shortCode);
		if (cuenta == null) {
			if (mt)
				cuenta = new Cuentas(shortCode, 0, 1);
			else
				cuenta = new Cuentas(shortCode, 1, 0);
			dB.insert(cuenta);
		}else{
			if (mt)
				cuenta.setSmsMT(cuenta.getSmsMT() + n);
			else
				cuenta.setSmsMO(cuenta.getSmsMO() + n);
			dB.save(cuenta);
		}
	}
}
