public class peerProcess {
	public static void main(String[] args)
	{
		ModuleFactory factory = new ModuleFactory();
		Module ctrl = factory.createCtrlMod(args[0]);
		ctrl.execute();
	}
}

