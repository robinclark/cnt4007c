public class ModuleFactory {
	public static Module createConfigMod()
	{
		Configuration config = new Configuration();
		config.intialConfiguration();
		
		return config;
		
	}

	public static Module createLogMod(String peerID) {
		
		Logger log = new Logger(peerID);
		log.intialConfiguration();
		
		return log;
	}
	
	public static Module createCtrlMod(String peerID)
	{
		Controller controller = new Controller();
		controller.setPeerID(peerID);
		controller.intialConfiguration();
		
		return controller;
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

