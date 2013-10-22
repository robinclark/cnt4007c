import java.io.*;
import java.util.*;
public class Configuration
{
	private peerInfo peerConfiguration;

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
	public Configuration(int peerID)
	{
	
		String st;
		boolean hasFile;
		peerConfiguration = new peerInfo();
		
			try {
				BufferedReader in = new BufferedReader(new FileReader(Constants.CFG_FILE));
		

					while((st = in.readLine()) != null)
					{
						String tokens[] = st.split(" ");
				
						if(peerID == Integer.parseInt(tokens[0]))
						{
							
							node.setPeerID(Integer.parseInt(tokens[0]));
							node.setHostName(tokens[1]);
							node.setPortNumber(Integer.parseInt(tokens[2]));
				
							hasFile = (tokens[3] == "1")  ? true : false;
							node.setHasFile(hasFile);
							peerConfiguration = node;
						}
						
						
			
			
					}

				in.close();
			}	catch(IOException e)
				{
					System.out.println("There was a problem opening the configuration file. Make sure the file exists");
				}

	}

	public HashMap<Integer, peerInfo> getPeerCollection()
	{
			return peerCollection;
	}
	
}
