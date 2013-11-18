import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Controller extends Module {
	
	private Configuration configInstance;
	private Logger logInstance;
	private String peerID;
	private Server serverInstance;
	private List<Peer> neighborPeers;
	private boolean isShuttingDown;
	private Controller ctrl;
    private OptimisticNeighborManager optimisticNeighborManager;
    private PreferredNeighborManager preferredNeighborManager;
	private HashMap<String, String> commonInfo;
	private int fileSize;
	private int pieceSize;
	private int numOfPieces;
	private FileHandler fileHandlerInstance;

	public Controller(String peerID)
	{
		this.peerID = peerID;
		
	}
	
	@Override
	public void initialConfiguration() {
			if(configInstance == null)
			{
				System.out.println("CONFIG INSTANCE START");
				configInstance = (Configuration) ModuleFactory.createConfigMod();
				commonInfo = configInstance.getCommonInfo();
				fileSize = Integer.parseInt(commonInfo.get("FileSize"));
				pieceSize = Integer.parseInt(commonInfo.get("PieceSize"));
				numOfPieces = (int) Math.ceil(fileSize/pieceSize);
				
			}
			
			if(logInstance == null)
			{
					logInstance = (Logger) ModuleFactory.createLogMod(peerID);
			}

			if(serverInstance == null)
			{
					serverInstance = (Server) ModuleFactory.createServerMod(peerID, (Controller) this);
			}
			
			if(neighborPeers == null)
			{
				neighborPeers = new ArrayList<Peer>();
			}


			if(fileHandlerInstance == null)
			{
				System.out.println("FILEHANDLER START");
				fileHandlerInstance = (FileHandler) ModuleFactory.createFileHandlerMod(this);				
			}

			
			isShuttingDown = false;

	}
	
	public void execute()
	{
			try {
				createServers();	
				createClients();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void setPeerID(String peerID)
	{
		this.peerID = peerID;
	}
	
	public String getPeerID()
	{
		return peerID;
	}
	
	public boolean isShuttingDown()
	{
		return isShuttingDown;
	}

	public void createServers()
	{
		new Thread(serverInstance).start();
	}
	
	public void createClients() throws UnknownHostException, IOException
	{
		HashMap<String, Configuration.PeerInfo> map = configInstance.getPeerList();

		Set<String> peerKeys = map.keySet();
		
		
		for(String peerKey :  peerKeys)
		{
		
				if(Integer.parseInt(peerID) > Integer.parseInt(peerKey))
				{
					Socket socket = new Socket(map.get(peerKey).getHostName(), map.get(peerKey).getPortNumber());
					Peer clientPeer = (Peer) ModuleFactory.createPeer(socket, this);
					neighborPeers.add(clientPeer);
					
					System.out.println("ADDING PEER FROM CLIENT: " + clientPeer + " " + peerKey);
					
					new Thread(clientPeer).start();
				}
		}
		
	}
	
	public int getNumberOfConnectedPeers(String peerID)
	{
		int numOfPeers = 0;
		
		HashMap<String, Configuration.PeerInfo> peers = configInstance.getPeerList();
		
		Set<String> peerKeys = peers.keySet();
		
		for(String peerKey : peerKeys)
		{
			if(Integer.parseInt(peerID) < Integer.parseInt(peerKey))
			{
				numOfPeers++;
			}
		}
		
		return numOfPeers;	
		
	}


	public byte[] setBits(boolean hasFile)
	{
		byte[] bits = new byte[numOfPieces];
		if(hasFile)
		{
			Arrays.fill(bits, (byte)1);
		}
		else
		{
			Arrays.fill(bits,(byte)0);
		}
		
	
		return bits;
		
	}
	
	public synchronized boolean compareBytesForInterested(byte[] bitFieldA, byte[] bitFieldB)
	{
		boolean flag = false;
		for(int i = 0; i < numOfPieces; i++)
		{
			if((byte)bitFieldA[i] != (byte)bitFieldB[i] && bitFieldA[i] == (byte)1)
			{
				flag = true;
				break;
				
			}
			
		}
		
		return flag;
	
	}

	public synchronized int getRandomInterestedPiece(byte[] bitFieldA, byte[] bitFieldB)
	{
		Random rdx = new Random();
		int requestedIndex;
		ArrayList<Integer> availablePieces = new ArrayList<Integer>();
		for(int i = 0; i < numOfPieces; i++)
		{
			if((byte)bitFieldA[i] != (byte)bitFieldB[i] && bitFieldA[i] == (byte)1)
			{
				availablePieces.add(i);
			}
		}
		
		requestedIndex = availablePieces.get(rdx.nextInt(availablePieces.size()));

		return requestedIndex;
		
	}
	
	public void setPeerBitfield(String id, byte[] bitfield)
	{
		fileHandlerInstance.setPeerBitfield(id, bitfield);
	}
	
	public boolean getInterested(String id)
	{
		return fileHandlerInstance.getInterested(id);
	}
	
	
	public  void addNeighbors(Peer peer)
	{
		neighborPeers.add(peer);
		System.out.println("ADDING PEER FROM SEVER: " + peer);
	}
	
	public List<Peer> getNeighborsList()
	{
		return neighborPeers;
	}
        
    public Configuration getConfiguration()
    {
        return configInstance;
    }
	
	public Module getLogger()
	{
		return logInstance;
	}
	
	public byte[] getBitfield(String id)
	{
		byte[] field = fileHandlerInstance.getBitfield(id);
		//printBitfield("CONTROLLER", field);
		return field;
	}
	
	public int getFileSize()
	{
		return fileSize;
	}
	
	public void printBitfield(String s, byte[] b)
	{
		System.out.println(s);
		for(int i = 0; i < b.length; i++)
		{
			System.out.print(b[i] + ", ");
		}
		System.out.println();
	}
	
	public int getNumOfPieces()
	{
		return numOfPieces;
	}
	
}

