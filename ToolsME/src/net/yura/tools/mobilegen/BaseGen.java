/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.tools.mobilegen;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.Task;

/**
 *
 * @author BMA
 */
public abstract class BaseGen extends Task {

    private String generatedFile;
    private String classNamesFile;

    public String getClassNamesFile() {
        return classNamesFile;
    }

    public void setClassNamesFile(String classNamesFile) {
        this.classNamesFile = classNamesFile;
    }

    public String getGeneratedFile() {
        return generatedFile;
    }

    public void setGeneratedFile(String generatedFile) {
        this.generatedFile = generatedFile;
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
    public static Collection<Class> loadClassesFromFile(String fileName) throws Exception {
        LineNumberReader reader = new LineNumberReader(new FileReader(fileName));
        ArrayList<Class> classes = new ArrayList<Class>();
        String line = null;

        while ((line = reader.readLine()) != null) {
            String klassName = line;
            classes.add( Class.forName(klassName) );
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
