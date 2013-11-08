import java.net.Socket;

public class ModuleFactory {
	private static volatile Module ctrl;
	public static Module createConfigMod()
	{
		Configuration config = new Configuration();
		config.initialConfiguration();
		
		return config;
		
	}

	public static ModuleFactory getFactory()
	{
		ModuleFactory fac = new ModuleFactory();

		return fac;
	}

	public static Module createLogMod(String peerID) {
		
		Logger log = new Logger(peerID);
		log.initialConfiguration();
		
		return log;
	}

	public synchronized static Module createCtrlMod(String peerID)
	{
		ctrl = new Controller(peerID);
		ctrl.initialConfiguration();
		return ctrl;
	}

	
	public static Module createServerMod(String peerID, Controller controller)
	{
		Server server = new Server(peerID, controller);
		server.initialConfiguration();
		return server;
	}
	
	public synchronized static Module createPeer(Socket socket, Controller controller)
	{
		int count = 0;
		Peer peer = new Peer(socket, controller);
		peer.initialConfiguration();
		return peer;
	}

	public static Module createTopPeerMod()
	{
			return null;
	}

	public static Module createOptimisticMod()
	{
			return null;
	}
}

