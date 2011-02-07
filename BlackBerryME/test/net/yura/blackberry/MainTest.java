package net.yura.blackberry;

import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.ProgressBar;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.BoxLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.test.MainPane;
import net.yura.mobile.test.MainPane.Section;

public class MainTest extends Section {

    public MainTest(MainPane mainPane) {
        super(mainPane);
    }

    //Override
    public void createTests() {
        add(new Label("Android Tests"));

        addTest("Show Native Popup", "nativePopup");
        addTest("Show Native Screen", "nativeScreen");
        addTest("Show Native Text", "nativeText");

        
        Label preferenceSeparator = new Label("here are some android only components");
        preferenceSeparator.setName("PreferenceSeparator");
        add(preferenceSeparator);

        Vector v = new Vector();
        v.addElement("HELLO");
        v.addElement("GOODBYE");
        ComboBox comboBox2 = new ComboBox(v);
        comboBox2.setName("ComboBox2");
        add(comboBox2);

        Button redButton = new Button("RED BUTTON");
        redButton.setName("RedButton");
        add(redButton);

        ProgressBar bar = new ProgressBar();
        bar.setName("IndeterminateSpinner");
        //bar.setIndeterminate(true);
        add(bar);

    }

    //Override
    public void openTest(String actionCommand) {
        if ("mainmenu".equals(actionCommand)) {
            Button exit = makeButton("Exit", "exit");
            exit.setMnemonic(KeyEvent.KEY_END);
            addToScrollPane(this, null, exit);
        }
        else if ("exit".equals(actionCommand)) {
            Midlet.exit();
        }
        else if ("nativeScreen".equals(actionCommand)) {
        	
        	UiApplication.getUiApplication().invokeLater (new Runnable() {
        	    public void run() {
        	    	HelloWorldScreen helloWorldScreen = new HelloWorldScreen();
        	    	
        	    	int size=200;
        	    	
        	    	Image buffer1 = Image.createImage(size, size);
        	    	Graphics g1 = buffer1.getGraphics();
        	    	//DesktopPane.getDesktopPane().paint(g1);
        	    	int[] data = new int[size*size];
        	    	buffer1.getRGB(data, 0, size, 0, 0, size, size);
        	    	Bitmap bitmap = new Bitmap(size, size);
        	    	bitmap.setARGB(data, 0, size, 0, 0, size, size);
        	    	
        	    	BitmapField label = new BitmapField(bitmap);
        	    	helloWorldScreen.add(label);
        	    	
        	    	UiApplication.getUiApplication().pushScreen(helloWorldScreen); 
        	    }
        	});
        }
        else if ("nativeText".equals(actionCommand)) {
        
        	UiApplication.getUiApplication().invokeLater (new Runnable() {
        	    public void run() {
        	    	Screen screen = UiApplication.getUiApplication().getActiveScreen();
        	    	
        	    	//TextField text = new TextField();
        	    	//TestBorder.man.add(text);
        	    	//TestBorder.man.setBounds(text, 5, 5, 50, 50);
        	    	//TestBorder.man.delete(text);
        	    	//screen.add(text);
/*
        	    	Dialog.alert("100");
        	    	
        	    	InputHelper helper = new InputHelper();
        	    	
        	    	helper.setTextInputConnector(new ITextInputConnector() {
						public void getTextLocation(TextHitInfo arg0, XYRect arg1) {
							
						}
						public TextHitInfo getLocationOffset(int x, int y) {
							return new TextHitInfo(0, true);
						}
						public void textChanged(int arg0, int arg1) {

						}
						public void caretChanged(int arg0) {

						}
					});
        	    	
        	    	//helper.
        	    	
        	    	screen.setInputHelper(helper);

        	    	screen.getVirtualKeyboard().setVisibility(VirtualKeyboard.SHOW);
*/
        	    }
        	});
        }
        else if ("nativePopup".equals(actionCommand)) {
        	
        	// this seems to open the Dialog, but freezes right after
        	//synchronized(Application.getEventLock()) {
        	//	Dialog.alert("bob");
            //}
        	
        	UiApplication.getUiApplication().invokeLater (new Runnable() {
        	    public void run() {
        	    	Dialog.alert("bob");
        	    	
        	    	//final net.rim.device.api.ui.picker.DateTimePicker datePicker = net.rim.device.api.ui.picker.DateTimePicker.createInstance();
        	        //datePicker.doModal();
        	        //Midlet.getMidlet().onResult(1, datePicker.getDateTime());

        	    }
        	});

        	/*
            String url = "native://net.yura.android.TestTimePickerActivity";
            try {
                Midlet.getMidlet().platformRequest(url);
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */
        }
    }

    public void actionPerformed(String actionCommand) {
        if ("sendSms".equals(actionCommand)) {
            //todo
        }
        else {
            super.actionPerformed(actionCommand);
        }
    }
}
