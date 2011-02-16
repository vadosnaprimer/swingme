package net.yura.blackberry;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.UiApplication;

public class HelloWorld extends UiApplication {
/*
	    public static void main(String[] args) 
	    { 
	            HelloWorld theApp = new HelloWorld(); 
	            theApp.enterEventDispatcher(); 
	  }
*/ 
	    public HelloWorld() 
	    { 
	    	
	    	ApplicationDescriptor ad = ApplicationDescriptor.currentApplicationDescriptor();
	    	

	    	
	            //display a new screen 
	        pushScreen(new HelloWorldScreen()); 
	} 

}

