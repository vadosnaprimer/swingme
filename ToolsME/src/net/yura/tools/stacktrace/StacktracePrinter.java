/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.tools.stacktrace;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 *
 * @author Administrator
 *
 */
public class StacktracePrinter extends Task {

  private final Set<Method> methods = new HashSet<Method>();
  private FileSet classFiles;
  private File dir;

  public static void main(String[] args) {
      System.out.println(args[0]);
  }

  public void setDir(String dir)
  {
    this.dir = new File(dir);
  }

  public void addFileSet(FileSet classFiles) {
    this.classFiles = classFiles;
  }
  
  public void execute() throws BuildException {
    try {
      for (Iterator<Resource> i = classFiles.iterator(); i.hasNext();) {
        final Resource classFile = i.next();
        InputStream inputStream = classFile.getInputStream();
        ClassReader reader = new ClassReader(inputStream);
        MethodIndexer indexer = new MethodIndexer();
        reader.accept(indexer, 0);
      }
      File[] files = dir.listFiles(new FilenameFilter() {

        public boolean accept(File dir, String name) {
          return name.endsWith(".log");
        }
      });
      if(files==null)
        throw new BuildException("Log directory specified in dir parameter does not exist");
      if(files.length==0)
        throw new BuildException("No logs found in directory specified in dir parameter");
      for (int i = 0; i < files.length; i++) {
        try {
          Reader reader = new FileReader(files[i]);
          String log;
          {
            StringBuffer sbuf = new StringBuffer();
            char[] cbuf = new char[1024];
            for(int read = reader.read(cbuf); read>0; read = reader.read(cbuf))
              sbuf.append(cbuf, 0, read);
            log = sbuf.toString();
          }

          for(Iterator<Method> m = methods.iterator(); m.hasNext();)
            log = m.next().expand(log);
          getProject().setProperty("net.yura.tools.stacktrace", log);
        } catch (IOException e) {
          log("Cannot read log " + files[i].getName(), e, 0);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static class Method extends EmptyVisitor {

    private int line = Integer.MAX_VALUE;
    private final String signature;
    private final String name;


    public Method(String className, String methodName, String methodId) {
      this.signature = methodId + "(?:";
      this.name = className.replace('/', '.') + "." + methodName + "(" + className.substring(className.lastIndexOf('/')+1, (className + "$").indexOf('$')) + ".java:";
    }

    public void visitLineNumber(int line, Label label) {
      this.line = Math.min(this.line, line-1);
    }

    public String expand(String string)
    {
      return string.replace(signature + "0", name + line).replace(signature, name);
    }
  }

  private class MethodIndexer extends EmptyVisitor {

    private String className;

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      super.visit(version, access, name, signature, superName, interfaces);
      className = name;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      final Checksum crc = new CRC32();
      byte[] b;
      crc.update(b = className.getBytes(), 0, b.length);
      crc.update(b = name.getBytes(), 0, b.length);
      crc.update(b = desc.getBytes(), 0, b.length);
      final Method method = new Method(className, name, Integer.toString((int)crc.getValue(), Character.MAX_RADIX));
      methods.add(method);
      return method;
    }
  }
}
