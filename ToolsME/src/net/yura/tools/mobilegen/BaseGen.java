/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.tools.mobilegen;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
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
        StringBuffer tmp = new StringBuffer();
        tmp.append( sourceRoot );
        tmp.append( File.separator ); // TODO may not work on mac?????
        tmp.append( this.outputPackage.replace( "." , File.separator ) );
        tmp.append( File.separator );
        tmp.append( this.outputClass );
        tmp.append( ".java" );

        File output = new File( tmp.toString() );

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

    /**
     * Load classes from a white space delimited file
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static Collection<Class> loadClassesFromFile(String fileName, boolean sort) throws Exception {
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));
        ArrayList<Class> classes = new ArrayList<Class>();
        String line = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!"".equals(line)) {
                String className = line;
                classes.add( Class.forName(className) );
            }
        }

        if (sort) {
            Collections.sort(classes, new ClassComparator());
        }

        return classes;
    }
    public static boolean hasBeanProperty(Method[] mymethods, String name) {
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
