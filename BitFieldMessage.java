public class BitFieldMessage extends Message{
	private int msg_len;
	private int msg_type;
	private byte[] msg_payload;
	private static final long serialVersionUID = 4L;
	

	@Override
	public void setMsgLen() {
		msg_len = msg_payload.length;
	}

	@Override
	public void setMsgType(int type) {	
		msg_type = type;
		
	}

	@Override
	public void setMsgPayLoad(byte[] payload) {
		
		msg_payload = payload;
	}

	@Override
	public int getMsgType() {
		return msg_type;
	}

	@Override
	public int getMsgLen() {
		
		return msg_len;
	}
	
	public byte[] getMsgPayLoad()
	{
		return msg_payload;
	}
	
	public long getUID()
	{
		return serialVersionUID;
	}
	
	public Message getMessage()
	{
		return this;
	}
	

}

