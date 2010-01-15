/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badoo.persistance;

import com.badoo.mobile.persistance.PimListener;
import com.badoo.mobile.model.PhonebookContact;
import com.badoo.mobile.persistance.PIMHandler;
import com.badoo.mobile.util.Logger;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.midlet.*;

/**
 * @author Administrator
 */
public class PIMTestMidlet extends MIDlet implements PimListener {

    public void startApp() {
        //#debug
        Logger.init();
        PIMHandler handler = new PIMHandler(this);
        handler.loadContactList();
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void onListUpdate(PhonebookContact contact) {
        //#debug
        Logger.debug(contact.getName() + " has been updated");
    }

    public void onListUpdateForFirstLaunch(PhonebookContact contact) {
        //#debug
        Logger.debug(contact.getName() + " has been updated on first launch");
    }

    public void contactItemsReadFromPIMDatabase(int count) {
        //#debug
        Logger.debug(count + " contacts read from pim");
    }

    public void contactItemsSorted(int count) {
        //#debug
        Logger.debug(count + " contacts have been sorted");
    }

    public void contactItemsSavedToRMS(int count) {
        //#debug
        Logger.debug(count + " contacts has been saved to rms");
    }

    public void contactReadingFailed(int contactsRead) {
        //#debug
        Logger.debug("Contacts reading failed after reading " + contactsRead + " contacts");
    }
    
    public void allContactsInvited() {
        //#debug
        Logger.debug( "allContactsInvited()" );
    }
  
    public void sendContacts(Vector contacts) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void pimException(Exception e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
