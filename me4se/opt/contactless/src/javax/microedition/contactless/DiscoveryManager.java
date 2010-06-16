package javax.microedition.contactless;

import java.util.Vector;

import javax.microedition.contactless.ndef.NDEFRecordListener;
import javax.microedition.contactless.ndef.NDEFRecordType;

import org.me4se.psi.java1.nfc.NDEFRecordListenerContainer;
import org.me4se.psi.java1.nfc.TargetListenerContainer;

public class DiscoveryManager {

  public static Vector ndefRecordListeners = null;
  public static Vector targetListeners = null;
  public static Vector transactionListeners = null;
  
  private static DiscoveryManager instance = null;
  
  private DiscoveryManager() {
    ndefRecordListeners = new Vector();
    targetListeners = new Vector();
    transactionListeners = new Vector();
  }
  
	public void addNDEFRecordListener(NDEFRecordListener listener, NDEFRecordType recordType) throws ContactlessException {
		System.out.println("ME4SE: DiscoveryManager.addNDEFRecordListener(NDEFRecordListener listener='"+listener+"', NDEFRecordType recordType='"+recordType+"') called.");
		NDEFRecordListenerContainer cont = new NDEFRecordListenerContainer(listener, recordType);
		ndefRecordListeners.addElement(cont);
	}

	public void addTargetListener(TargetListener listener, TargetType targetType) throws ContactlessException, IllegalStateException {
	  System.out.println("ME4SE: DiscoveryManager.addTargetListener(TargetListener listener='" + listener + "', TargetType targetType='" + targetType + "') called.");
	  TargetListenerContainer cont = new TargetListenerContainer(listener, targetType);
	  targetListeners.addElement(cont);
	}
	
	public void addTransactionListener(TransactionListener listener) throws ContactlessException {
		System.out.println("ME4SE: DiscoveryManager.addTransactionListener(TransactionListener listener='"+listener+"') called.");
		transactionListeners.addElement(listener);
	}
	
	public static DiscoveryManager getInstance() {
		if (instance == null)
		  instance = new DiscoveryManager(); 
		return instance;
	}
	
	public String getProperty(String name) {
		System.out.println("ME4SE: DiscoveryManager.getProperty() called without effect. NYI!");
		return "ME4SE NYI!";
	}

	public static TargetType[] getSupportedTargetTypes() {
		System.out.println("ME4SE: DiscoveryManager.getSupportedTargetTypes() called without effect. NYI!");
		return null;
	}
	
	public void removeNDEFRecordListener(NDEFRecordListener listener, NDEFRecordType recordType) {
    System.out.println("ME4SE: DiscoveryManager.removeNDEFRecordListener(NDEFRecordListener listener='"+listener+"', NDEFRecordType recordType='"+recordType+"') called.");
	  for (int i = 0; i < ndefRecordListeners.size(); i++) {
		  NDEFRecordListenerContainer cont = (NDEFRecordListenerContainer)ndefRecordListeners.elementAt(i);
		  if (cont.getListener() == listener && cont.getRecordType() == recordType) {
		    System.out.println("\tRemoving listener: " + listener);
		    ndefRecordListeners.removeElementAt(i);
		    break;
		  }
		}
	}
	
	public void removeTargetListener(TargetListener listener, TargetType targetType) {
	  System.out.println("ME4SE: DiscoveryManager.removeTargetListener(TargetListener listener='" + listener + "', TargetType targetType='" + targetType + "') called.");
    for (int i = 0; i < targetListeners.size(); i++) {
      TargetListenerContainer cont = (TargetListenerContainer)targetListeners.elementAt(i);
      if (cont.getListener() == listener && cont.getTargetType() == targetType) {
        System.out.println("\tRemoving listener: " + listener);
        targetListeners.removeElementAt(i);
        break;
      }
    }
	}
	
	public void removeTransactionListener(TransactionListener listener) {
		System.out.println("ME4SE: DiscoveryManager.removeTransactionListener() called without effect. NYI!");
		targetListeners.removeElement(listener);
	}
	
	public void setProperty(String name, String value) {
		System.out.println("ME4SE: DiscoveryManager.setProperty() called without effect. NYI!");
	}	
}