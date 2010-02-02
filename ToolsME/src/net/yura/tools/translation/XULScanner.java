package net.yura.tools.translation;

import java.io.File;
import java.util.Hashtable;
import qdxml.DocHandler;

public class XULScanner implements DocHandler {

    File baseDir;

    public void scan() {

        File[] files = baseDir.listFiles();

    }



    public void startElement(String tag, Hashtable h) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void endElement(String tag) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
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
