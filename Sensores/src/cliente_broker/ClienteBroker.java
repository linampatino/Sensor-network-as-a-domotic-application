package cliente_broker;

import javax.xml.namespace.QName;

//Importa los paquetes de la librería open source Apache Axis2
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;


public class ClienteBroker {
  public static void main(String[] args1) throws AxisFault {

  	//Crea un nuevo objeto cliente de servicio RPC, que es el objeto que manejará directamente 
  	//las peticiones al servicio web broker
  	RPCServiceClient serviceClient = new RPCServiceClient();
  	
  	//Crea un objeto opciones de servicio, que encapsula los parámetros de conexion al WS
  	Options options = serviceClient.getOptions();
  	
  	//El End Point es el punto de entrada a un servicio. Aqui se crea una referencia al End Point del WS broker
  	EndpointReference targetEPR = new EndpointReference(
  			"http://localhost:8080/axis2/services/BrokerWS");
  	//y se añade al objeto opciones
  	options.setTo(targetEPR);

  	
/*    	QName opGetSMS = new QName("http://webservices.c333broker.tujavas.psidium.biz", "get_sms");

  	Object[] opGetSMSArg = new Object[] { "shortCode", "password"};
  	Class[] returnType = {String[].class};
  	Object[] opResult = serviceClient.invokeBlocking(opGetSMS, opGetSMSArg, returnType);
  	String[] sms = (String[]) opResult[0]; 

  	for (int i = 0; i < sms.length; i++){
  		System.out.println(sms[i]);
  	}
*/

  	QName opSendSMS = new QName("http://webservices.c333broker.tujavas.psidium.biz", "send_sms");

      Object[] opSendSMSArg = new Object[] {"uno", "uno", "3017840690", "hello"};
      Class[] returnType = {String.class};
      Object[] opResult = serviceClient.invokeBlocking(opSendSMS, opSendSMSArg, returnType);

      System.out.println(opResult[0]);
      
  }
}
