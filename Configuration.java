import java.io.*;
import java.util.*;

public class Configuration extends Module {

	private LinkedHashMap<String, Configuration.PeerInfo> peerList;

        private LinkedHashMap<String, String> commonInfo;
        //private Configuration.CommonInfo commonInfo;


    /**
     * @return the peerList
     */
    public LinkedHashMap<String, Configuration.PeerInfo> getPeerList() {
        return peerList;
    }

    /**
     * @return the commonInfo
     */

    public LinkedHashMap<String,String> getCommonInfo() {
       return commonInfo;
    }
        
	
	public class PeerInfo
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
	public void initialConfiguration() {

		String st;
		boolean hasFile;
		peerList = new LinkedHashMap<String, Configuration.PeerInfo>();
		Configuration.PeerInfo node;
			try {
				BufferedReader in = new BufferedReader(new FileReader(Constants.PEER_CFG_FILE));
		

					while((st = in.readLine()) != null)
					{
						String tokens[] = st.split(" ");
							
							node  = new Configuration.PeerInfo();
							node.setPeerID(Integer.parseInt(tokens[0]));
							node.setHostName(tokens[1]);
							node.setPortNumber(Integer.parseInt(tokens[2]));
				
							hasFile = (tokens[3] == "1")  ? true : false;
							node.setHasFile(hasFile);
					
							getPeerList().put(tokens[0], node);
						}
						
						
				in.close();
			}	catch(IOException e)
				{
					System.out.println("There was a problem opening the peer configuration file. Make sure the file exists");
				}
                        
                        //read in config info
                        
                        commonInfo = new LinkedHashMap<String, String>();
			try {
				BufferedReader in = new BufferedReader(new FileReader(Constants.COMMON_CFG_FILE));
		

					while((st = in.readLine()) != null)
					{
                                               System.out.println("ST: " + st);
						String tokens[] = st.split(" ");
                                                
                                                commonInfo.put(tokens[0], tokens[1]);
							
						}
						
						
				in.close();
			}	catch(IOException e)
				{
					System.out.println("There was a problem opening the common configuration file. Make sure the file exists");
				}
	}

}

