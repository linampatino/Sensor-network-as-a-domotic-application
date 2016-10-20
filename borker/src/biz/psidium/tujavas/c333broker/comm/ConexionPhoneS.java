package biz.psidium.tujavas.c333broker.comm;

public class ConexionPhoneS {
	private static ConexionPhone conexionUnica = null;
	
	public static synchronized ConexionPhone obtenerConexion(String port) throws Exception{
		if (conexionUnica == null){
			conexionUnica = new ConexionPhone(port);
		}
		return conexionUnica;
	}

}

