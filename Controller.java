public class Controller()
{
		private Configuration configFile;
		//must be synchronized because 
		public synchronized Controller(int peerID)
		{
			configFile = new Configuration(peerID);
			

			
		}


		public void execute()
		{
			new Thread(new Server(configFile)).start();
		}

		
}
