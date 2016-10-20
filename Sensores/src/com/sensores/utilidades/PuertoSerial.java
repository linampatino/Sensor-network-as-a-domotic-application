package com.sensores.utilidades;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.UnsupportedCommOperationException;
import javax.comm.SerialPortEventListener;

import com.sensores.modelo.Estado;
import com.sensores.modelo.Evento;
import com.sensores.modelo.Sensor;
import com.sensores.persistencia.Persistencia;

public class PuertoSerial implements Runnable, SerialPortEventListener {
	
	static CommPortIdentifier portId;
	InputStream inputStream;
	OutputStream outputStream;
	SerialPort serialPort;
	Thread readThread;
	String cadenaLeida="";

	public PuertoSerial() {
		
		try {
			portId = CommPortIdentifier.getPortIdentifier("COM4");
			serialPort = (SerialPort) portId.open("ComunicacionSerial", 2000);
		} catch (NoSuchPortException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (PortInUseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		serialPort.notifyOnDataAvailable(true);
		
		try {
			// Se especifica la configuracion del puerto serial
			serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} catch (UnsupportedCommOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serialPort.setDTR(false);
		serialPort.setRTS(false);
		
		readThread = new Thread(this);
		readThread.start();
	}

	public void run() {
		System.out.println("Run pto serial");
	}
	
	public void serialEvent(SerialPortEvent event) {
		switch(event.getEventType()) {
			case SerialPortEvent.BI:
				System.out.println("BI");
			case SerialPortEvent.OE:
				System.out.println("OE");
			case SerialPortEvent.FE:
				System.out.println("FE");
			case SerialPortEvent.PE:
				System.out.println("PE");
			case SerialPortEvent.CD:
				System.out.println("CD");
			case SerialPortEvent.CTS:
				System.out.println("CTS");
			case SerialPortEvent.DSR:
				System.out.println("DSR");
			case SerialPortEvent.RI:
				System.out.println("DSR");
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				System.out.println("OUTPUT_BUFFER_EMPTY");
			break;
			case SerialPortEvent.DATA_AVAILABLE:
				int c =leerCaracter();
				if (c != 13 && c != 10){
					cadenaLeida+=(char)c;
				}else{
					///System.out.println("Cadena leida::::: "+cadenaLeida);
					if (cadenaLeida.indexOf("&") != -1 ||(cadenaLeida.indexOf("Error") != -1 && cadenaLeida.indexOf("sending message") != -1)||(cadenaLeida.indexOf("No se encontro la mac")!=-1)) {
						ProcesarLectura procesa=new ProcesarLectura(new String(cadenaLeida));
					}
					cadenaLeida="";
				}
			break;
		}
	}

	public void escribirCaracter(int caracter) {

		try {
			outputStream = serialPort.getOutputStream();
			outputStream.write(caracter);
			outputStream.close();
		} catch (IOException e) {
			System.out.println(e.getMessage()+ "Excepción escribiendo caracter");
		}
	}

	public void escribirCadena(String cadena) {

		try {
			outputStream = serialPort.getOutputStream();
			outputStream.write(cadena.getBytes());
			outputStream.close();
			System.out.println("Escribiendo: " + cadena);
		} catch (IOException e) {
			System.out.println(e.getMessage()+ "Excepción escribiendo cadena");
		}
	}

	public void cerrarPuerto() {
		serialPort.close();
	}

	public int leerCaracter() {
		int c = -1;
		try {
			if (inputStream.available()>0){
				c = inputStream.read();
			}
			return c;
		} catch (Exception e) {
			System.out.println(e.getMessage() + "Excepción leyendo caracter ");
		}
		return c;
	}
	
	public String leerCadena(){
        int c = 0;
		StringBuffer readBuffer = new StringBuffer();
		try {
			while ((c = inputStream.read())!= 10) {
				if (c != 13)
					readBuffer.append((char) c);
			}
			String scannedInput = readBuffer.toString();
			System.out.println("Cadena leida::::: "+scannedInput);
			return scannedInput;
		} catch (IOException e) {
			System.out.println("Excepcion leyendo cadena " + e.getMessage());
		}
		return null;
	}
	
}
