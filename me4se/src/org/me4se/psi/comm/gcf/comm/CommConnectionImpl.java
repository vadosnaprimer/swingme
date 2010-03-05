/*
 * $Id: CommConnectionImpl.java,v 1.1 2007/07/29 19:10:37 haustein Exp $
 */

package org.me4se.psi.comm.gcf.comm;

import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.midlet.ApplicationManager;

import javax.comm.*;

import org.me4se.impl.*;

/**
 * @author Michael Kroll, michael.kroll@trantor.de
 */

public class CommConnectionImpl extends ConnectionImpl implements CommConnection {

	Enumeration portList;
	CommPortIdentifier portId;
	SerialPort serialPort;

	/* *********************************************************************************
	 * Parameter     Default              Description                                  *  
	 ***********************************************************************************
	 * baudrate      platform dependent   The speed of the port.                       *  
	 * bitsperchar   8                    The number bits per                          *
	 *                                    character(7 or 8).                           *
	 * stopbits      1                    The number of stop bits                      *
	 *                                    per char(1 or 2)                             *
	 * parity        none                 The parity can be odd, even, or none.        *
	 * blocking      on                   If on, wait for a full buffer when reading.  *
	 * autocts       on                   If on, wait for the CTS line                 *
	 *                                    to be on before writing.                     *
	 * autorts       on                   If on, turn on the RTS line when             *
	 *                                    the input buffer is not full.                *
	 *                                    If off, the RTS line is always on.           *  
	 ***********************************************************************************/

	String comport = "COM1";
	int baudrate = 9600;
	int databits = SerialPort.DATABITS_8;
	int stopbits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	boolean autocts = true;
	boolean autorts = true;
	boolean blocking = true;

	/**
	 * 
	 */
	public void open(String url, int mode, boolean timeouts) throws IOException {

		String useCommPortsString = ApplicationManager.getInstance().getProperty("me4se.comm.enable");

		if (useCommPortsString != null) {
			Hashtable ht = new Hashtable();

			int portEnd = url.indexOf(";");

			if (portEnd == -1)
				portEnd = url.length();

			String protocol = url.substring(0, portEnd);

			comport = protocol.substring(5, protocol.length());

			int index = url.indexOf(";") + 1;
			int length = url.length();
			String params = url.substring(index, length);

			StringTokenizer st = new StringTokenizer(params, ";");
			while (st.hasMoreTokens()) {
				String param = st.nextToken();

				int idx = param.indexOf("=");
				if (idx > 0) {
					ht.put(param.substring(0, idx), param.substring(idx + 1, param.length()));
				}
			}

			// Check if the passed parameters are vaild ones

			Enumeration keys = ht.keys();

			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();

				if (!key.equals("baudrate")
					&& !key.equals("bitsperchar")
					&& !key.equals("stopbits")
					&& !key.equals("parity")
					&& !key.equals("blocking")
					&& !key.equals("autocts")
					&& !key.equals("autorts")) {

					throw new IllegalArgumentException("The parameter '" + key + "' is not valid for a CommConnection.");

				}
			}

			System.out.println(comport);

			if (ht.containsKey("baudrate")) {
				String baud = (String) ht.get("baudrate");

				if (baud.equals("57600")) {
					baudrate = 57600;
				}
				else if (baud.equals("38400")) {
					baudrate = 38400;
				}
				else if (baud.equals("19200")) {
					baudrate = 19200;
				}
				else if (baud.equals("9600")) {
					baudrate = 9600;
				}
				else if (baud.equals("4800")) {
					baudrate = 4800;
				}
				else if (baud.equals("2400")) {
					baudrate = 2400;
				}
				else if (baud.equals("1200")) {
					baudrate = 1200;
				}
				else if (baud.equals("300")) {
					baudrate = 300;
				}
				else
					throw new IOException("Specified baudrate not supported " + baud);
			}

			if (ht.containsKey("bitsperchar")) {
				String bits = (String) ht.get("bitsperchar");
				if (bits.equals("8")) {
					databits = SerialPort.DATABITS_8;
				}
				else if (bits.equals("7")) {
					databits = SerialPort.DATABITS_7;
				}
				else
					throw new IOException("Specified bitnumber not supported " + bits);
			}

			if (ht.containsKey("parity")) {
				String pari = (String) ht.get("parity");
				if (pari.equals("none")) {
					parity = SerialPort.PARITY_NONE;
				}
				else if (pari.equals("odd")) {
					parity = SerialPort.PARITY_ODD;
				}
				else if (pari.equals("even")) {
					parity = SerialPort.PARITY_EVEN;
				}
				else
					throw new IllegalArgumentException("Specified parity not supported " + pari);
			}

			if (ht.containsKey("stopbits")) {
				String stop = (String) ht.get("stopbits");
				if (stop.equals("1")) {
					stopbits = SerialPort.STOPBITS_1;
				}
				else if (stop.equals("2")) {
					stopbits = SerialPort.STOPBITS_2;
				}
				else
					throw new IllegalArgumentException("Specified amount of stopbits not supported '" + stop + "'");
			}

			if (ht.containsKey("blocking")) {
				String block = (String) ht.get("blocking");
				if (block.equals("on")) {
					blocking = true;
				}
				else if (block.equals("off")) {
					blocking = false;
				}
				else
					throw new IllegalArgumentException("Specified blocking mode not supported " + block);
			}

			if (ht.containsKey("autocts")) {
				String acts = (String) ht.get("autocts");
				if (acts.equals("on")) {
					autocts = true;
				}
				else if (acts.equals("off")) {
					autocts = false;
				}
				else
					throw new IllegalArgumentException("Specified mode for autocts not supported " + acts);
			}

			if (ht.containsKey("autorts")) {
				String arts = (String) ht.get("autorts");
				if (arts.equals("on")) {
					autorts = true;
				}
				else if (arts.equals("off")) {
					autorts = false;
				}
				else
					throw new IllegalArgumentException("Specified mode for autorts not supported " + arts);
			}

			portList = CommPortIdentifier.getPortIdentifiers();

			while (portList.hasMoreElements()) {
				portId = (CommPortIdentifier) portList.nextElement();
				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					if (portId.getName().equals(comport)) {

						try {
							serialPort = (SerialPort) portId.open("ME4SE", 2000);
						}
						catch (PortInUseException e) {
							System.out.println(e);
						}

						try {
							serialPort.setSerialPortParams(baudrate, databits, stopbits, parity);
						}
						catch (UnsupportedCommOperationException e) {
							System.out.println(e);
						}
					}
				}
			}
		}
		else {
			throw new IOException("CommConnection is not enabled using the 'comm.enable' property.");
		}
	}

	/**
	 * 
	 */
	public void close() throws IOException {
		if (serialPort != null) {
			serialPort.close();
		}
	}

	/**
	 * 
	 */
	public void initialise(Properties properties) {
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		StringBuffer sb = new StringBuffer();
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				sb.append(portId.getName());
				sb.append(",");
			}
		}

		String sysProp = sb.toString().substring(0, sb.length() - 1);
		Properties sysProps = System.getProperties();
		System.out.println("****************************");
		System.out.println("*    CommConnection impl   *");
		System.out.println("* of ME4SE by M.Kroll 2002 *");
		System.out.println("****************************");
		System.out.println("microedition.commports=" + sysProp);
		sysProps.put("microedition.commports", sysProp);
		System.setProperties(sysProps);
	}

	public int getBaudRate() {
		return serialPort.getBaudRate();
	}

	public int setBaudRate(int baudrate) {
		int bd = getBaudRate();
		try {
			String parityStr = "";
			if (parity == SerialPort.PARITY_EVEN) {
				parityStr = "even";
			}
			else if (parity == SerialPort.PARITY_ODD) {
				parityStr = "odd";
			}
			else if (parity == SerialPort.PARITY_NONE) {
				parityStr = "none";
			}
			
			System.out.println("Parameters for javax.comm.SerialPort: Baudrate=" + baudrate + 
				", Databits=" + databits + 
				", Stopbits=" + stopbits + 
				", Parity=" + parityStr);
			serialPort.setSerialPortParams(baudrate, databits, stopbits, parity);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return bd;
	}

	/**
	 * 
	 */
	public DataInputStream openDataInputStream() throws IOException {
		if (serialPort != null)
			return new DataInputStream(serialPort.getInputStream());
		throw new IOException("CommPort is not open!");
	}

	/**
	 * 
	 */
	public InputStream openInputStream() throws IOException {
		if (serialPort != null)
			return serialPort.getInputStream();
		throw new IOException("CommPort is not open!");
	}

	/**
	 * 
	 */
	public DataOutputStream openDataOutputStream() throws IOException {
		if (serialPort != null)
			return new DataOutputStream(serialPort.getOutputStream());
		throw new IOException("CommPort is not open!");
	}

	/**
	 * 
	 */
	public OutputStream openOutputStream() throws IOException {
		if (serialPort != null)
			return serialPort.getOutputStream();
		throw new IOException("CommPort is not open!");
	}
}

/*
 * $Log: CommConnectionImpl.java,v $
 * Revision 1.1  2007/07/29 19:10:37  haustein
 * Initial checkin of contents moved from the kobjects.org me4se module...
 *
 * Revision 1.2  2007/03/13 16:49:44  haustein
 * manager encapsulated
 *
 * Revision 1.1  2005/01/11 00:17:14  haustein
 * dynamic class loading refactoring, www/doc refactoring
 *
 */