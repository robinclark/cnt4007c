public class peerInfo
{
	private int peerID;
	private String hostName;
	private int portNumber;
	private boolean hasFile;
	
	protected void setPeerID(int peerID)
	{
		this.peerID = peerID;
	}

	public int getPeerID()
	{		
		return peerID;
	}

	protected void setHostName(String hostName)
	{
		this.hostName = hostName;
	}

	public String getHostName()
	{
		return hostName;
	}

	protected void setPortNumber(int portNumber)
	{
		this.portNumber = portNumber;
	}

	public int getPortNumber()
	{
		return portNumber;
	}

	protected void setHasFile(boolean hasFile)
	{
		this.hasFile = hasFile;
	}

	public boolean getHasFile()
	{
		return hasFile;
	}

}