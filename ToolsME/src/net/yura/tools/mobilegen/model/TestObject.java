package net.yura.tools.mobilegen.model;

import java.util.Arrays;
import java.util.Hashtable;
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
    private String myType;
    private transient Vector arms;
    private transient Hashtable organs;
    private transient boolean isAlive;
    private transient int heads;
    private transient long lastUpdated;
    private transient int things;
    private transient Test andOneInside;

    public Test getAndOneInside() {
        return andOneInside;
    }

    public void setAndOneInside(Test andOneInside) {
        this.andOneInside = andOneInside;
    }

    public int getThings() {
        return things;
    }

    public void setThings(int things) {
        this.things = things;
    }

    public String getMyType() {
        return myType;
    }

    public void setMyType(String myType) {
        this.myType = myType;
    }

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
    public String toString() {
        return "TestObject:"+name+","+age+","+numbers;
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
        if ((this.myType == null) ? (other.myType != null) : !this.myType.equals(other.myType)) {
            return false;
        }
        return true;
    }

    public Vector getArms() {
        return arms;
    }

    public void setArms(Vector arms) {
        this.arms = arms;
    }

    public int getHeads() {
        return heads;
    }

    public void setHeads(int heads) {
        this.heads = heads;
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public Hashtable getOrgans() {
        return organs;
    }

    public void setOrgans(Hashtable organs) {
        this.organs = organs;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + this.age;
        hash = 97 * hash + (this.numbers != null ? this.numbers.hashCode() : 0);
        hash = 97 * hash + (this.body != null ? this.body.hashCode() : 0);
        hash = 97 * hash + (this.legs != null ? Arrays.hashCode(legs) : 0);
        hash = 97 * hash + (this.image != null ?  Arrays.hashCode(image) : 0);
        hash = 97 * hash + (this.objects != null ?  Arrays.hashCode(objects) : 0);
        hash = 97 * hash + (this.myType != null ? this.myType.hashCode() : 0);
        return hash;
    }

}
