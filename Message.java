import java.io.Serializable;

interface Message extends Serializable {

	static final long serialVersionUID = 1L;
	public  void setMsgLen();
	public void setMsgType(byte type);
	public  void setMsgPayLoad();//***what does this do?
	public  void setMsgPayLoad(int index);	
	public  void setMsgPayLoad(byte[] payload);
	public  int getMsgType();
	public  int getMsgLen();
	public  long getUID();
	

	
}
