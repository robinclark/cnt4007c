import java.net.Socket;

public class ModuleFactory {
	public static Module createConfigMod()
	{
		Configuration config = new Configuration();
		config.initialConfiguration();
		
		return config;
		
	}

	public static Module createLogMod(String peerID) {
		
		Logger log = new Logger(peerID);
		log.initialConfiguration();
		
		return log;
	}
	
	public static Module createCtrlMod(String peerID)
	{
		PeerController controller = new PeerController(peerID);
		controller.initialConfiguration();
		
		return controller;
	}
	
	public static Module createServerMod(String peerID, PeerController controller)
	{
		Server server = new Server(peerID, controller);
		server.initialConfiguration();
		return server;
	}
	
	public static Module createPeer(Socket socket, PeerController controller)
	{
		Peer peer = new Peer(socket, controller);
		peer.initialConfiguration();
		return peer;
	}

	public static Module createTopPeerMod()
	{
			return null;
	}

	public static Module crateOptimisticMod()
	{
			return null;
	}
}

