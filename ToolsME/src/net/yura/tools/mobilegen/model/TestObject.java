package net.yura.tools.mobilegen.model;

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
//    private byte[] image;
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
//    public byte[] getImage() {
//        return image;
//    }
//
//    public void setImage(byte[] image) {
//        this.image = image;
//    }

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

    
    public static class Number {
        private String number;
        private int type;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

}
