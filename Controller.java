import java.io.IOException;

public class Controller extends Module {
	
	private Configuration configInstance;
	private Logger logInstance;
	private String peerID;
	private Server serverInstance;
	private List<Peer> neighborPeers;

	
	@Override
	public void intialConfiguration() {
		
		ModuleFactory factory = new ModuleFactory();
			if(configInstance == null)
			{
				configInstance = (Configuration) factory.createConfigMod();
				
			}
			
			if(logInstance == null)
			{
					logInstance = (Logger) factory.createLogMod(peerID);
			}

	}
	
	public void execute()
	{
	  String msg;
		try {
			msg = logInstance.testMessage();
			logInstance.writeLogger(msg);
			logInstance.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setPeerID(String peerID)
	{
		this.peerID = peerID;
	}
	
	

}

