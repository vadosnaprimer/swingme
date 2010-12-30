package net.yura.blackberry;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.layout.XULLoader;
import net.yura.mobile.gui.plaf.MetalLookAndFeel;

public class HelloWorldScreen extends MainScreen{
	public HelloWorldScreen() 
    { 
            super(); 
            
            XULLoader loader = new XULLoader();
            
            LabelField title = new LabelField( loader.toString() , 
                 LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH); 
            setTitle(title); 
            add(new RichTextField("Hello World!")); 
            
            // the ButtonField.CONSUME_CLICK is needed to stop the application mneu poping up
            ButtonField button = new ButtonField("test", ButtonField.CONSUME_CLICK);
            
            button.setChangeListener(new FieldChangeListener() {
                public void fieldChanged(Field field,int context) {
                	test();
                 }
            });

            add(button);
            
            add(new TextField(TextField.EDITABLE));
    }
	
	void test() {
		//Dialog.alert("test 1111!");
		
		
    	UiApplication.getUiApplication().invokeLater (new Runnable() {
    	    public void run() {
    	    	Dialog.alert("test 2222");
    	    }
    	});
		
		
/*
 * this throws a security exception
		Midlet midlet = new Midlet() {
			protected void initialize(DesktopPane rootpane) {
				rootpane.setLookAndFeel( new MetalLookAndFeel() );
				
                Frame mainWindow = new Frame();

                mainWindow.add(new Label("test 2"));

                mainWindow.setMaximum(true);
                mainWindow.setVisible(true);
			}
		};
*/

		
		//Dialog.alert("test 2222!");
	}
	
	public boolean onClose() 
    { 
       Dialog.alert("Goodbye!");
       
       if (UiApplication.getUiApplication() instanceof HelloWorld) {
    	   System.exit(0);
       }
       else {
    	   //UiApplication.getUiApplication().popScreen();
    	   UiApplication.getUiApplication().popScreen(this);
       }
        return true; 
  }
	
}
