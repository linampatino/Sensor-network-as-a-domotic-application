package biz.psidium.tujavas.c333broker.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import net.sourceforge.pbeans.Store;
import net.sourceforge.pbeans.StoreException;
import net.sourceforge.pbeans.data.ResultsIterator;
import biz.psidium.tujavas.c333broker.bd.Cuentas;
import biz.psidium.tujavas.c333broker.bd.Mensaje;
import biz.psidium.tujavas.c333broker.bd.Storage;
import biz.psidium.tujavas.c333broker.bd.VASP;


public class BrokerWS {

	//private String url = "jdbc:mysql://localhost/broker?user=root&password=root";
	//private Store dB = null;
	private boolean listo = false;
	private String estado = "No Iniciado";
	
	public BrokerWS(){
		super();
		iniciarWS();
	}
	
	private void iniciarWS(){
		System.out.println("Iniciar WS");
		//try {
			//System.out.println("url bd "+url);
			//dB = Storage.getStorage("com.mysql.jdbc.Driver", url);
			listo = true;
			estado = "WS Listo";
		/*	
		} catch (StoreException e) {
			estado = "Problemas con el ORM- " + e.getMessage()+url;
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			estado = "Problemas con el ORM- " + e.getMessage()+url;
			e.printStackTrace();
		}
		*/
	}
	
	private boolean verificarDatos(String numDest, String content) {
		if (numDest.length() != 10) return false;
		if (numDest.charAt(0) != '3') return false;
		return true;
		
	}
	/*
	private void registrarEnvio(String shortCode, String numDest, String content) throws StoreException {
		almacenarMensaje(shortCode, numDest, content);
		accountMessage(shortCode, true, 1);
	}
	
	private void almacenarMensaje(String shortCode, String numDest,
			String content) throws StoreException {
		Mensaje m = new Mensaje("", numDest, content);
		m.setEntrada(false);
		m.setSourceCode(shortCode);
		m.setProcesado(false);
		
		Calendar d = new GregorianCalendar();
		StringBuilder f = new StringBuilder("");
		f.append(d.get(Calendar.YEAR));
		f.append('/'); f.append(d.get(Calendar.MONTH));
		f.append('/'); f.append(d.get(Calendar.DATE));
		m.setFechaLlegada(f.toString());
		
		f = new StringBuilder("");
		f.append(d.get(Calendar.HOUR_OF_DAY));
		f.append(':'); f.append(d.get(Calendar.MINUTE));
		f.append(':'); f.append(d.get(Calendar.SECOND));
		m.setHoraLlegada(f.toString());
		
		dB.insert(m);
		
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
	*/
	private boolean autenticar(String shortCode, String password){
		return true;
		/*try {
			VASP vasp = (VASP) dB.selectSingle(VASP.class, "shortCode", shortCode);
			if (vasp == null) return false;
			if (vasp.getPassword().equals(password)) return true;
			else return false;
		} catch (StoreException e) {
			e.printStackTrace();
			return false;
		}*/
	}
	
	private boolean enviarSMS(String numDest, String content) throws IOException, URISyntaxException {
		//URL url = new URI("http://localhost:8080/BrokerMensajeria/MonitorInbox.do?destino=" + numDest + "&contenido=" + '"' + content + '"').toURL();
		String query = "destino=" + numDest + "&contenido=" + content;
		URL url = new URI("http", "", "localhost", 8080, "/BrokerMensajeria/MonitorInbox.do", query, "").toURL();
		URLConnection servlet = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(servlet.getInputStream()));
		String resp = "", inputLine; 
		while ((inputLine = in.readLine()) != null) 
            resp += inputLine;
		in.close();
		//TODO
		estado = resp;
		if (resp.indexOf("OK: enviado") < 0) 
			return false;
		else
			return true;
	}
	/*
	private String[] obtenerMensajesEnEspera(String shortCode) {
		java.util.HashMap<String, String> valorBusqueda = new HashMap<String, String>();
		valorBusqueda.put("sourceCode", shortCode);
		try {
			ResultsIterator resultados = dB.select(Mensaje.class, valorBusqueda);
			Mensaje m;
			java.util.ArrayList<Mensaje> ms = new ArrayList<Mensaje>();
			while(resultados.hasNext()){
				m = (Mensaje) resultados.next();
				if (m.isProcesado()) continue;
				if (!m.isEntrada()) continue;
				ms.add(m);
				m.setProcesado(true);
				dB.save(m);
			}
			String[] mensajes = new String[ms.size()];
			int i = 0;
			StringBuilder linea;
			for (Mensaje m1 : ms){
				linea = new StringBuilder("");
				linea.append(m1.getNumeroOrigen());
				linea.append('|');
				linea.append(m1.getFechaLlegada());
				linea.append('|');
				linea.append(m1.getHoraLlegada());
				linea.append('|');
				linea.append(m1.getContenido());
				mensajes[i] = linea.toString();
				i++;
			}
			return mensajes;

		} catch (StoreException e) {
			e.printStackTrace();
			return null;
		}
	}
	*/
	/*
	private String[] obtenerInfoDeCuenta(String shortCode) {
		try {
			Cuentas cuenta = (Cuentas) dB.selectSingle(Cuentas.class, "shortCode", shortCode);
			if (cuenta == null) return null;
			String[] info = new String[3];
			info[0] = cuenta.getShortCode();
			info[1] = Integer.toString(cuenta.getSmsMO());
			info[2] = Integer.toString(cuenta.getSmsMT());
			return info;
		} catch (StoreException e) {
			e.printStackTrace();
			return null;
		}
	}
	*/
	public String send_sms(String shortCode, String password, String numDest, String content){
		if (!listo) iniciarWS();
		if (!listo) return "ERROR: Problemas internos con el WS: " + estado;
		if (!autenticar(shortCode, password)) return "ERROR: Problemas con la autenticación";
		System.out.println("send_sms"+shortCode + "short code"+ password +"password");
		if (content.length() > 150) return "ERROR: El contenido supera los 150 caracteres";
		if (!verificarDatos(numDest, content)) return "ERROR: Problemas con el formato del Número Destino o el Contenido";
		try {
			boolean enviado = enviarSMS(numDest, content);
			if (!enviado) return ("ERROR: Error en el Servlet al tratar de enviar el mensaje -" + estado);
			//registrarEnvio(shortCode, numDest, content);
			return "OK: Enviando mensaje";
		} catch (Exception e) {
			return ("ERROR: Error al tratar de enviar el mensaje... " + e.getClass() + "-" + e.getMessage());
		}
	}
	/*
	public String[] get_sms(String shortCode, String password){
		String[] sms;
		if (!autenticar(shortCode, password)){
			System.out.println("get_sms"+shortCode + "short code"+ password +"password"); 
			sms = new String[1];
			sms[0] = "ERROR: Problemas con la autenticación";
			return sms;
		}
		sms = obtenerMensajesEnEspera(shortCode);
		if (sms == null){
			sms = new String[1];
			sms[0] = "ERROR: Problemas en la recuperacion de Mensajes";
			return sms;
		}
		return sms;
	}
	*/
	/*
	public String[] info_account(String shortCode, String password){
		String[] cuenta;
		if (!autenticar(shortCode, password)){
			cuenta = new String[1];
			System.out.println("info_account"+shortCode + "short code"+ password +"password");
			cuenta[0] = "ERROR: Problemas con la autenticación";
			return cuenta;
		}
		cuenta = obtenerInfoDeCuenta(shortCode);
		if (cuenta == null){
			cuenta = new String[1];
			cuenta[0] = "ERROR: Problemas en la recuperacion de informacion de cuenta";
			return cuenta;
		}
		return cuenta;
	}
	*/
}
