
public class InterestedMessage implements Message{
	private int msg_len;
	private int msg_type;
	private byte[] msg_payload;
	private long serialVersionUID;
	
	public InterestedMessage()
	{
		this.serialVersionUID = ++serialVersionUID;
	}
	


	public void setMsgLen() {
		msg_len = 1;
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

	public void setMsgPayLoad(int index, byte[] payload){}

	@Override
	public void setMsgType(byte type) {
		// TODO Auto-generated method stub
		msg_type = type;
	}





}
