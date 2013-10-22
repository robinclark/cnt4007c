public class Server implements Runnable
{
	private Configuration configFile;
	
	public synchronized PeerServer(Configuration configFile)
	{
		this.configFile = configFile;
	}	

}
