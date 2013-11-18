public class NormalMessageCreator {
	private Message builder;
	
	public NormalMessageCreator(Message builder)
	{
		this.builder = builder;
	}
	
	public void createNormalMessage(byte type, byte[] payload)
	{
		builder.setMsgType(type);
		builder.setMsgPayLoad(payload);
		builder.setMsgLen();
	}

	public void createNormalMessage(byte type)
	{
		builder.setMsgType(type);
		builder.setMsgPayLoad();
		builder.setMsgLen();
	}

	public void createNormalMessage(byte type, int index)
	{
		builder.setMsgType(type);
		builder.setMsgPayLoad(index);
		builder.setMsgLen();
	}
}
	
	

