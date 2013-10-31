public class PeerProcess {
	public static void main(String[] args)
	{
		ModuleFactory factory = new ModuleFactory();
		Module peerController = factory.createCtrlMod(args[0]);
		peerController.begin();
	}
}

