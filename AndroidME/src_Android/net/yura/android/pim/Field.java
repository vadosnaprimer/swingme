package net.yura.android.pim;

import java.util.ArrayList;
import java.util.Arrays;

public class Field {
	private final FieldInfo fieldInfo;
	private ArrayList<Object> values;
	private ArrayList<Integer> attributes;

	public Field(FieldInfo fieldInfo) {
		this.fieldInfo = fieldInfo;
		this.values = new ArrayList<Object>();
		this.attributes = new ArrayList<Integer>();
	}

	public void add(Object obj, Integer attribute) {
	    this.values.add(obj);
	    this.attributes.add(attribute);
	}

	public void set(int index, Object obj, Integer attribute) {
        this.values.set(index, obj);
        this.attributes.set(index, attribute);
    }

	public FieldInfo getFieldInfo() {
	    return this.fieldInfo;
	}

	public int getSize() {
	    return this.values.size();
	}

	public int getAttribute(int index) {
	    return attributes.get(index);
	}

	public Object getValue(int index) {
        return values.get(index);
    }

	public void remove(int index) {
	    this.values.remove(index);
        this.attributes.remove(index);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.fieldInfo);
		buffer.append("\n");
		int numberOfValues = this.values.size();
		for(int i = 0; i < numberOfValues; i++) {
			Object value = this.values.get(i);
			if(value instanceof Object[]) {
				Object[] array = (Object[]) value;
				value = Arrays.toString(array);
			}
			buffer.append("Value["+i+"]:"+value+".");
			buffer.append("Attr["+i+"]:"+this.attributes.get(i)+".");
			buffer.append("\n");
		}
		buffer.append("\n");
		return buffer.toString();
	}
}