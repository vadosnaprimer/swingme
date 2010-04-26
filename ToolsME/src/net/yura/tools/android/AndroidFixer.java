package net.yura.tools.android;

import java.io.*;
import java.util.jar.*;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AndroidFixer {

	public static void main(String args[]) {

		try {

			if (args.length == 2 || args.length == 3) {
				System.out.println("AndroidFixer: Fixing " + (new File(args[1])).getCanonicalPath());
			}

			if (args.length == 2) {
				File dir = new File(args[1]);
				if (!dir.exists()) {
					System.out.println("AndroidFixer: Directory not found! (" + args[1] + ")");
					System.out.println("Working dir is " + (new File(".")).getCanonicalPath());
					return;
				}

				processDir(args[0], new File(args[1]));
			}
			else if (args.length == 3) {
				processJar(args[0], new File(args[1]), new File(args[2]));
			} else {
				System.out.println("usage1: AndroidFixer getResourceAsStream_class_path <Class directory>");
				System.out.println("usage2: AndroidFixer getResourceAsStream_class_path <infile.jar> <outfile.jar>");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void processJar(String replaceClassName, File jarInputFile, File jarOutputFile) throws IOException {
		JarInputStream jis = null;
		JarOutputStream jos = null;
		try {
			jis = new JarInputStream(new FileInputStream(jarInputFile));
			Manifest manifest = jis.getManifest();
			if (manifest == null) {
				jos = new JarOutputStream(new FileOutputStream(jarOutputFile));
			} else {
				jos = new JarOutputStream(new FileOutputStream(jarOutputFile), manifest);
			}

			byte[] inputBuffer = new byte[1024];
			JarEntry jarEntry;
			while ((jarEntry = jis.getNextJarEntry()) != null) {
				if (jarEntry.isDirectory() == false) {
					String name = jarEntry.getName();
					int size = 0;
					int read;
					int length = inputBuffer.length;
					while ((read = jis.read(inputBuffer, size, length)) > 0) {
						size += read;

						length = 1024;
						if (size + length > inputBuffer.length) {
							byte[] newInputBuffer = new byte[size + length];
							System.arraycopy(inputBuffer, 0, newInputBuffer, 0, inputBuffer.length);
							inputBuffer = newInputBuffer;
						}
					}

					byte[] outputBuffer = inputBuffer;
					int outputSize = size;
					if (name.endsWith(".class")) {
				        outputBuffer = instrument(replaceClassName, new ByteArrayInputStream(inputBuffer, 0, size));
				        if (outputBuffer == null) {
				        	outputBuffer = inputBuffer;
				        }
				        outputSize = outputBuffer.length;
					}
					jos.putNextEntry(new JarEntry(name));
					jos.write(outputBuffer, 0, outputSize);
				}
			}
		} finally {
			if (jis != null) {
				jis.close();
			}
			if (jos != null) {
				jos.close();
			}
		}
	}

	public static void processDir(String replaceClassName, File inputDir) throws IOException {
		File[] dirFiles = inputDir.listFiles();

		for (int i = 0; i < dirFiles.length; i++) {
			if (dirFiles[i].isDirectory()) {

				processDir(replaceClassName, dirFiles[i]); // Recursive call

			} else if (dirFiles[i].getName().endsWith(".class")) {

				FileInputStream fileInput = new FileInputStream(dirFiles[i]);
				byte[] outputBuffer = instrument(replaceClassName, fileInput);
				fileInput.close();

				if (outputBuffer != null) {
					FileOutputStream fileOutput = new FileOutputStream(dirFiles[i]);
					fileOutput.write(outputBuffer);
					fileOutput.close();
				}
			}
		}
	}


	private static byte[] instrument(String replaceClassName, final InputStream classInputStream) throws IOException {
		ClassReader cr = new ClassReader(classInputStream);
		ClassWriter cw = new ClassWriter(0);
		AndroidClassVisitor cv = new AndroidClassVisitor(cw, replaceClassName);
		cr.accept(cv, 0);

		return cv.hasChanged() ? cw.toByteArray() : null;
    }
}

class AndroidClassVisitor extends ClassAdapter {

	private String className;
	private String replaceClassName;
	private boolean hasChanged;

	public AndroidClassVisitor(ClassVisitor cv, String replaceClassName) {
		super(cv);
		this.replaceClassName = replaceClassName.replace('.', '/');
	}

	public MethodVisitor visitMethod(final int access, final String name, String desc, final String signature, final String[] exceptions) {
		return new AndroidMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), this);
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);

		this.className = name;
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	public void setChanged() {
		hasChanged = true;
	}

	public String getClassName() {
		return className;
	}

	public String getReplaceClassName() {
		return replaceClassName;
	}
}

class AndroidMethodVisitor extends MethodAdapter {
	private AndroidClassVisitor classVisitor;

	public AndroidMethodVisitor(MethodVisitor mv, AndroidClassVisitor classVisitor) {
		super(mv);

		this.classVisitor = classVisitor;
	}

	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		if (opcode == Opcodes.INVOKEVIRTUAL && "java/lang/Class".equals(owner) && "getResourceAsStream".equals(name)) {

		    String replaceClass = classVisitor.getReplaceClassName();
		    System.out.println("Replacing " + classVisitor.getClassName() + "." + name + "() -> " +
                    replaceClass + "." + name + "()...");

		    if (replaceClass.equals(classVisitor.getClassName())) {
		        System.out.println("[IGNORED!] > Source class is same as destination...");
		    } else {
    			mv.visitMethodInsn(Opcodes.INVOKESTATIC, replaceClass, name, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/io/InputStream;");
    			classVisitor.setChanged();
		    }
			return;
		}

		mv.visitMethodInsn(opcode, owner, name, desc);
	}
}
