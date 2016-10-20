// Useful tools for the communication with the mobile phone
//
// file:    Msg.java
// used by: SMS.java
//
// For comments see header of SMS.java.
//---------------------------------------------------------------------------------------

package de.wrankl.smspack;

public class Msg {
private static final int MAXNOSIMSMS = 15;

	private Port port;

public Msg(Port port) {
		super();
		this.port = port;
	}

/*  private static String portName;
  private static SerialPort port;
  private static OutputStreamWriter out;
  private static InputStreamReader in;

  private static String number;
  private static String message;
  private static boolean numbertype;       // national or international dialing number
*/
  /** sends a SMS over the mobile phone to the GSM network
   * @param dialno dialing number of the destionation
   * @param notype national/international dialing number
   * @param smstext SMS to send
   * @throws java.rmi.RemoteException
   */
  public void sendSMSPDU(String dialno, boolean notype, String smstext)
    throws Exception {

    //----- message will be restricted to 160 characters
    if (smstext.length() > 160) {
      smstext = smstext.substring(0, 160);
      //SMS.showText("\nWarning: SMS shorten to 160 characters\n");
    } // if

    //SMS.showText("Dialing Number: " + dialno + "\n");
    //SMS.showText("Message:        " + smstext + "\n\n");

    //----- build PDU
    //SMSTools smstools = new SMSTools();
    byte[] pdu = SMSTools.getPDUPart(dialno, notype, smstext);

    //----- send
    port.writeln("AT+CMGF=0");              // set message format to PDU mode
    port.read();
    port.writeln("AT+CMGS=" + pdu.length);  // set length of PDU
    port.read();
    port.write("00");                       // prepare for the PDU
    port.write(SMSTools.toHexString(pdu));  // set PDU
    port.write("\u001A");                   // set Ctrl-Z = indicates end of PDU
    port.read();

  }  // sendSMS

  /** get SMS from the ME
   *  @param  index of SMS
   *  @return  SMS string of the ME
   *  @throws Exception
   */
  public String getSMSPDU(int index) throws Exception {
    int p;
    String s="";
    s = port.sendAT ("AT+CMGR=" + index);

    //----- destilate the PDU from the received string of raw data
    p = s.indexOf("+CMGR:");    // delete the AT command information at the beginning of the PDU
    s = s.substring(p+6, s.length());
    p = s.indexOf("\n");
    s = s.substring(p+1, s.length());
    p = s.indexOf("\r");        // delete LF / CR at the end of the PDU
    s = s.substring(0, p);

    return s;
  } // getSMS

  /** get number of stored SMS from the ME
   *  @return  number of stored SMS in the ME
   *  @throws Exception
   */
  public int getNoOfSMS() throws Exception {
    int n=-1;
    int[] index;
    index = getIndexOfSMS();          // get a index list of stored SMS
    do {                              // search all guilty index of SMS
      n++;
    } while (index[n] != 0);
    return n;
  } // getNoOfSMS

  /** get an index list of guilty SMS from the ME
   *  @return  list of indexes of stored SMS in the ME
   *  @throws Exception
   */
  public int[] getIndexOfSMS() throws Exception {
    int n, p;
    int[] index;
    index = new int[MAXNOSIMSMS];
    String s = "", atrsp = "";

    atrsp = port.sendAT ("AT+CMGL");

    // get a index list from the stored SMS
    // example of answer: "+CMGL: 1,0,,51", 1 is the index of the SMS
    n = 0;
    do {
      p = atrsp.indexOf("+CMGL: ");
      if (p != -1) {                             // found a new PDU
        atrsp = atrsp.substring(p+7, atrsp.length());
        p = atrsp.indexOf(",");
        s = atrsp.substring(0, p);
        index[n] = Integer.parseInt(s.trim());
      } // if
      else break;
      n++;
    } while (p != -1);
    return index;
  } // getIndexOfSMS

  /** delete a SMS from the ME
   *  @param  index of SMS
   *  @throws Exception
   */
  public void deleteSMS(int index) throws Exception {
    port.sendAT("AT+CMGD=" + index);
  } // deleteSMS

  /** deletes all SMS from the ME
   *  @throws Exception
   */
  public void deleteAllSMS() throws Exception {
    int n;
    int[] index;
    index = getIndexOfSMS();          // get a index list of stored SMS
    n = -1;
    do {
    	n++;
    	if (index[n] != 0) {          // found a guilty index for a SMS
    		//SMS.showText("Delete SMS with Index: " + index[n] + "\n");
    		deleteSMS(index[n]);
    	} // if
     } while (index[n] != 0);
  } // deleteAllSMS

  /** get the signal quality from the ME
   *  @return  received signal strength indication (rssi = 0 ... 31 (best), 99 not known)
   *  @throws Exception
   */
  public int getSignalQuality() throws Exception {
    int n, p;
    String s = "";
    s = port.sendAT ("AT+CSQ");

    // destilate the signal quality from the answer string
    // example of answer: "+CSQ: 31,99"
    if (s.length() > 0) {
      p = s.indexOf(":");
      s = s.substring(p+1, s.length());
      p = s.indexOf(",");
      s = s.substring(0, p);
      n = Integer.parseInt(s.trim());
    } // if
    else n = 99;

    return n;
  } // getSignalQuality

  /** get the battery status from the ME
   *  @return  battery connection status (bcs), battery charge level (bcl)
   *           bcs: 0	     powered by the battery
   *                1	     battery connected, but not powered by it
   *                2	     does not have a battery connected
   *           bcl: 0	     battery is exhausted, or MT does not have a battery connected
   *                1...100  battery has 1 ... 100 percent of capacity remaining
   *  @throws Exception
   */
  public String getBatteryStatus() throws Exception {
    int p;
    String s = "";
    s = port.sendAT ("AT+CBC");

    // destilate the battery status from the answer string
    // example of answer: "+CBC: 0,90"
    if (s.length() > 0) {
      p = s.indexOf(":");
      s = s.substring(p+1, s.length());
      p = s.indexOf("\r");
      s = s.substring(0, p);
      s = s.trim();
    } // if
    else s = "";

    return s;
  } // getBatteryStatus

  /** test the connection between PC and ME
   * @return  answer string of the ME
   * @throws java.rmi.RemoteException
   */
  public boolean test() throws Exception {
    String s="";

    port.writeln("AT");   // test if there is a working communication
    s = port.read();      // get response from ME

    if (s.indexOf("OK") < 0) return false;  // no connection to the mobile phone
    else return true;                      // working connection to the mobile phone
  }  // test

} // Msg
