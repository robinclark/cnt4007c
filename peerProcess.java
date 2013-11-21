public class peerProcess {
	public static void main(String[] args)
	{
		Module ctrl = ModuleFactory.createCtrlMod(args[0]);
		ctrl.execute();
	}
}

