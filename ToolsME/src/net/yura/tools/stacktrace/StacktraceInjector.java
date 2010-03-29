/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.yura.tools.stacktrace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.EmptyVisitor;
//import org.objectweb.asm.util.ASMifierClassVisitor;

/**
 *
 * @author Administrator
 *
 */
public class StacktraceInjector extends Task {

  private final static String CALLSTACK_FILE = "net\\yura\\mobile\\util\\CallStack.class";
  private final static String CALLSTACK_CLASS = "net/yura/mobile/util/CallStack";
  private final static String GET_CALLSTACK = "getCallStack";
  private FileSet classFiles;
  private boolean lineNumbers, arguments;
  private Map<String, Method> methods;
  private final Set<String> pushMethods = new HashSet<String>();
  private int oldSize = 0, newSize = 0, injectedClasses = 0, totalMethods, injectedMethods;

  public StacktraceInjector() {
    log("StacktraceInjector v1.0");
  }

  public void addFileSet(FileSet classFiles) {
    this.classFiles = classFiles;
  }

  public void setLineNumbers(boolean lineNumbers) {
    this.lineNumbers = lineNumbers;
    if (lineNumbers) {
      log("Line numbers enabled");
    }
  }

  public void setArguments(boolean arguments) {
    this.arguments = arguments;
    if (arguments) {
      log("Arguments enabled");
    }
  }

  public void setMethods(boolean methods) {
    if (methods) {
      this.methods = new HashMap<String, Method>();
    } else {
      log("Skipping injection");
    }
  }

  public void execute() {
    try {
//      getPushMethods();
//      if (true) {
//        return;
//      }
      if (methods == null) {
        return;
      }
      List<Resource> resources = new ArrayList<Resource>();
      for (Iterator<Resource> i = classFiles.iterator(); i.hasNext();) {
        final Resource classFile = i.next();
        if (!classFile.getName().equals(CALLSTACK_FILE)) {
          try {
            InputStream inputStream = classFile.getInputStream();
            ClassReader reader = new ClassReader(inputStream);
            reader.accept(new MethodAnalyzer(), 0);
            resources.add(classFile);
            oldSize += classFile.getSize();
          } catch (IllegalStateException e) {
          }
        }
      }
      for (Iterator<Resource> i = resources.iterator(); i.hasNext();) {
        final Resource classFile = i.next();
        try {
          InputStream inputStream = classFile.getInputStream();
          ClassReader reader = new ClassReader(inputStream);
          ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
          if (!classFile.getName().startsWith(CALLSTACK_FILE.substring(0, CALLSTACK_FILE.indexOf('.')))) {
            reader.accept(new MethodInjector(writer), 0);
            byte[] b = writer.toByteArray();
            inputStream.close();
            OutputStream outputStream = classFile.getOutputStream();
            outputStream.write(b);
            outputStream.close();
            injectedClasses++;
          }
        } catch (IllegalStateException e) {
        }
        newSize += classFile.getSize();
      }
      File classFile = new File(classFiles.getDir(), CALLSTACK_FILE);
      if (!classFile.exists()) {
        throw new BuildException("Missing net.yura.mobile.util.CallStack class");
      }
      InputStream inputStream = new FileInputStream(classFile);
      ClassReader reader = new ClassReader(inputStream);
      ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
      try {
        reader.accept(new PushMethodGenerator(writer), 0);
      } finally {
        inputStream.close();
      }
      byte[] b = writer.toByteArray();
      inputStream.close();
      OutputStream outputStream = new FileOutputStream(classFile);
      outputStream.write(b);
      outputStream.close();
      log(injectedClasses + " classes injected");
      log((injectedMethods * 100) / Math.max(1, totalMethods) + "% of methods injected");
      log((100 - ((oldSize * 100) / newSize)) + "% code size increase");
    } catch (IOException e) {
      log(e, 0);
    }
  }
//  private Set<String> getPushMethods() throws IOException {
//    File classFile = new File(classFiles.getDir(), CALLSTACK_FILE);
//    InputStream inputStream = new FileInputStream(classFile);
//    ClassReader reader = new ClassReader(inputStream);
//    reader.accept(new ASMifierClassVisitor(new PrintWriter(System.out)), ClassReader.SKIP_DEBUG);
//    inputStream.close();
//    return pushMethods;
//  }
  private final Method THROWING_METHOD = new Method();

  private class Method extends EmptyVisitor implements Opcodes {

    private boolean throwsExceptions;
    private final Set<Method> calls;
    private final Map<Integer, Method> lines;

    public Method() {
      this.throwsExceptions = true;
      this.calls = new HashSet<Method>();
      this.lines = new HashMap<Integer, Method>();
    }

    public void visitCode() {
      throwsExceptions = true;
    }

    public void visitInsn(int opcode) {
      if (opcode == ATHROW) {
        throwsExceptions = true;
      }
    }

    public void visitLineNumber(int line, Label label) {
      lines.put(line, THROWING_METHOD);
    }

    public boolean throwsExceptions() {
      for (Iterator<Method> i = calls.iterator(); !throwsExceptions && i.hasNext();) {
        throwsExceptions = i.next().throwsExceptions();
      }
      return throwsExceptions;
    }

    public boolean throwsExceptions(int line) {
      return lines.get(line).throwsExceptions();
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
      if (owner.equals(CALLSTACK_CLASS) && name.equals("push")) {
        throw new IllegalStateException();
      }
      name = owner + "/" + name + desc;
      Method method = methods.get(name);
      if (method == null) {
        methods.put(name, method = new Method());
      }
      calls.add(method);
    }
  }

  private class MethodAnalyzer extends EmptyVisitor {

    private boolean hasMethods = false;
    private String className;

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      super.visit(version, access, name, signature, superName, interfaces);
      className = name;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      hasMethods = true;
      name = className + "/" + name + desc;
      Method method = methods.get(name);
      if (method == null) {
        methods.put(name, method = new Method());
      }
      return method;
    }

    public FieldVisitor visitField(int i, String string, String string1, String string2, Object o) {
      return null;
    }

    public AnnotationVisitor visitAnnotation(String string, boolean string1) {
      return null;
    }

    public void visitEnd() {
      if (!hasMethods) {
        throw new IllegalStateException();
      }
    }
  }

  private class MethodInjector extends ClassAdapter implements Opcodes {

    private String className;

    public MethodInjector(ClassVisitor cv) {
      super(cv);
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      super.visit(version, access, name, signature, superName, interfaces);
      className = name;
      if ((access & ACC_INTERFACE) != 0) {
        throw new IllegalStateException();
      }
    }

    public MethodVisitor visitMethod(final int access, String name, final String desc, String signature, String[] exceptions) {
      totalMethods++;
      final Method method = methods.get(className + "/" + name + desc);
      if (!method.throwsExceptions()) {
        return super.visitMethod(access, name, desc, signature, exceptions);
      }
      injectedMethods++;
      final String pushDesc = "(I" + (arguments ? desc.substring(1, desc.indexOf(')') + 1).replaceAll("\\[+[^L]|\\[*L[^;]+;", "Ljava/lang/Object;").replaceAll("C|B|S", "I") : ")") + "L" + CALLSTACK_CLASS + ";";
      pushMethods.add(pushDesc);
      final Checksum crc = new CRC32();
      byte[] b;
      crc.update(b = className.getBytes(), 0, b.length);
      crc.update(b = name.getBytes(), 0, b.length);
      crc.update(b = desc.getBytes(), 0, b.length);
      return new AdviceAdapter(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc) {

        private int index = -1;
        private Set<Label> exceptionHandlers = new HashSet<Label>();

        protected void onMethodEnter() {
          mv.visitLdcInsn((int) crc.getValue());
          Type[] args = Type.getArgumentTypes(desc);
          for (int i = 0, j = (access & ACC_STATIC) == 0 ? 1 : 0; arguments && i < args.length; j += args[i].getSize(), i++) {
            mv.visitVarInsn(args[i].getOpcode(ILOAD), j);
          }
          mv.visitMethodInsn(INVOKESTATIC, CALLSTACK_CLASS, "push", pushDesc);
          index = newLocal(Type.getObjectType(CALLSTACK_CLASS));
          mv.visitVarInsn(ASTORE, index);
        }

        public void visitLineNumber(int line, Label label) {
          super.visitLineNumber(line, label);
          if (lineNumbers && index != -1 && method.throwsExceptions(line)) {
            mv.visitVarInsn(ALOAD, index);
            mv.visitLdcInsn(line);
            mv.visitFieldInsn(PUTFIELD, CALLSTACK_CLASS, "line", "I");
          }
        }

        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
          super.visitTryCatchBlock(start, end, handler, type);
          exceptionHandlers.add(handler);
        }

        public void visitLabel(Label label) {
          super.visitLabel(label);
          if (exceptionHandlers.contains(label)) {
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, index);
            mv.visitInsn(SWAP);
            mv.visitLdcInsn((int) crc.getValue());
            mv.visitMethodInsn(INVOKEVIRTUAL, CALLSTACK_CLASS, "unwind", "(Ljava/lang/Throwable;I)V");
          }
        }

        protected void onMethodExit(int opcode) {
          if (opcode != ATHROW) {
            mv.visitVarInsn(ALOAD, index);
            mv.visitMethodInsn(INVOKEVIRTUAL, CALLSTACK_CLASS, "pop", "()V");
          }
        }
      };
    }
  }

  private class PushMethodGenerator extends ClassAdapter implements Opcodes {

    public PushMethodGenerator(ClassVisitor cv) {
      super(cv);
    }

    public MethodVisitor visitMethod(final int access, String name, final String desc, String signature, String[] exceptions) {
      if (name.equals("push")) {
        pushMethods.remove(desc);
      }
      return new MethodAdapter(super.visitMethod(access, name, desc, signature, exceptions)) {

        public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
//          for (Iterator<String> i = pushMethods.iterator(); i.hasNext();) {
//            Type[] args = Type.getArgumentTypes(desc);
//            int ints = 0, objects = 0;
//            for (int j = 1; j < args.length; j++) {
//
//            }
//          }
          super.visitTableSwitchInsn(min, max, dflt, labels);
        }
      };
    }

    public void visitEnd() {
      int x = 0;
      for (Iterator<String> i = pushMethods.iterator(); i.hasNext(); x++) {
        String desc = i.next();
        MethodVisitor mv = super.visitMethod(ACC_FINAL | ACC_PUBLIC | ACC_STATIC, "push", desc, null, null);
        mv.visitCode();
        mv.visitMethodInsn(INVOKESTATIC, CALLSTACK_CLASS, GET_CALLSTACK, "()L" + CALLSTACK_CLASS + ";");
        Type[] args = Type.getArgumentTypes(desc);
        int ints = 0, objects = 0;
        for (int j = 1, k = 1; j < args.length; k += args[j++].getSize()) {
          mv.visitInsn(DUP);
          switch (args[j].getSort()) {
            case Type.LONG:
            case Type.FLOAT:
            case Type.DOUBLE:
              mv.visitTypeInsn(NEW, "java/lang/" + args[j].getClassName().substring(0, 1).toUpperCase() + args[j].getClassName().substring(1));
              mv.visitInsn(DUP);
              mv.visitVarInsn(args[j].getOpcode(ILOAD), k);
              mv.visitMethodInsn(INVOKESPECIAL, "java/lang/" + args[j].getClassName().substring(0, 1).toUpperCase() + args[j].getClassName().substring(1), "<init>", "(" + args[j].getDescriptor() + ")V");
              mv.visitMethodInsn(INVOKEVIRTUAL, CALLSTACK_CLASS, "pushObject", "(Ljava/lang/Object;)V");
              objects++;
              break;
            case Type.ARRAY:
            case Type.OBJECT:
              mv.visitVarInsn(args[j].getOpcode(ILOAD), k);
              mv.visitMethodInsn(INVOKEVIRTUAL, CALLSTACK_CLASS, "pushObject", "(Ljava/lang/Object;)V");
              objects++;
              break;
            default:
              mv.visitVarInsn(args[j].getOpcode(ILOAD), k);
              mv.visitMethodInsn(INVOKEVIRTUAL, CALLSTACK_CLASS, "pushInt", "(I)V");
              ints++;
          }
        }
        mv.visitInsn(DUP);
        mv.visitVarInsn(ILOAD, 0);
        if (ints == 0 && objects == 0) {
          mv.visitInsn(ICONST_0);
        } else {
          mv.visitIntInsn(SIPUSH, (x << 8) + (ints << 4) + objects);
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, CALLSTACK_CLASS, "pushMethod", "(II)V");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
      }
      super.visitEnd();
    }
  }
}
