package javax.microedition.m3g;

import java.util.ArrayList;

public class Mesh extends Node {
	private VertexBuffer vertices;
	private ArrayList submeshes = new ArrayList();
	private ArrayList appearances = new ArrayList();

	public Mesh(VertexBuffer vertices,
            IndexBuffer submesh,
            Appearance appearance)
	{
		this.vertices = vertices;
		this.submeshes.add(submesh);
		this.appearances.add(appearance);
	}

	public Mesh(VertexBuffer vertices,
            IndexBuffer[] submeshes,
            Appearance[] appearances)
	{
		this.vertices = vertices;
		for(int i=0; i < submeshes.length; ++i)
			this.submeshes.add(submeshes[i]);
		for(int i=0; i < appearances.length; ++i)
			this.appearances.add(appearances[i]);
	}

	public Appearance getAppearance(int index)
	{
		return (Appearance)appearances.get(index);
	}

	public IndexBuffer getIndexBuffer(int index)
	{
		return (IndexBuffer)submeshes.get(index);
	}

	public int getSubMeshCount()
	{
		return submeshes.size();
	}

	public VertexBuffer getVertexBuffer()
	{
		return vertices;
	}

	public void setAppearance(int index, Appearance appearance)
	{
		appearances.set(index, appearance);
	}

	public int getReferences(Object3D[] references) throws IllegalArgumentException
	{
		int parentCount = super.getReferences(references);

		if(vertices != null)
		{
			if(references != null)
				references[parentCount] = vertices;
			++parentCount;
		}

		for(int i = 0; i < submeshes.size(); ++i)
		{
			if(references != null)
				references[parentCount] = (Object3D)submeshes.get(i);
			++parentCount;
		}

		for(int i = 0; i < appearances.size(); ++i)
		{
			if(references != null)
				references[parentCount] = (Object3D)appearances.get(i);
			++parentCount;
		}

		return parentCount;
	}
}
