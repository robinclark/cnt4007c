import java.io.*;
import java.util.*;

public class Configuration extends Module {

	private HashMap<String, peerInfo> peerList;
	
	public class peerInfo
	{
		private int peerID;
		private String hostName;
		private int portNumber;
		private boolean hasFile;
		
		protected void setPeerID(int peerID)
		{
			this.peerID = peerID;
		}

		public int getPeerID()
		{		
			return peerID;
		}

		protected void setHostName(String hostName)
		{
			this.hostName = hostName;
		}

		public String getHostName()
		{
			return hostName;
		}

		protected void setPortNumber(int portNumber)
		{
			this.portNumber = portNumber;
		}

		public int getPortNumber()
		{
			return portNumber;
		}

		protected void setHasFile(boolean hasFile)
		{
			this.hasFile = hasFile;
		}

		public boolean getHasFile()
		{
			return hasFile;
		}

	}
	
	@Override
	public void intialConfiguration() {

		String st;
		boolean hasFile;
		peerList = new HashMap<String,peerInfo>();
		peerInfo node = new peerInfo();
			try {
				BufferedReader in = new BufferedReader(new FileReader(Constants.CFG_FILE));
		

					while((st = in.readLine()) != null)
					{
						String tokens[] = st.split(" ");
							
							
							node.setPeerID(Integer.parseInt(tokens[0]));
							node.setHostName(tokens[1]);
							node.setPortNumber(Integer.parseInt(tokens[2]));
				
							hasFile = (tokens[3] == "1")  ? true : false;
							node.setHasFile(hasFile);
					
							peerList.put(tokens[0], node);
						}
						
						
				in.close();
			}	catch(IOException e)
				{
					System.out.println("There was a problem opening the configuration file. Make sure the file exists");
				}
		
	}
	
	public HashMap<String,peerInfo> getPeerListCollection()
	{
		return peerList;
	}

}

