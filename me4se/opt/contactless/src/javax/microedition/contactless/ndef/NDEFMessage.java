package javax.microedition.contactless.ndef;

public class NDEFMessage {

	public NDEFMessage() {
		
	}
	
	public NDEFMessage(byte[] data, int offset) {
		
	}
	
	public NDEFMessage(NDEFRecord[] records) {
		
	}
	
	public void appendRecord(NDEFRecord record) {
		
	}

	public int getNumberOfRecords() {
		return -1;
	}
	
	public NDEFRecord getRecord(byte[] id) {
		return null;
	}
	
	public NDEFRecord getRecord(int index) {
		return null;
	}
	
	public NDEFRecord[] getRecord(NDEFRecordType recordType) {
		return null;
	}
	
	public NDEFRecord[] getRecords() {
		return null;
	}
	
	public NDEFRecordType[] getRecordTypes() {
		return null;
	}
	
	public void insertRecord(int index, NDEFRecord record) {
		
	}
	
	public void removeRecord(int index) {
		
	}
	
	public void setRecord(int index, NDEFRecord record) {
		
	}
	
	public byte[] toByteArray() {
		return null;
	}	
}
