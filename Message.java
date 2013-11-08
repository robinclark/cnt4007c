import java.io.Serializable;

interface Message extends Serializable {

	static final long serialVersionUID = 1L;
	public  void setMsgLen();
	public void setMsgType(int type);
	public  void setMsgPayLoad();
	public  void setMsgPayLoad(byte[] payload);
	public  int getMsgType();
	public  int getMsgLen();
	public  long getUID();

	
}
