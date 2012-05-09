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
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.MenuBar;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.Table;
import net.yura.mobile.gui.components.TextPane;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.io.UTF8InputStreamReader;

/**
 * @author Yura Mamyrin
 * @see java.awt.Robot
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

    public RemoteTest() {
        super("RemoteTest");
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
//                //#debug info
                System.out.println("failed to start RemoteTest "+ex);
//                ex.printStackTrace();
            }

            try {
                ssc.close();
            } catch (Throwable e) {}
        }
    }

    protected void process(String command,Writer writer) throws IOException {
        try {
            if (command.startsWith("click ")) {
                String text = command.substring("click ".length());
                text = replaceEscapeSequences(text);

                boolean result = onClickText(text);

                System.out.println("result: "+result);
                writer.write(result ? "OK\n" : "FAIL\n");
            }
            else if (command.startsWith("clickFocusable ")) {
                String text = command.substring("clickFocusable ".length());
                String[] idxs = StringUtil.split(text, ' ');
                int n1 = Integer.parseInt(idxs[0]);
                int n2 = (idxs.length <= 1) ? 0 : Integer.parseInt(idxs[1]);

                // Indexes start in one instead of zero...
                boolean result = onClickFocusable(n1 - 1, n2 - 1);

                writer.write(result ? "OK\n" : "FAIL\n");
            }
            else if (command.equalsIgnoreCase("setCursorInvisible")) {
                onSetCursorInvisible();
                writer.write("OK\n");
            }
            else if (command.startsWith("command ")) {
                String cmd = command.substring("command ".length());
                boolean result = onCommand(cmd);
                writer.write(result ? "OK\n" : "FAIL\n");
            }
            else {
                System.out.println("Unknown command: "+command);
                writer.write("UNKNOWN\n");
            }
        }
        catch (Throwable e) {
            //#debug debug
            e.printStackTrace();
            writer.write("EXCEPTION: " + e + "\n");
        }

        writer.flush();
    }

    protected boolean onClickText(String text) {
        System.out.println("CLICK on >"+text+"<");

        Window window = DesktopPane.getDesktopPane().getSelectedFrame();

        boolean result = clickText(text,window.getCommands()); // check soft-keys first
        if (!result) {
            result = clickText(text,window.getComponents()); // now check all other components
        }
        return result;
    }

    protected boolean onClickFocusable(int n1, int n2) {
        System.out.println("CLICK Focusable " + n1 + "@" + n2);

        Window window = DesktopPane.getDesktopPane().getSelectedFrame();
        Vector focusComps = new Vector();

        getFocusableItems(window, focusComps);

        boolean isValidComponent = (n1 >= 0 && n1 < focusComps.size());
        if (isValidComponent) {
            Component comp = (Component) focusComps.elementAt(n1);
            comp.makeVisible();
            comp.requestFocusInWindow();
            if (comp instanceof Button) {
                ((Button)comp).fireActionPerformed();
            }
            else if (n2 >= 0) { // Handle lists
                if (comp instanceof List) {
                    List list = (List) comp;
                    list.setSelectedIndex(n2);
                    list.fireActionPerformed();
                }
                else if (comp instanceof Table) {
                    Table table = (Table) comp;
                    int numCols = table.getColumnCount();
                    int row = n2 / numCols;
                    int col = n2 % numCols;

                    if (row >= 0 && col >= 0) {
                        table.editCellAt(row, col);

                        // Table is a panel, and its first component is the editor
                        Component editorComp = (Component) table.getComponents().elementAt(0);
                        editorComp.makeVisible();
                        if (editorComp instanceof Button) {
                            ((Button) editorComp).fireActionPerformed();
                        }
                    }
                }
            }
        }

        return isValidComponent;
    }

    protected boolean onCommand(String cmd) {
        // To be overload by sub classes
        return false;
    }

    private void getFocusableItems(Component comp, Vector focusList) {

        if (comp.isVisible()) {
            if (comp.isFocusable()) {
                focusList.addElement(comp);
            }
            else if (comp instanceof Panel) {
                Vector comps = ((Panel)comp).getComponents();
                for (int i = 0; i < comps.size(); i++) {
                    // recursive call
                    getFocusableItems((Component) comps.elementAt(i), focusList);
                }
            }
        }
    }

    protected void onSetCursorInvisible() {

    }

    static boolean clickText(String text,Vector components) {

        for (int c=0;c<components.size();c++) {
            Component comp = (Component)components.elementAt(c);

            if (comp instanceof Button && ((Button)comp).getText().equalsIgnoreCase(text)) {
                comp.requestFocusInWindow();
                ((Button)comp).fireActionPerformed();
                return true;
            }
            else if (comp instanceof Panel) {
                if (clickText (text,((Panel)comp).getComponents())) {
                    return true;
                }
            }
            else if (comp instanceof MenuBar) {
                if (clickText (text,((MenuBar)comp).getItems())) {
                    return true;
                }
            }
            else if (comp instanceof List) {
                List list = (List) comp;
                int size = list.getSize();
                for (int i = 0; i < size; i++) {
                    Object o = list.getElementAt(i);
                    if (text.equalsIgnoreCase(String.valueOf(o))) {
                        list.setSelectedIndex(i);
                        list.fireActionPerformed();
                        return true;
                    }
                }
            }
            else if (comp instanceof TextPane) {
                if (((TextPane) comp).pressLink(text)) {
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
