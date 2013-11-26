import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Module implements Runnable
{
	private String peerID;
	private Controller controllerInstance;
	
	public Server(String peerID, Module ctrl)
	{
		controllerInstance = (Controller) ctrl;
		this.peerID = peerID;
	}

	@Override
	public void initialConfiguration() {		
	}

	@Override
	public void run() {
		
	HashMap<String,Configuration.PeerInfo> peers = controllerInstance.getConfiguration().getPeerList();
		
		int numOfPeers = controllerInstance.getNumberOfConnectedPeers(peerID);
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
			
				controllerInstance.addNeighbors(neighborPeer);
		
				//new Thread(neighborPeer).start();
				controllerInstance.addThread(neighborPeer);
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		
	}

}
