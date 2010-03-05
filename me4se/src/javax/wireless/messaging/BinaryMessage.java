package javax.wireless.messaging;

public interface BinaryMessage extends Message {

	public byte[] getPayloadData();
	public void setPayloadData(byte[] data);
}
