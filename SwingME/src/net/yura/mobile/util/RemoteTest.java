package net.yura.mobile.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.MenuBar;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.io.UTF8InputStreamReader;

/**
 * @author Yura Mamyrin
 */
public class RemoteTest extends Thread {

    public static void open() {
        try {
            if (Midlet.getPlatform() == Midlet.PLATFORM_ANDROID) {
                RemoteTest remoteTest = (RemoteTest)Class.forName("net.yura.android.AndroidRemoteTest").newInstance();
                remoteTest.start();
            }
            else {
           new RemoteTest().start();
        }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        // TODO: Need a quit flag
        while (true) {
            ServerSocketConnection ssc = null;
            try {
                 Thread.sleep(5000);
                 System.out.println("Remote Test starting...");

                ssc = (ServerSocketConnection)Connector.open("socket://:1234");

                System.out.println("Remote Test " +ssc.getLocalAddress() +" " +ssc.getLocalPort() );

                StreamConnection sc = ssc.acceptAndOpen();
                InputStream is = sc.openInputStream();
                OutputStream out = sc.openOutputStream();

                UTF8InputStreamReader reader = new UTF8InputStreamReader(is);
                OutputStreamWriter writer = new OutputStreamWriter(out);

                while (true) {
                    String command = readLine(reader);

    System.out.println("COMMAND: "+command);
                    process(command,writer);
                }
            }
            catch (Throwable ex) {
                //#debug info
                ex.printStackTrace();
            }

            try {
                ssc.close();
            } catch (Throwable e) {}
        }
    }

    void process(String command,Writer writer) throws IOException {

        if (command.startsWith("click ")) {
            String text = command.substring("click ".length());
            text = replaceEscapeSequences(text);

            boolean result = click(text);

            System.out.println("result: "+result);
            writer.write(result ? "OK\n" : "FAIL\n");
        }
        else {
            System.out.println("Unknown command: "+command);
            writer.write("UNKNOWN\n");
        }

        writer.flush();
    }

    protected boolean click(String text) {

            DesktopPane dp = DesktopPane.getDesktopPane();
            Window window = dp.getSelectedFrame();

            System.out.println("CLICK on >"+text+"<");

            boolean result = click(text,window.getCommands()); // check soft-keys first
            if (!result) {
                result = click(text,window.getComponents()); // now check all other components
            }
            return result;

    }

    static boolean click(String text,Vector components) {

        for (int c=0;c<components.size();c++) {
            Component comp = (Component)components.elementAt(c);

            if (comp instanceof Button && ((Button)comp).getText().equalsIgnoreCase(text)) {
                ((Button)comp).fireActionPerformed();
                return true;
            }
            if (comp instanceof Panel) {
                if (click (text,((Panel)comp).getComponents())) {
                    return true;
                }
            }
            if (comp instanceof MenuBar) {
                if (click (text,((MenuBar)comp).getItems())) {
                    return true;
                }
            }
        }

        return false;
    }

    static String readLine(Reader in) throws IOException {
        int ch = 0;
        StringBuffer sb = new StringBuffer();

        while ((ch = in.read()) != -1){
            if (ch == '\n' && sb.length() > 0) {
               //Carriage return was received or ENTER was pressed
               break; //Exit loop and print input
            }

            if (ch != '\r') {
                sb.append((char)ch);
            }
        }

        if (ch == -1 && sb.length() == 0) {
            // Notify that the connection is now closed.
            throw new IOException();
        }

        return sb.toString();
    }

    private String replaceEscapeSequences(String txt) {
        // TODO: for now we only replace "\n"
        txt = StringUtil.replaceAll(txt, "\\n", "\n");

        return txt;
    }
}
