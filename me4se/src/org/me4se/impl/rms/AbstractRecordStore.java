// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors: Michael Kroll
//
// STATUS: 
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version. This program is
// distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details. You should have received a copy of the
// GNU General Public License along with this program; if not, write
// to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

package org.me4se.impl.rms;

import javax.microedition.rms.*;

import java.util.*;


public abstract class AbstractRecordStore extends RecordStore {

	
	/** 
	 * Working set of record stores, should be filled by the platform
	 * specific RecordStoreInitializer 
	 * */
    public static Hashtable recordStores = new Hashtable ();
//    public static RecordStoreImpl metaStore = newInstance ();
    
   
    protected Vector listeners;
    
    protected String recordStoreName;
    protected int refCount;


    public void addRecordListener (RecordListener listener) {
	if (listeners == null)
	    listeners = new Vector ();
	
	listeners.insertElementAt(listener , listeners.size());
    }

    
    public void removeRecordListener (RecordListener listener) {
	if (listeners != null)
	    listeners.removeElement (listener);
    }
    
       
    public abstract void deleteRecordStoreImpl () throws RecordStoreException ;


    protected void checkOpen () throws RecordStoreNotOpenException {
	if (refCount == 0) 
	    throw new RecordStoreNotOpenException 
		("RecordStore not open: "+recordStoreName);
    } 


    protected void checkId (int index) throws RecordStoreException {
	checkOpen ();
	if (index < 1 || index >= getNextRecordID ()) 
	    throw new InvalidRecordIDException 
		("Id "+index+" not valid in recordstore "+recordStoreName);
    }


    public static AbstractRecordStore newInstance () {
           return new org.me4se.psi.j2se.rms.RecordStoreImpl ();
    }

    
    public static String [] listRecordStoresImpl (){
    	String[] stores = new String[recordStores.size()];
    	
    	int i = 0;
    	for(Enumeration e = recordStores.keys(); e.hasMoreElements(); ){
    		stores[i++] = (String) e.nextElement();
    	}
    	
    	return stores;
    }

    public abstract void open (String recordStoreName, 
			       boolean create) throws RecordStoreNotFoundException, RecordStoreException;
    
    
    
    
    
}
