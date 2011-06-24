/*
 * $Id: FileSystemRegistry.java,v 1.1 2007/07/29 19:13:02 haustein Exp $
 */

/**
 * @author Michael Kroll, michael.kroll@trantor.de
 */

package javax.microedition.io.file;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 * @API PDAP-1.0
 */

public class FileSystemRegistry {
	/**
	 * @API PDAP-1.0
	 */

	static class FileRootEnumeration implements Enumeration {
		
		String[] list = null;
		int idx = 0;
	
		public FileRootEnumeration(String[] list) {
			this.list = list;
		}
	
		public int size() {
			if (list != null) {
				return list.length;
			}
			return 0;
		}
	
		public boolean hasMoreElements() {
			if (list == null) {
				return false;
			}
			else if (idx < list.length){
				return true;
			}
			else {
				return false;
			}
		}
	
		public Object nextElement() { 
			return list[idx++];
		}
		
	}

	static String[] listenRoots = null;

	static FileSystemListener fslistener;

	static class ListenThread extends Thread {

		Hashtable roots = new Hashtable();

		public void run() {

			FileRootEnumeration list = (FileRootEnumeration)FileSystemRegistry.listRoots();

			while(list.hasMoreElements()){
				String element = (String)list.nextElement();
				roots.put(element, element);
			}
			
			while (FileSystemRegistry.fslistener != null) {
				list = (FileRootEnumeration)FileSystemRegistry.listRoots();

				// new root has beed added
				if (list.size() > roots.size()) {
					while (list.hasMoreElements()) {
						String element = (String)list.nextElement();
						if (roots.get(element) == null) {
							roots.put(element, element);

							fslistener.rootChanged(FileSystemListener.ROOT_ADDED, element);
							break;
						}
					}

				}
				// root has been removed
				else if (list.size() < roots.size()) {
					while (list.hasMoreElements()) {
						roots.remove(list.nextElement());
					}
					String removedRoot = (String) roots.elements().nextElement();
					fslistener.rootChanged(FileSystemListener.ROOT_REMOVED, removedRoot);
					
					roots = new Hashtable();
					
					list = (FileRootEnumeration)FileSystemRegistry.listRoots();					
					
					while(list.hasMoreElements()) {
						String element = (String)list.nextElement();
						roots.put(element, element);
					}
				}
				
				try {
					sleep(2500);
				}
				catch (InterruptedException e) {
				}
			}
		}
	}

	private FileSystemRegistry() {
	}

        /**
         * map fake URL -> Real File Path
         * e.g. file:///sdcard/ -> c:/temp/sdcard/
         */
        public static final Hashtable remap = new Hashtable();
        
	private static void initRoots() {
		String roots = System.getProperty("fconn.listenroots");

//                if(roots == null){
//			System.err.println("Please specify the file system roots in VM variable fconn.listenroots. Example -Dfconn.listenroots=C:\\;D:\\");
//			roots= File.separatorChar == '\\' ? "c:" : "/";
//		}

                if (roots!=null) {

                    StringTokenizer tokens = new StringTokenizer(roots, ";");
                    listenRoots = new String[tokens.countTokens()];
                    int i = 0;
                    while (tokens.hasMoreTokens()) {
                            listenRoots[i++] = tokens.nextToken();
                    }

                }
                else {
                    File[] fileroots = File.listRoots();
                    listenRoots = new String[fileroots.length];
                    for (int c=0;c<fileroots.length;c++) {
                        listenRoots[c] = fileroots[c].getAbsolutePath();
                    }


                    File filesystem = new File( System.getProperty("user.home") , "j2mewtk/2.5.2/appdb/DefaultColorPhone/filesystem");
                    
                    if (filesystem.exists() && filesystem.isDirectory()) {
                        
                        File[] files = filesystem.listFiles();
                        Vector dirs = new Vector();
                        for (int c=0;c<files.length;c++) {
                            if (files[c].isDirectory()) {
                                String name = files[c].getName()+"/";
                                remap.put("file:///"+name, files[c].getAbsolutePath()+"/");
                                dirs.add( name );
                            }
                        }

                        String[] old = listenRoots;
                        listenRoots = new String[old.length + dirs.size()];
                        System.arraycopy(old, 0, listenRoots, 0, old.length);

                        for (int c=old.length;c<listenRoots.length;c++) {
                            listenRoots[c] = (String)dirs.elementAt(c-old.length);
                        }

                    }

                    // convert all \ to /
                    for (int c=0;c<listenRoots.length;c++) {
                        listenRoots[c] = listenRoots[c].replaceAll("\\\\", "/");
                    }

                }
                
                //System.out.println("listenRoots="+Arrays.asList( listenRoots ));
                
	}

	/**
	 * @API PDAP-1.0
	 */
	public static boolean addFileSystemListener(FileSystemListener listener) {
		if (fslistener != null)
			return false;

		if (listenRoots == null)
			initRoots();

		fslistener = listener;
		new ListenThread().start();

		return true;
	}
	
	/**
	 * @API PDAP-1.0
	 */
	public static Enumeration listRoots() {

		if (listenRoots == null)
			initRoots();

		if (listenRoots != null) {

			Vector v = new Vector();

			for (int i = 0; i < listenRoots.length; i++) {
                            
				//File rootDir = new File(listenRoots[i]);
				//if ((rootDir != null) && (rootDir.exists())) {
				//	v.addElement(listenRoots[i]);
				//}
                            
                                // instead of using normal java File IO, we use FileConnection in case it does any remapping
                                try {
                                    FileConnection fc = (FileConnection)Connector.open( "file:///"+listenRoots[i] , Connector.READ);
                                    if (fc.exists()) {
                                        v.addElement(listenRoots[i]);
                                    }
                                }
                                catch (Exception ex) { }
			}

			String[] roots = new String[v.size()];

			for (int i = 0; i < roots.length; i++) {
				String root = (String) v.elementAt(i);
				root = root.substring(0, root.length());
				roots[i] = root;
			}
			
			return new FileRootEnumeration(roots);
		}

		return new FileRootEnumeration(null);
	}

	/**
	 * @API PDAP-1.0
	 */
	public static boolean removeFileSystemListener(FileSystemListener listener) {
		listener = null;
		return true;
	}
}

