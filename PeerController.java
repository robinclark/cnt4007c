import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class PeerController extends Module {
	
	private Configuration configInstance;
	private Logger logInstance;
	private String peerID;
	private Server serverInstance;
	private List<Peer> peerConnections;
        private ServerSocket serverSocket;
        private ExecutorService threadPool;

	public PeerController(String peerID)
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
					serverInstance = (Server) ModuleFactory.createServerMod(peerID, (PeerController) this);
			}
			
			if(peerConnections == null)
			{
				peerConnections = new ArrayList<Peer>();
			}
                         
                     //init threadpool w size equal to # peer connections
                     int numThreads = configInstance.getPeerList().size()-1;
                     threadPool = Executors.newFixedThreadPool(numThreads);
                        
                      
	}
	
	public void begin()
	{     
            addServerSockets();
            addConnectionsToPreviousPeers();            
	}
        
        public void addServerSockets()
        {
            //port num for this peer
            int portNumber = configInstance.getPeerList().get(peerID).getPortNumber();
            
            //create socket for every peer that will make connection to this peer(#peers after)
            int numConnections = 0;
            Set<String> peerKeySet = configInstance.getPeerList().keySet();
            for(String pID: peerKeySet)
            {
                if(Integer.parseInt(pID) > Integer.parseInt(peerID))
                {
                    numConnections++;
                }
            }           
            
            //add connections to peerlist
            try
            {
                serverSocket = new ServerSocket(portNumber);
                for(int i =0; i < numConnections; i++)
                {
                    Peer peer = new Peer(serverSocket.accept(), this);
                    peerConnections.add(peer);
                    threadPool.execute(peer);
                }
            }
            catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
        
        //connect to peers that come before in peerconfig
        
        public void addConnectionsToPreviousPeers()
        {
            int numPreviousPeers = 0;
            Set<String> peerKeySet = configInstance.getPeerList().keySet();
            for(String pID: peerKeySet)
            {
                if(Integer.parseInt(pID) < Integer.parseInt(peerID))
                {
                    String hostName = configInstance.getPeerList().get(pID).getHostName();
                    int portNumber = configInstance.getPeerList().get(pID).getPortNumber();
                    
                    try
                    {
                        Socket socket = new Socket(hostName, portNumber);
                        Peer peer = new Peer(socket, this);
                        peerConnections.add(peer);
                        threadPool.execute(peer);
                    }
                    catch(IOException e)
                    {
                        System.out.println(e.getMessage());
                    }
                    
                }
            }   
           
        }
        
  
	
	public void setPeerID(String peerID)
	{
		this.peerID = peerID;
	}

        //create server threads
	public void createServers()
	{
		
	}
        
        public Configuration getConfiguration()
        {
            return configInstance;
        }
	
	public Module getLogger()
	{
		return logInstance;
	}

}

