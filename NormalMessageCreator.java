public class NormalMessageCreator {
	private NormalMessage builder;
	
	public NormalMessageCreator(NormalMessage builder)
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
