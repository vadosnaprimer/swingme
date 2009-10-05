package net.yura.tools.mobilegen.model;

public class Number {

    private String number;
    private int type;
    
    public String getNumber()
    {
        return this.number;
    }

    public int getType()
    {
        return this.type;
    }
    
    public void setNumber(String number)
    {
        this.number = number;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Number other = (Number) obj;
        if ((this.number == null) ? (other.number != null) : !this.number.equals(other.number)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.number != null ? this.number.hashCode() : 0);
        hash = 79 * hash + this.type;
        return hash;
    }
}
