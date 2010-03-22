package javax.microedition.m3g;

import java.util.ArrayList;

public class Group extends Node {

	protected ArrayList children;

	public Group()
	{
		children = new ArrayList();
	}

	public void addChild(Node child)
	{
		if (child == null)
			throw new NullPointerException("child can not be null");
		if (child == this)
			throw new IllegalArgumentException("can not add self as child");
//		if (child instanceof World)
//			throw new IllegalArgumentException("node of type World can not be child");
		if (child.parent != null)
			throw new IllegalArgumentException("child already has a parent");

		// todo
		children.add(child);
		child.parent = this;
	}

	public Node getChild(int index)
	{
		return (Node)children.get(index);
	}

	public int getChildCount()
	{
		return children.size();
	}

	public int getReferences(Object3D[] references) throws IllegalArgumentException
	{
		int parentCount = super.getReferences(references);
		if(references != null)
			for(int i = 0; i < children.size(); ++i)
				references[parentCount+i] = (Object3D)children.get(i);
		return parentCount+children.size();
	}

	/*
	public boolean pick(int scope, float x, float y, Camera camera, RayIntersection ri)
	{
	}

	public boolean pick(int scope, float ox, float oy, float oz, float dx, float dy, float dz, RayIntersection ri)
	{
	}
	*/

	public void removeChild(Node child)
	{
		children.remove(child);
		child.parent = null;
	}
}
