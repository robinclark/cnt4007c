import java.io.Serializable;

abstract class  Message implements Serializable {

	private static final long serialVersionUID = 1L;
	public  void setMsgLen(){};
	public void setMsgType(int type){};
	public  void setMsgPayLoad(){};
	public  void setMsgPayLoad(byte[] payload){};
	public  int getMsgType(){return 0;};
	public  int getMsgLen(){return 0;};
	public  long getUID(){return 0;};

	
}
