package javax.microedition.location;

public class Landmark {

	public Landmark(String name, String description, QualifiedCoordinates coordinates, AddressInfo addressInfo) {
		System.out.println("ME4SE: Landmark(String, String, QualifiedCoordinates, AddressInfo) NYI!");
	}

	public AddressInfo getAddressInfo() {
		System.out.println("ME4SE: Landmark.getAddressInfo() NYI!");
		return null;
	}
	
	public String getDescription() {
		System.out.println("ME4SE: Landmark.getDescription() NYI!");
		return null;
	}
	
	public String getName() {
		System.out.println("ME4SE: Landmark.getName() NYI!");
		return null;
	}
	
	public QualifiedCoordinates getQualifiedCoordinates() {
		System.out.println("ME4SE: Landmark.getQualifiedCoordinates() NYI!");
		return null;
	}
	
	public void setAddressInfo(AddressInfo addressInfo) {	
		System.out.println("ME4SE: Landmark.setAddressInfo(AddressInfo) NYI!");
	}
	
	public void setDescription(String description) {
		System.out.println("ME4SE: Landmark.setDescription(String) NYI!");
	}

	public void setName(String name) {
		System.out.println("ME4SE: Landmark.setName(String) NYI!");
	}
	
	public void setQualifiedCoordinates(QualifiedCoordinates coordinates) {
		System.out.println("ME4SE: Landmark.setQualifiedCoordinates(QualifiedCoordinates) NYI!");
	}	
}