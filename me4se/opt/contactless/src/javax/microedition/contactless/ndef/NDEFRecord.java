package javax.microedition.contactless.ndef;

public class NDEFRecord {

	public NDEFRecord(byte[] data, int offset) {
		
	}

	public NDEFRecord(NDEFRecordType recordType, byte[] id, byte[] payload) {
		
	}

	public void appendPayload(byte[] payload) {
		
	}

	public byte[] getId() {
		return null;
	}
    
	public NDEFMessage getNestedNDEFMessage(int offset) {
		return null;
	}

	public byte[] getPayload() {
		return null;
	}
   
	public long getPayloadLength() {
		return -1;
	}
    
	public NDEFRecordType getRecordType() {
		return null;
	}
    
	public void setId(byte[] id) {
		
	}
    
	public byte[] toByteArray() {
		return null;
	}
}