/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.tools.mobilegen;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.Task;

/**
 *
 * @author BMA
 */
public abstract class BaseGen extends Task {

    private String classNamesFile;
    String outputClass;
    String sourceRoot;
    String outputPackage;

    public String getOutputClass() {
        return outputClass;
    }

    public String getOutputPackage() {
        return outputPackage;
    }

    public String getClassNamesFile() {
        return classNamesFile;
    }

    public void setClassNamesFile(String classNamesFile) {
        this.classNamesFile = classNamesFile;
    }

    public File getGeneratedFile() {
        File output = new File( sourceRoot+File.separator+outputPackage.replace( "." , File.separator ), outputClass + ".java" );
        if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
        }
        return output;
    }

    public void setOutputPackage( String argument ) {
	    this.outputPackage = argument;
    }
    public void setOutputClass( String argument ) {
	    this.outputClass   = argument;
    }
    public void setSourceRoot( String argument ) {
	    this.sourceRoot    = argument;
    }

    @Override
    public void execute() {
        try {
            doGen();
        } catch (Exception ex) {
            System.out.println("ERROR in doGen: "+ex);
            ex.printStackTrace();
            Logger.getLogger(BaseGen.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    abstract  public void doGen() throws Exception;

    public static class MyClass {
    
        public Class theClass;
        public int id;
    }
    
    public static Collection<Class> sort(Collection<MyClass> list) {
        ArrayList<Class> list2 = new ArrayList<Class>();
        for (MyClass c:list) {
            list2.add(c.theClass);
        }
        Collections.sort(list2, new ClassComparator());
        return list2;
    }
    
    /**
     * Load classes from a white space delimited file
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static Collection<MyClass> loadClassesFromFileRaw(String fileName) throws Exception {
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));
        ArrayList<MyClass> classes = new ArrayList<MyClass>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!"".equals(line)) {
                MyClass className = new MyClass();
                int equals = line.indexOf('=');
                if (equals > 0) {
                    String number = line.substring(equals+1).trim();
                    className.id = Integer.parseInt(number);
                    line = line.substring(0, equals).trim();
                }
                className.theClass = Class.forName(line);
                classes.add( className );
            }
        }
        return classes;
    }
    public static boolean hasBeanProperty(Class theclass,Method[] mymethods, String name) {

        String fieldName = Character.toLowerCase(name.charAt(0))+name.substring(1);

        try {
            Field field = theclass.getDeclaredField(fieldName);
            if ( Modifier.isTransient(field.getModifiers()) ) {
                return false;
            }
        }
        catch(Exception ex) {
            // field not found
        }

        boolean hasset = false;
        boolean hasget = false;
        for (Method method:mymethods) {
            if (method.getName().equals("get"+name) && method.getReturnType()!=void.class && method.getParameterTypes().length == 0 && Modifier.isPublic( method.getModifiers() ) ) {
                hasget = true;
            }
            else if (method.getName().equals("set"+name) && method.getReturnType()==void.class && method.getParameterTypes().length == 1 && Modifier.isPublic( method.getModifiers() )) {
                hasset = true;
            }


        }
        return hasget && hasset;
    }
}
