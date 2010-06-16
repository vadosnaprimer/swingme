package javax.microedition.location;

public class Criteria {

	public static final int NO_REQUIREMENT = 0;
	public static final int POWER_USAGE_HIGH = 3;
	public static final int POWER_USAGE_LOW = 1;
	public static final int POWER_USAGE_MEDIUM = 2;
	
	public Criteria() {
		System.out.println("ME4SE: Criteria() NYI!");
	}
    
	public int getHorizontalAccuracy() {
		System.out.println("ME4SE: Criteria.getHorizontalAccurancy() NYI!");
		return Integer.MIN_VALUE;
	}

	public int getPreferredPowerConsumption() {
		System.out.println("ME4SE: Criteria.getPreferredPowerConsumption() NYI!");
		return Integer.MIN_VALUE;
	}
	
	public int getPreferredResponseTime() {
		System.out.println("ME4SE: Criteria.getPreferredResponseTime() NYI!");
		return Integer.MIN_VALUE;
	}
	
	public int getVerticalAccuracy() {
		System.out.println("ME4SE: Criteria.getVerticalAccurancy() NYI!");
		return Integer.MIN_VALUE;
	}
	
	public boolean isAddressInfoRequired() {
		System.out.println("ME4SE: Criteria.isAddressInfoRequired() NYI!");
		return false;
	}

	public boolean isAllowedToCost() {
		System.out.println("ME4SE: Criteria.isAllowedToCost() NYI!");
		return false;
	}

	public boolean isAltitudeRequired() {
		System.out.println("ME4SE: Criteria.isAltitudeRequired() NYI!");
		return false;
	}
    
	public boolean isSpeedAndCourseRequired() {
		System.out.println("ME4SE: Criteria.isSpeedAndCourceRequired() NYI!");
		return false;
	}

	public void setAddressInfoRequired(boolean addressInfoRequired) {
		System.out.println("ME4SE: Criteria.addressInfoRequired(boolean) NYI!");
	}

	public void setAltitudeRequired(boolean altitudeRequired) {
		System.out.println("ME4SE: Criteria.setAltitudeRequired(boolean) NYI!");
	}

	public void setCostAllowed(boolean costAllowed) {
		System.out.println("ME4SE: Criteria.setCostAllowed(boolean) NYI!");
	}

	public void setHorizontalAccuracy(int accuracy) {
		System.out.println("ME4SE: Criteria.setHorizontalAccurancy(int) NYI!");
	}

	public void setPreferredPowerConsumption(int level) {
		System.out.println("ME4SE: Criteria.setPreferredPowerConsumption(int) NYI!");
	}

	public void setPreferredResponseTime(int time) {
		System.out.println("ME4SE: Criteria.setPreferredResponseTime(int) NYI!");
	}

    public void setSpeedAndCourseRequired(boolean speedAndCourseRequired) {
		System.out.println("ME4SE: Criteria.setSpeedAndCourceRequired(boolean) NYI!");
    }

    public void setVerticalAccuracy(int accuracy) {
		System.out.println("ME4SE: Criteria.setVerticalAccurncy(int) NYI!");
    }	
}