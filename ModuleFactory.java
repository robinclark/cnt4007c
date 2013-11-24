import java.net.Socket;

public class ModuleFactory {
	private static Module ctrl;
	
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

	public static Module createCtrlMod(String peerID)
	{
		if(ctrl == null)
		{
			ctrl = new Controller(peerID);
			ctrl.initialConfiguration();
		}
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

	public static Module createFileHandlerMod(Controller controller)
	{
		//System.out.println("CREATE FILEHANDLER MOD");
		FileHandler fileHandler = new FileHandler(controller);
		fileHandler.initialConfiguration();
		return fileHandler;
	}
	
	public static Module createFileHandlerMod(String file, int fileSize, int pieceSize, String id, boolean has)
	{
		FileHandler fileHandler = new FileHandler(file, fileSize, pieceSize, id, has);
		fileHandler.initialConfiguration();
		return fileHandler;
	}
	
	public static Module createBitfieldHandlerMod(FileHandler fileHandler)
	{
		BitfieldHandler bitfieldHandler = new BitfieldHandler(fileHandler);
		return bitfieldHandler;
	}
	
	public static Module createBitfieldHandlerMod(FileHandler fileHandler, boolean has)
	{
		BitfieldHandler bitfieldHandler = new BitfieldHandler(fileHandler, has);
		return bitfieldHandler;
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

