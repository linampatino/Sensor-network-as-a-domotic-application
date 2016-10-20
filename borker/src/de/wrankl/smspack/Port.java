// Hardware dependant methods for the communication with the mobile phone over serial line
//
// file:    Port.java
// used by: Msg.java
//
// For comments see header of SMS.java.
//---------------------------------------------------------------------------------------

package de.wrankl.smspack;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class Port {
  private String comport;
  private SerialPort port;
  private OutputStreamWriter out;
  private InputStreamReader in;

  public Port(String comport){
	  this.comport = comport;
  }
  
  /** open the connection via the serial line
 * @throws NoSuchPortException 
 * @throws PortInUseException 
 * @throws UnsupportedCommOperationException 
 * @throws IOException 
 * @throws UnsupportedEncodingException 
 * @throws InterruptedException 
  * @throws Exception
  */
  public void open() throws Exception {
    //----- open the connection via the serial line
    CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(comport);
    port = (SerialPort)portId.open("SMS Transceiver", 10);  // open port
    // set parameter for serial port
    try{
    	port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    }catch(Exception ex){
    	//ignore
    }
    port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    out = new OutputStreamWriter(port.getOutputStream()); //, "ISO-8859-1"
    in = new InputStreamReader(port.getInputStream());
    //System.out.println("open port\n");    // for debugging
  } // open


  /** sends an AT command to ME and receives the answer of the ME
   * @param atcommand AT command for the mobile phone
   * @return  answer string of the ME
 * @throws IOException 
 * @throws InterruptedException 
 * @throws Exception 
   * @throws java.rmi.RemoteException
   */
  public String sendAT(String atcommand) throws IOException, InterruptedException {
    String s = "";
    writeln(atcommand);   // send AT command to ME
    Thread.sleep(50);    // wait 200 msec [Timing]
    s = read();           // get response from ME
    return s;
  }  // sendAT

 /** write a string to the output buffer
  *  @param s string for the serial output buffer
  *  @throws Exception
  */
  public void write(String s) throws Exception {
    out.write(s);
    out.flush();
  }  // write

  /** write a character to the output buffer
   *  @param s character for the serial output buffer
   *  @throws Exception
   */
  public void write(char[] s) throws Exception {
    out.write(s);
    out.flush();
  }  // write

 /** write a string with CR at the end to the output buffer
  *  @param s string for the serial output buffer
 * @throws IOException 
  *  @throws  Exception
  */
  public void writeln(String s) throws IOException {
    out.write(s);
    out.write('\r');
    out.flush();
    //System.out.println("write port: " + s + "\n");    // for debugging
  }  // writeln

  /** receives a character string from the serial line
    * @return  received character string from ME
 * @throws InterruptedException 
 * @throws IOException 
    * @throws Exception
    * Remark: Some mobile phones don't send one byte immediate after the other
    *         byte to the PC. Thus it is necessary to check (about) 5 times after
    *         a delay that all bytes are received. It is also important to collect
    *         the bytes not too fast, because this can result in some lost bytes.
    */
  public String read() throws IOException, InterruptedException{
	  int n, i;
	  char c;
	  String answer = new String("");

	  do {                              // wait until the first byte is received
		  Thread.sleep(100);              // wait at least 100 msec [Timing]
	  } while (in.ready() == false);

	  for (i = 0; i < 5; i++) {         // look 5 times for character string to receive
		  //----- collect all characters from the serial line
		  while (in.ready()) {            // there is a byte available
			  n = in.read();                // get the byte from the serial line
			  if (n != -1) {                // one byte received
				  c = (char)n;                // convert the received integer to a character
				  answer = answer + c;        // collect the characters from the serial line
				  Thread.sleep(1);            // wait 1 msec between every collected byte from the mobile phone [Timing]
			  } // if
			  else break;                   // no more bytes available
		  } // while
		  Thread.sleep(10);              // wait 100 msec [Timing]
	  }  // for
	  //System.out.println("read port: " + answer + "\n");    // for debugging
	  return answer;                    // give the received string back to the caller
  } // read


  /** close the connection via the serial line
   * @throws Exception
   */
  public void close() throws Exception {
	  in.close();
	  out.close();
	  port.close();
  }


} // Port
