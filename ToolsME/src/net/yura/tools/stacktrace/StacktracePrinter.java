/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.tools.stacktrace;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

  private final Map<Integer, Method> methods = new HashMap<Integer, Method>();
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
          return name.endsWith(".tra");
        }
      });
      if(files==null)
        throw new BuildException("Stacktrace directory specified in dir parameter does not exist");
      if(files.length==0)
        throw new BuildException("No stacktraces found in directory specified in dir parameter");
      StringBuffer out = new StringBuffer();
      for (int i = 0; i < files.length; i++) {
        try {
          DataInputStream stream = new DataInputStream(new FileInputStream(files[i]));
          out.append(stream.readUTF()).append(" @ ").append(new Date(Long.parseLong(files[i].getName().substring(0, files[i].getName().length()-4)))).append('\n');
          while (stream.available()>0) {
            Method method = methods.get(stream.readInt());
            out.append("\tat " + method.toString(stream.readUnsignedShort(), stream.readUTF())).append('\n');
          }
        } catch (IOException e) {
          log("Cannot read stacktrace " + files[i].getName(), e, 0);
        }
        getProject().setProperty("net.yura.tools.stacktrace", out.toString());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static class Method extends EmptyVisitor {

    private int line = Integer.MAX_VALUE;
    private final String className;
    private final String methodName;

    public Method(String className, String methodName) {
      this.className = className;
      this.methodName = methodName;
    }

    public void visitLineNumber(int line, Label label) {
      this.line = Math.min(this.line, line-1);
    }

    public String toString(int line, String args) {
      if(line==0)
        line = this.line;
      if(args.length()>0)
        args = "[" + args + "]";
      return className.replace('/', '.') + "." + methodName + args + "(" + className.substring(className.lastIndexOf('/')+1, (className + "$").indexOf('$')) + ".java:" + line + ")";
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
      final Method method = new Method(className, name);
      methods.put((int) crc.getValue(), method);
      return method;
    }
  }
}
