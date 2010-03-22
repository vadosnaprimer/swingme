package javax.microedition.m3g;

import java.nio.IntBuffer;
//JPimport com.sun.opengl.util.*;

public abstract class IndexBuffer extends Object3D {
	protected IntBuffer buffer = null;

	public int getIndexCount()
	{
		return buffer.limit();
	}

	public abstract void getIndices(int[] indices);

	protected void allocate(int numElements)
	{
//JP		buffer = BufferUtil.newIntBuffer(numElements);
		buffer = IntBuffer.allocate(numElements);
	}

	IntBuffer getBuffer() {
		return buffer;
	}
}
