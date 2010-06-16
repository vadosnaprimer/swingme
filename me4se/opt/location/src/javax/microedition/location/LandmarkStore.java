package javax.microedition.location;

import java.util.Enumeration;

public class LandmarkStore {

	private LandmarkStore() {
		System.out.println("ME4SE: LandmarkStore() NYI!");
	}
	
	public void addCategory(String categoryName) {
		System.out.println("ME4SE: LandmarkStore.addCategory(String) NYI!");
	}
	
	public void addLandmark(Landmark landmark, String category) {
		System.out.println("ME4SE: LandmarkStore.addLandmark(Landmark, String) NYI!");	
	}
	
	public static void createLandmarkStore(String storeName) {
		System.out.println("ME4SE: LandmarkStore.createLandmarkStore(String) NYI!");
	}
	
	public void deleteCategory(String categoryName) {
		System.out.println("ME4SE: LandmarkStore.deleteCategory(String) NYI!");	
	}

	public void deleteLandmark(Landmark lm) {
		System.out.println("ME4SE: LandmarkStore.deleteLandmark(Landmark) NYI!");		
	}

	public static void deleteLandmarkStore(String storeName) {
		System.out.println("ME4SE: LandmarkStore.deleteLandmarkStore(String) NYI!");
	}

	public Enumeration getCategories() {
		System.out.println("ME4SE: LandmarkStore.getCategories() NYI!");
		return null;
	}
	
	public static LandmarkStore getInstance(String storeName) {
		System.out.println("ME4SE: LandmarkStore.getInstance(String) NYI!");
		return null;
	}

	public Enumeration getLandmarks() {
		System.out.println("ME4SE: LandmarkStore.getLandmarks() NYI!");
		return null;
	}
	
	public Enumeration getLandmarks(String category, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
		System.out.println("ME4SE: LandmarkStore.getLandmarks(String, double, double, double, double) NYI!");
		return null;
	}

	public Enumeration getLandmarks(String category, String name) {
		System.out.println("ME4SE: LandmarkStore.getLandmarks(String, String) NYI!");
		return null;
	}
	
	public static String[] listLandmarkStores() {
		System.out.println("ME4SE: LandmarkStore.listLandmarkStores() NYI!");
		return null;
	}

	public void removeLandmarkFromCategory(Landmark lm, String category) {
		System.out.println("ME4SE: LandmarkStore.removeLandmarkFromCategory(Landmark, String) NYI!");
	}
	
	public void	updateLandmark(Landmark lm) {
		System.out.println("ME4SE: LandmarkStore.updateLandmark(Landmark) NYI!");
	}	
}