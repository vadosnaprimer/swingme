package net.yura.mobile.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Vector;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.StringUtil;

/**
 * @author Yura Mamyrin
 */
public class CSVUtil {

    public interface CSV {
        public void newLine();
        public void newValue(int count,String value);
        public String getValueAt(Object obj, int c);
    }
	// i took something when i wrote this, it was shiny and a powder
	// makes for some crazy code, but at least it works
	public static void load(InputStream inputStream,CSV io) throws IOException {

                InputStreamReader inputStreamTxt = new InputStreamReader(inputStream);

		boolean inside=false;
		char how=0;
		int count=0;
		StringBuffer buf = new StringBuffer();
		io.newLine();

		try {
			int c;
			while ((c = inputStreamTxt.read()) != -1) {
                                if (c=='\r') { continue; }

				if (!inside) {

					if (c=='"') { //  || c=='\''

						inside = true;
						how = (char)c;
					}
					else if (c=='\n') {

						// ignore the \r char
						//if ( !"\r".equals(buf.toString()) ) { // buf.length()>0 &&
                                                io.newValue(count,buf.toString().trim());
                                                buf = new StringBuffer();
						//}

						count = 0;

						io.newLine();

					}
					else if (c==',') {

						//if (buf.length()>0) {
							io.newValue(count,buf.toString().trim());
							buf = new StringBuffer();
						//}

						count++;

					}
					else {

						// adding something that is not in quotes
						buf.append((char)c);
					}
				}
				else if (c==how) {
					inside = false;
                                        // not needed here, as will be added in 'outside' section anyway
					//saveDetail(product,count,buf.toString().trim());
					//buf = new StringBuffer();
				}
				else {
					// adding something that is in quotes
					buf.append((char)c);
				}


			}
		}
		//catch(Exception ex) {
			//Logger.warn(ex);
			//buf.append(ex.toString());
		//}
		finally {

			try { if (inputStreamTxt!=null) inputStreamTxt.close(); }
			catch(IOException ex) { Logger.info(null, ex); }

			try {if (inputStream!=null)  inputStream.close(); }
			catch(IOException ex) { Logger.info(null, ex); }
		}

	}


	public static void store(OutputStream out, CSV io,Vector objects,int count) throws IOException {

            Writer writer = null;

            try {

                writer = new OutputStreamWriter(out);

		for (int a=0;a<objects.size();a++) {
                        Object obj = objects.elementAt(a);

                        StringBuffer buffer = new StringBuffer();

                        for (int c=0;c<count;c++) {

                                String newString=io.getValueAt(obj,c);

                                if (newString!=null && newString.indexOf('"')>=0) {

                                        //throw new Exception("illegal char found: \"");
                                        // just strip out all the "
                                        newString = StringUtil.replaceAll(newString, "\"", "''");
                                }

                                if (newString!=null && (newString.indexOf(',')>=0 || newString.indexOf('\n')>=0)) {

                                        buffer.append("\"");

                                        buffer.append(newString);

                                        buffer.append("\"");

                                }
                                else if (newString!=null) {

                                        buffer.append(newString);

                                }

                                if (c<(count-1)) {
                                    buffer.append(",");
                                }
                        }

			writer.write( buffer.toString() );
			writer.write( "\n" );
                }
            }
            finally {

                try { if (writer!=null) writer.close(); }
                catch(IOException ex) { Logger.info(null, ex); }

                try { if (out!=null) out.close(); }
                catch(IOException ex) { Logger.info(null, ex); }
	    }
	}


}
