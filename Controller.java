import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
	private List<String> preferredNeighbors;
	private List<String> interestedNeighbors;
	private String optimisticallyUnchokedNeighbor;
	private HashMap<String, Float> peerDownloadRates;
	private HashMap<String, Boolean> hasFileStatus;
	private ExecutorService pool;
	private ArrayList<Future<?>> futures;
	private ArrayList<Integer> requestedPieces;
	private ArrayList<String> peerKeyArray;
	
	public Controller(String peerID)
	{
		this.peerID = peerID;
		
	}
	
	@Override
	public void initialConfiguration() {
			if(configInstance == null)
			{
				configInstance = (Configuration) ModuleFactory.createConfigMod();
				peerList = configInstance.getPeerList();
				peerKeys = peerList.keySet();
				peerKeyArray = new ArrayList<String>();
				
				
				hasFileStatus = new HashMap<String, Boolean>();
				for(String peerKey: peerKeys)
				{
					hasFileStatus.put(peerKey, configInstance.getPeerList().get(peerKey).getHasFile());					
					peerKeyArray.add(peerKey);
				}
				
				peerKeyArray.remove(peerID);
				
				commonInfo = configInstance.getCommonInfo();
				fileName = "peer_" + peerID + "/" + commonInfo.get("FileName");
				System.out.println(fileName);
				fileSize = Integer.parseInt(commonInfo.get("FileSize"));
				pieceSize = Integer.parseInt(commonInfo.get("PieceSize"));
				numOfPieces = (int) Math.ceil(fileSize/pieceSize);
				
				pool = Executors.newFixedThreadPool(peerList.size());
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

			if(interestedNeighbors == null)
			{
				interestedNeighbors = new ArrayList<String>();
			}


			if(fileHandlerInstance == null)
			{
				fileHandlerInstance = (FileHandler) ModuleFactory.createFileHandlerMod(this);				
			}
			
			isShuttingDown = false;
			
			peerDownloadRates = new HashMap<String, Float>();
			preferredNeighborManager = new PreferredNeighborManager(this);
			
			futures = new ArrayList<Future<?>>();
			
			//new Thread(preferredNeighborManager).start();
			futures.add(pool.submit(preferredNeighborManager));
			
			requestedPieces = new ArrayList<Integer>();
	}
	
	public void addThread(Runnable r)
	{
		futures.add(pool.submit(r));
	}
	
	public void execute()
	{
			try {
				createServers();	
				createClients();
				
				//start preferred neighbor selection
				//select optimistic neighbor
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
		//new Thread(serverInstance).start();
		futures.add(pool.submit(serverInstance));
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
		
					//new Thread(clientPeer).start();
					futures.add(pool.submit(clientPeer));
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
	
	public synchronized void broadcastHaveMsg(int index)
	{
		for(int i = 0; i < neighborPeers.size(); i++)
		{
			neighborPeers.get(i).sendHaveMsg(index);
		}
	}

	public synchronized byte[] getPiece(int index)
	{
		return fileHandlerInstance.getPiece(index);
	}
	
	public synchronized void setPeerBitfield(String id, byte[] bitfield)
	{
		fileHandlerInstance.setPeerBitfield(id, bitfield);
	}
	
	public synchronized boolean getInterested(String id)
	{
		return fileHandlerInstance.getInterested(id);
	}
	
	public synchronized int  getInterestedIndex(String id)
	{
		ArrayList<Integer> interestedPieces = fileHandlerInstance.getInterestedPieceArray(id);
		if(!interestedPieces.isEmpty())
		{
			Random rdx = new Random();
			int index = interestedPieces.get(rdx.nextInt(interestedPieces.size()));
			/*if(requestedPieces.size() < interestedPieces.size())
			{
				while(requestedPieces.contains(index))					
				{
					index = interestedPieces.get(rdx.nextInt(interestedPieces.size()));
				}
			}*/
			return index;
		}
		else
		{
			return -1;
		}
	}
	
	public synchronized void setPiece(int index, String id)
	{
		fileHandlerInstance.setPiece(index, id);
	}
	
	public synchronized void addInterestedPeer(String id)
	{
		interestedNeighbors.add(id);
		System.out.println("*****ADDED INTERESTED");
		for(String s: interestedNeighbors)
		{
			System.out.println(s);
		}
	}
	
	public synchronized void removeInterestedPeer(String id)
	{
		try
		{
			interestedNeighbors.remove(id);
			System.out.println("*****REMOVED INTERESTED");
			for(String s: interestedNeighbors)
			{
				System.out.println(s);
			}
		 }
		catch(IndexOutOfBoundsException  e)
		{} //fail silently if peer does not exist
	}
	
	public synchronized List<String> getInterestedNeighbors()
	{
		return interestedNeighbors;
	}
	
	public synchronized void writePiece(int index, byte[] piece)
	{
		fileHandlerInstance.writePiece(index, piece);
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
	
	public synchronized byte[] getBitfield(String id)
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
		System.out.println(s + " ");
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
	

	public HashMap<String, Float> getPeerDownloadRates()
	{
		for(Peer p: neighborPeers)
		{
			if(p.getHandshakeReceived())
			{
				peerDownloadRates.put(p.getNeighborPeerID(), p.getDownloadRate());
				//System.out.println(p.getNeighborPeerID() + "handshakeReceived: " + p.getHandshakeReceived());
			}
		}
		return peerDownloadRates;
	}
       	
	public synchronized List<String> getPreferredNeighbors()
	{
		return preferredNeighbors;
	}
	
	public void setPreferredNeighbors(List<String> preferred)
	{
		preferredNeighbors = preferred;
		
		System.out.println("PreferredNeighbors");
    	for(String neighbor: preferredNeighbors)
    	{
    		System.out.println(neighbor);
    	}
		//send unchoke messages to peers
		unchokePreferredChokeOthers();
	}
	
	public void unchokePreferredChokeOthers()
	{
		for(String prefkey: preferredNeighbors)
		{
			for(Peer peer: neighborPeers)
			{
				if(peer.getHandshakeReceived())
				{
					if(peer.getNeighborPeerID().equals(prefkey))
					{
						System.out.println("KEYS EQUAL: " + prefkey + ", CHOKED: " + peer.getIsChokedByPeer());
						
						if(peer.getIsChokedByPeer())
						{
							System.out.println("UNCHOKING");
							peer.sendUnchokeMsg();
							
						}
					}
					else
					{
						System.out.println("KEYS UNEQUAL: " + prefkey + ", CHOKED" + peer.getIsChokedByPeer());
						if(!peer.getIsChokedByPeer())
						{
							System.out.println("CHOKING");
							peer.sendChokeMsg();
						}
					}
				}
			}
		}
	}
	
	public void setHasFile(String id)
	{
		hasFileStatus.put(id, true);
		
		for(String peerKey: peerKeys)
		{
			if(!hasFileStatus.get(peerKey))
			{
				System.out.println(peerKey + "STILL DO ESNT HAVE FILE");
				return;
			}
		}
		System.out.println("EVERYONE HAS FILE");
		for(String s: peerKeys)
		{
			byte[] b = getBitfield(s);
			printBitfield(s, b);
		}
		closeEverything();
	}
	
	public synchronized boolean getHasFile()
	{
		return hasFileStatus.get(peerID);
	}
	
	public void closeEverything()
	{
		System.out.println("SHUTTING DOWN");
		
		for(Future<?> f: futures)
		{
			System.out.println("B4 FUTURE DONE: " + f.isDone());
			f.cancel(true);
		}
		
		preferredNeighborManager.shutdown();
		
		List<Runnable> threads = pool.shutdownNow();
		System.out.println("NUMBER THREADS: " + threads.size());
		
		for(Future<?> f: futures)
		{
			System.out.println("AFTR FUTURE DONE: " + f.isDone());
		}
		
		try{
			 System.err.println("TERMINATING******************");
			if (!pool.awaitTermination(15, TimeUnit.SECONDS)) {
			       pool.shutdownNow(); // Cancel currently executing tasks
			       // Wait a while for tasks to respond to being cancelled
			       if (!pool.awaitTermination(15, TimeUnit.SECONDS))
			       {
			           System.err.println("Pool did not terminate");
			       }
			       else
			       {
			    	   System.err.println("POOL DID TERMINATE");
			       }
			}
			else
			{
				System.err.println("POOL DID TERMINATE");
			}
		}
		catch(InterruptedException e)
		{
			// (Re-)Cancel if current thread also interrupted
		     pool.shutdownNow();
		}

		for(Future<?> f: futures)
		{
			System.out.println("AFTER SHUTDOWN FUTURE DONE: " + f.isDone());
		}
		
			try{
				logInstance.close();
				fileHandlerInstance.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			System.exit(0);

	}
	
	public synchronized void addRequestedPiece(int index)
	{
		if(requestedPieces.contains(index))
		{
			System.out.println("DUPLICATE PIECES FOUND****************************8");
		}
		requestedPieces.add(index);
	}
	
	public synchronized void removeRequestedPiece(int index)
	{
		requestedPieces.remove(Integer.valueOf(index));
	}
	
	public ArrayList<String> getPeerKeyArray()
	{
		System.out.println("PEERKEYS");
		
		
		for(String s: peerKeyArray)
		{
			System.out.println(s);
		}
		return peerKeyArray;
	}
}

