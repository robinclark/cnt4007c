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
	private String fileName;
	private int fileSize;
	private int pieceSize;
	private int numOfPieces;
	private FileHandler fileHandlerInstance;
	private HashMap<String, Configuration.PeerInfo> peerList;
	private Set<String> peerKeys;
	
	public Controller(String peerID)
	{
		this.peerID = peerID;
		
	}
	
	@Override
	public void initialConfiguration() {
			if(configInstance == null)
			{
				//System.out.println("CONFIG INSTANCE START");
				configInstance = (Configuration) ModuleFactory.createConfigMod();
				 peerList = configInstance.getPeerList();
				 peerKeys = peerList.keySet();
				commonInfo = configInstance.getCommonInfo();
				fileName = "peer_" + peerID + "/" + commonInfo.get("FileName");
				System.out.println(fileName);
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
				//System.out.println("FILEHANDLER START");
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
		for(String peerKey :  peerKeys)
		{
		
				if(Integer.parseInt(peerID) > Integer.parseInt(peerKey))
				{
					Socket socket = new Socket(peerList.get(peerKey).getHostName(), peerList.get(peerKey).getPortNumber());
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
		
		peerKeys = peerList.keySet();
		
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
	
	public boolean allPeersHaveFile()
	{
		for(String peerKey: peerKeys)
		{
			if(!fileHandlerInstance.hasCompleteFile(peerKey))
			{
				return false;
			}
		}
		return true;
	}
	
	public void broadcastHaveMsg(int index)
	{
		for(int i = 0; i < neighborPeers.size(); i++)
		{
			neighborPeers.get(i).sendHaveMsg(index);
		}
	}

	public byte[] getPiece(int index)
	{
		return fileHandlerInstance.getPiece(index);
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
	
	public String getFileName()
	{
		return fileName;
	}
	
	public int getPieceSize()
	{
		return pieceSize;
	}
}

