package net.yura.tools.translation;

import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Vector;
import qdxml.DocHandler;
import qdxml.QDParser;

public class XULScanner implements DocHandler {

    String string;
    
    File currentFile;
    Vector result;

    public Vector scan(File baseDir,String string) {

        result = new Vector();

        this.string = string;
        scan(baseDir);

        return result;
    }

    public void scan(File f) {
        File[] files = f.listFiles();
        for (File file: files) {
            if (file.isDirectory()) {
                scan(file);
            }
            else if (file.getName().endsWith(".xml")) {
                try {
                    currentFile = file;
                    QDParser.parse(this, new FileReader(file));
                }
                catch(FoundException ex) {
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class FoundException extends Exception {
    }

    public void startElement(String tag, Hashtable h) throws Exception {
        String i18n = (String)h.get("i18n");

        if ("true".equals(i18n)) {
            String text = (String)h.get("text");
            if (string.equals(text)) {
                result.add(currentFile);
                throw new FoundException();
            }
        }
    }

    public void endElement(String tag) throws Exception {
        // nothing
    }

    public void startDocument() throws Exception {
        // nothing
    }

    public void endDocument() throws Exception {
        // nothing
    }

    public void text(String str) throws Exception {
        // NOTHING!!!!
    }

}
