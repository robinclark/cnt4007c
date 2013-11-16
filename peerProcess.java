public class peerProcess {
	public static void main(String[] args)
	{
		//sfdf
		Module ctrl = ModuleFactory.createCtrlMod(args[0]);
		ModuleFactory fac = ModuleFactory.getFactory();
		//System.out.println("CTRLpeerProcess: " + ctrl);
		ctrl.execute();
	}
}

