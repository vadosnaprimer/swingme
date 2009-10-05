package net.yura.tools.mobilegen.model;

import java.util.Arrays;
import java.util.Vector;

/**
 * @author Lenin
 */

public class TestObject extends Test {

    private String name;
    private byte age;
    private Vector numbers;
    private Object body;
    private String[] legs;
    private byte[] image;
    private Object[] objects;

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    public void addNumber(String string) {
        if (legs==null) {
            legs = new String[] {string};
        }
        else {
            String[] newa = new String[legs.length+1];
            System.arraycopy(legs,0,newa,0,legs.length);
            newa[legs.length] = string;
            legs = newa;
        }
    }
//    private long[] times;
//
//    public long[] getTimes() {
//        return times;
//    }
//
//    public void setTimes(long[] times) {
//        this.times = times;
//    }
//
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String[] getLegs() {
        return legs;
    }

    public void setLegs(String[] legs) {
        this.legs = legs;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public byte getAge() {
        return age;
    }

    public void setAge(byte age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector getNumbers() {
        return numbers;
    }

    public void setNumbers(Vector numbers) {
        this.numbers = numbers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestObject other = (TestObject) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.age != other.age) {
            return false;
        }
        if (this.numbers != other.numbers && (this.numbers == null || !this.numbers.equals(other.numbers))) {
            return false;
        }
        if (this.body != other.body && (this.body == null || !this.body.equals(other.body))) {
            return false;
        }
        if (this.legs != other.legs && (this.legs == null || !Arrays.equals(this.legs,other.legs))) {
            return false;
        }
        if (this.image != other.image && (this.image == null || !Arrays.equals(this.image,other.image))) {
            return false;
        }
        if (this.objects != other.objects && (this.objects == null || !Arrays.equals(this.objects,other.objects))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 41 * hash + this.age;
        hash = 41 * hash + (this.numbers != null ? this.numbers.hashCode() : 0);
        hash = 41 * hash + (this.body != null ? this.body.hashCode() : 0);
        hash = 41 * hash + (this.legs != null ? Arrays.hashCode(this.legs) : 0);
        hash = 41 * hash + (this.image != null ? Arrays.hashCode(this.image) : 0);
        hash = 41 * hash + (this.objects != null ? Arrays.hashCode(this.objects) : 0);
        return hash;
    }

    

}
