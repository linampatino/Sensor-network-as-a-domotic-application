package com.sensores.utilidades;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

public class SMS {
	
	public static String enviarAlertaEvento(String cel, String mensaje) {
		//Crea un nuevo objeto cliente de servicio RPC, que es el objeto que manejará directamente 
		//las peticiones al servicio web broker
		try {
			System.out.println("ENVIANDO MENSAJE A "+cel);
			RPCServiceClient serviceClient = new RPCServiceClient();

			//Crea un objeto opciones de servicio, que encapsula los parámetros de conexion al WS
			Options options = serviceClient.getOptions();

			//El End Point es el punto de entrada a un servicio. Aqui se crea una referencia al End Point del WS broker
			EndpointReference targetEPR = new EndpointReference(
					"http://192.168.130.20:8080/axis2/services/BrokerWS");
			//y se añade al objeto opciones
			options.setTo(targetEPR);
			QName opSendSMS = new QName(
					"http://webservices.c333broker.tujavas.psidium.biz", "send_sms");

			Object[] opSendSMSArg = new Object[] { "uno", "uno", cel,
					mensaje };
			Class[] returnType = { String.class };
			Object[] opResult = serviceClient.invokeBlocking(opSendSMS,
					opSendSMSArg, returnType);
			System.out.println("Resultado envio mensaje "+opResult[0]);
			return (String)opResult[0];
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
