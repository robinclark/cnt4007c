
public class PieceMessage implements Message{
	int length;
	byte type;
	byte[] payload;

	@Override
	public void setMsgLen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMsgType(byte type) {
		// TODO Auto-generated method stub
		this.type = type;
	}

	@Override
	public void setMsgPayLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMsgPayLoad(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMsgPayLoad(byte[] payload) {
		// TODO Auto-generated method stub
		System.arraycopy(payload,0,this.payload,0,payload.length);
	}

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

	@Override
	public long getUID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMsgLen(int length) {
		// TODO Auto-generated method stub
		
	}

}
