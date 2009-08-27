package net.yura.tools.mobilegen;

import java.util.Vector;

/**
 * @author Lenin
 */

public class TestObject extends Test {

    private String name;
    private byte age;
    private Vector numbers;

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
