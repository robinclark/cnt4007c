import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

public class Controller extends Module {
	
	private Configuration configInstance;
	private Logger logInstance;
	private String peerID;
	private Server serverInstance;
	private List<Peer> neighborPeers;

	public Controller(String peerID)
	{
		this.peerID = peerID;
	}
	
	@Override
	public void intialConfiguration() {
			if(configInstance == null)
			{
				configInstance = (Configuration) ModuleFactory.createConfigMod();
				
			}
			
			if(logInstance == null)
			{
					logInstance = (Logger) ModuleFactory.createLogMod(peerID);
			}

			if(serverInstance == null)
			{
					serverInstance = (Server) ModuleFactory.createServerMod(peerID, (Controller) this);
			}
			
			if(neighborPeers == null)
			{
				neighborPeers = new ArrayList<Peer>();
			}

	}
	
	public void execute()
	{
			createServers();
			//createClients();
	}
	
	public void setPeerID(String peerID)
	{
		this.peerID = peerID;
	}

	public void createServers()
	{
		//new Thread(Server)
	}
	
	public int getNumberOfConnectedPeers(String peerID)
	{
		int numOfPeers = 0;
		
		HashMap<String, peerInfo> peers = configInstance.getPeerListCollection();
		
		Set<String> peerKeys = peers.keySet();
		
		for(String peerKey : peerKeys)
		{
			if(Integer.parseInt(peerID) < Integer.parseInt(peerKey))
			{
				numOfPeers++;
			}
		}
		
		return numOfPeers;	
		
	}
	
	public void addNeighbors(Peer peer)
	{
		neighborPeers.add(peer);
	}
	
	public Module getLogger()
	{
		return logInstance;
	}

}

