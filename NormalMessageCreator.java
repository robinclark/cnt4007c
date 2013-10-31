public class NormalMessageCreator {
	private Message builder;
	
	public NormalMessageCreator(Message builder)
	{
		this.builder = builder;
	}
	
	public void createNormalMessage(int type, byte[] payload)
	{
		builder.setMsgType(type);
		builder.setMsgPayLoad(payload);
		builder.setMsgLen();
	}
	
	
}
