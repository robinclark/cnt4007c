import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Controller extends Module {
	
	private Configuration configInstance;
	private Logger logInstance;
	private BitFieldManager bitFieldManager;
	private String peerID;
	private Server serverInstance;
	private List<Peer> neighborPeers;
	private boolean isShuttingDown;
	private static Controller ctrl;
        private OptimisticNeighborManager optimisticNeighborManager;
        private PreferredNeighborManager preferredNeighborManager;


	public synchronized static Module createCtrlMod(String peerID)
	{
		ctrl = new Controller(peerID);
		ctrl.initialConfiguration();
		return ctrl;
	}

	public Controller(String peerID)
	{
		this.peerID = peerID;
	}
	
	@Override
	public void initialConfiguration() {
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

			if(bitFieldManager == null)
			{
				bitFieldManager = new BitFieldManager(this);
				System.out.println("BIT: " + bitFieldManager);
			}
			if(fileHandler == null)
			{
				fileHandlerInstance = (FileHandler) ModuleFactory.createFileHandlerInstance(configInstance);				
			}
			
			isShuttingDown = false;

	}
	
	public void execute()
	{
			try {
				createServers();	
				createClients();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void setPeerID(String peerID)
	{
		this.peerID = peerID;
	}
	
	public String getPeerID()
	{
		return peerID;
	}
	
	public boolean isShuttingDown()
	{
		return isShuttingDown;
	}

	public void createServers()
	{
		new Thread(serverInstance).start();
	}
	
	public void createClients() throws UnknownHostException, IOException
	{
		HashMap<String, Configuration.PeerInfo> map = configInstance.getPeerList();

		Set<String> peerKeys = map.keySet();
		
		
		for(String peerKey :  peerKeys)
		{
		
				if(Integer.parseInt(peerID) > Integer.parseInt(peerKey))
				{
					Socket socket = new Socket(map.get(peerKey).getHostName(), map.get(peerKey).getPortNumber());
					System.out.println("CTRL1: " + this);
					Peer clientPeer = (Peer) ModuleFactory.createPeer(socket, this);
					neighborPeers.add(clientPeer);
					
					System.out.println("ADDING PEER FROM CLIENT: " + clientPeer);
					new Thread(clientPeer).start();
				}
		}
		
	}
	
	public int getNumberOfConnectedPeers(String peerID)
	{
		int numOfPeers = 0;
		
		HashMap<String, Configuration.PeerInfo> peers = configInstance.getPeerList();
		
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
	
	public synchronized void addNeighbors(Peer peer)
	{
		neighborPeers.add(peer);
		System.out.println("ADDING PEER FROM SEVER: " + peer);
	}
	
	public List<Peer> getNeighborsList()
	{
		return neighborPeers;
	}
        
        public Configuration getConfiguration()
        {
            return configInstance;
        }
	
	public Module getLogger()
	{
		return logInstance;
	}
	
	public BitFieldManager getBitFieldManager()
	{
		return bitFieldManager;
	}

}

