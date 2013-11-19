import java.io.*;
import java.util.*;
public class Test {

		public static void main(String[] args) throws IOException, ClassNotFoundException
		{
			
			String st;
			boolean hasFile;
			Vector <Peer> peerVectors = new Vector<Peer>();
			BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
			Peer handler;
			HashMap<Integer, Peer> peerCollection = new HashMap<Integer, Peer>();
			Controller controller = new Controller();
			while((st = in.readLine()) != null)
			{
				String tokens[] = st.split(" ");
				handler = new Peer();
				handler.setPeerID(Integer.parseInt(tokens[0]));
				handler.setHostName(tokens[1]);
				handler.setPortNumber(Integer.parseInt(tokens[2]));
				
				hasFile = (tokens[3] == "1")  ? true : false;
				handler.setHasFile(hasFile);
				
				peerCollection.put(Integer.parseInt(tokens[0]), handler);	
				
				
			}

			in.close();
			
			System.out.println(peerCollection.size());
			Set<Integer> peerKeys = peerCollection.keySet();
			String path = System.getProperty("user.dir"); //CONSTANTS

			for(Integer key : peerKeys)
			{
				handler = peerCollection.get(key);

				System.out.println("Start remote peer " + handler.getPeerID() + " at " + handler.getHostName());
				Runtime.getRuntime().exec("ssh " + handler.getHostName() + " cd " + path + "; java peerProcess " + handler.getPeerID());
			
				peerVectors.add(handler);		
			}

				System.out.println("Stating all remove peers has done.");

			for(int i = 0; i < peerVectors.size(); i++)
			{
				Peer peer = peerVectors.get(i);
				peer.establishConnectionandStreams(controller);
				peer.startProcess();
			}
		}
}
