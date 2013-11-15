import java.io.Serializable;

public class HandShakeMessage implements Message
{
	
	private int msg_type;
	private String peerID;
	
	private static final long serialVersionUID = 3L;

	public long getUID() {
		return serialVersionUID;
	}
	
	public String getHeader()
	{
		return Constants.HANDSHAKE_HEADER;
	}
	
	public void setPeerID(String peerID)
	{
		this.peerID = peerID; 
	}
	
	public String getPeerID()
	{
		return peerID;
	}
	
	public HandShakeMessage getMessage()
	{
		return this;
	}

	@Override
	public void setMsgLen() {
		
		
	}

	@Override
	public void setMsgType(int type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMsgPayLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMsgPayLoad(byte[] payload) {
		// TODO Auto-generated method stub
		
	}

	public void setMsgPayLoad(int index){}

	@Override
	public int getMsgType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMsgLen() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
