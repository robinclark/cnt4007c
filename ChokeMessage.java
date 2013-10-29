import java.nio.ByteBuffer;

public class ChokeMessage extends NormalMessage {
	private byte[] msg_len;
	private byte[] msg_type;
	private byte[] msg_payload;
	private byte[] newMsg;
	
	
	@Override
	public void setMsgLen() {
		msg_len = ByteBuffer.allocate(4).putInt(msg_type.length).array();
	}

	@Override
	public void setMsgType(int type) {	
		msg_type = ByteBuffer.allocate(4).putInt(type).array();
		
	}

	@Override
	public void setMsgPayLoad() {
		msg_payload = new byte[4];
	}
	
	
	public byte[] getMessage()
	{
		newMsg = new byte[msg_len.length + msg_type.length + msg_payload.length];
		System.arraycopy(msg_len, 0, newMsg, 0, msg_len.length);
		System.arraycopy(msg_type, 0, newMsg, 0, msg_type.length);
		System.arraycopy(msg_payload, 0, newMsg, 0, msg_payload.length);
		
		
		return newMsg;
	}

	@Override
	public void setMsgPayLoad(byte[] payload) {
		
		msg_payload = new byte[4];
	}
	
	

}
