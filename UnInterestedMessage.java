public class UnInterestedMessage implements Message{
	private int msg_len;
	private int msg_type;
	private byte[] msg_payload;
	private long serialVersionUID;
	
	public UnInterestedMessage()
	{
		this.serialVersionUID = ++serialVersionUID;
	}

	public void setMsgLen() {
		msg_len = msg_payload.length;
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

	@Override
	public void setMsgPayLoad(){}
	public void setMsgPayLoad(int index){}

	@Override
	public void setMsgType(byte type) {
		// TODO Auto-generated method stub
		msg_type = type;
	}

	@Override
	public void setPieceIndex(int index) {
		// TODO Auto-generated method stub
		
	}
	

}
