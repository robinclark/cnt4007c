import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Module implements Runnable
{
	private Configuration configInstance;
	private String peerID;
	private Controller controllerInstance;
	
	public Server(String peerID, Module ctrl)
	{
		controllerInstance = (Controller) ctrl;
		this.peerID = peerID;
	}

	@Override
	public void initialConfiguration() {
		if(configInstance == null)
		{
			configInstance = (Configuration) ModuleFactory.createConfigMod();
		}
		
	}

	@Override
	public void run() {
		
		LinkedHashMap<String,Configuration.PeerInfo> peers = configInstance.getPeerList();
		
		int numOfPeers = controllerInstance.getNumberOfConnectedPeers(peerID);
		
		System.out.println("NUM: " + numOfPeers);
		Configuration.PeerInfo node = peers.get(peerID);
		
		try
		{
			ServerSocket peerServer = new ServerSocket(node.getPortNumber());
			
			for(int i = 0; i < numOfPeers; i++)
			{
			
				System.out.println("WAITING");

				Socket peer = peerServer.accept();
				
				System.out.println("CONNECTING: " + peer);
			
				Peer neighborPeer = (Peer) ModuleFactory.createPeer(peer, controllerInstance);

		
				//System.out.println("NP: " + neighborPeer);
				controllerInstance.addNeighbors(neighborPeer);
				new Thread(neighborPeer).start();
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		
	}

}
