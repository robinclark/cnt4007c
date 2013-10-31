import java.io.*;
import java.util.*;

public class Configuration extends Module {

	private HashMap<String, peerInfo> peerList;
	
	
	@Override
	public void intialConfiguration() {

		String st;
		boolean hasFile;
		peerList = new HashMap<String,peerInfo>();
		peerInfo node;
			try {
				BufferedReader in = new BufferedReader(new FileReader(Constants.CFG_FILE));
		

					while((st = in.readLine()) != null)
					{
						String tokens[] = st.split(" ");
							
						 	node = new peerInfo();
							node.setPeerID(tokens[0]);
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

