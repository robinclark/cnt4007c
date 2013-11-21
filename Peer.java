import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;

public class Peer extends Module implements Runnable{
	private ObjectInputStream inputStream;
	private ArrayList<String> peers = new ArrayList<String>();
	private ObjectOutputStream outputStream;
	private String peerID;
	private String neighborPeerID;
	private Logger logInstance;
	private  Controller controller;
	private boolean isChokedByPeer;
	private int downloadDataSize;
	private int downloadTime;
	private Socket neighborPeer;
	private boolean isShuttingDown;
	private byte[] readBuffer;
	private boolean hasFile;
	private byte[] bitField;
	private boolean handshakeSent = false;
	private float downloadRate = 0.0f;
	private long startTime = 0;
	private int bytesDownloaded = 0; 
	private boolean isOptimisticMessage = false;
	
	public Peer(Socket socket, Controller controller)
	{
			neighborPeer = socket;
			this.controller = controller;	
	}
	
	@Override
	public void initialConfiguration() 
	{
		
			peerID = controller.getPeerID();

			if(peerID.equals("1001"))isChokedByPeer = true;
			
			isShuttingDown = controller.isShuttingDown();
			
			if(logInstance == null)
			{
				logInstance = (Logger) controller.getLogger();
			}
			
			if(inputStream == null && outputStream  == null)
			{
				try {
					outputStream = new ObjectOutputStream(neighborPeer.getOutputStream());
					inputStream = new ObjectInputStream(neighborPeer.getInputStream());
				
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}

			Configuration config = controller.getConfiguration();
			HashMap<String, Configuration.PeerInfo> peers = config.getPeerList();
		
			Configuration.PeerInfo peer = peers.get(peerID);
			hasFile = peer.getHasFile();
		
	}
	
	@Override
	public void run() {
		sendHandShake();

		while(!isShuttingDown)
		{

			try {
				Message message = (Message) inputStream.readObject();
				
				if(message.getUID() == Constants.HANDSHAKE_UID) /* differentiates betwen normal messages and handshake messages*/
				{
					handleHandShakeMessage(message);
				}
				else
				{
					if(message.getMsgType() == Constants.MSG_BITFIELD_TYPE)
					{
						handleBitFieldMsg(message);
					}

					else if(message.getMsgType() == Constants.MSG_INTERESTED_TYPE)	
					{
						handleInterestedMsg();
					}

					else if(message.getMsgType() == Constants.MSG_UNINTERESTED_TYPE)
					{
						handleUnInterestedMsg();
					}
					else if(message.getMsgType() == Constants.MSG_PIECE_TYPE)
					{
						handlePieceMsg(message);
					}
					else if(message.getMsgType() == Constants.MSG_REQUEST_TYPE)
					{
						handleRequestMsg(message);
					}
					else if(message.getMsgType() == Constants.MSG_HAVE_TYPE)
					{
						handleHaveMsg(message);
					}
					else if(message.getMsgType() == Constants.MSG_CHOKE_TYPE)
					{
						handleChokeMsg(message);
					}
					else if(message.getMsgType() == Constants.MSG_UNCHOKE_TYPE)
					{
						handleUnchokeMsg(message);
					}
				}				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e)
			{
				e.printStackTrace();
			}	
		}		
	}
	
	private void sendHandShake()
	{
		
		try {
			
			HandShakeMessage message = new HandShakeMessage();
			message.setPeerID(peerID);
			outputStream.writeUnshared(message);
			outputStream.flush();
			handshakeSent = true;

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void handleHandShakeMessage(Message msg)
	{
		 HandShakeMessage newMsg = (HandShakeMessage) msg; 
		try {
			 if(newMsg.getHeader() == Constants.HANDSHAKE_HEADER)
			 {
				 if(handshakeSent)
				 {
					neighborPeerID = newMsg.getPeerID();
					logInstance.writeLogger(logInstance.TCPConnectLog(neighborPeerID));
					sendBitFieldMsg();
				 }
				 else
				 {
					 sendHandShake();
				 }
			 }	
		   }catch(IOException e)
		    {
			e.printStackTrace();
		    }
		 
	}
	
	private void sendBitFieldMsg()
	{
		try 
		{
			
			BitFieldMessage builder = new BitFieldMessage();
			NormalMessageCreator creator = new NormalMessageCreator(builder);
	
			byte[] field = controller.getBitfield(peerID);

			printBitfield(peerID, field);
			
			creator.createNormalMessage(Constants.MSG_BITFIELD_TYPE, field);
			Message msg = builder.getMessage();
			
			byte[] payLd = ((BitFieldMessage) msg).getMsgPayLoad();
			printBitfield(peerID, field);
			
			
			outputStream.writeUnshared(msg);
			outputStream.flush();
		}catch(IOException e) {
			e.printStackTrace();		
		}

	}

	private void handleBitFieldMsg(Message msg)
	{
		controller.setPeerBitfield(neighborPeerID, ((BitFieldMessage) msg).getMsgPayLoad());
		printBitfield(neighborPeerID, controller.getBitfield(neighborPeerID));

			if(peerID.equals("1001")) controller.broadcastHaveMsg(0);
	
		if(controller.getInterested(neighborPeerID))
		{
			sendInterestedMsg();
		}
		else
		{
			sendUnInterestedMsg();
		}
	}

	private void sendUnInterestedMsg()
	{	
		try 
		{			
			UnInterestedMessage builder = new UnInterestedMessage();
			NormalMessageCreator creator = new NormalMessageCreator(builder);
			creator.createNormalMessage(Constants.MSG_UNINTERESTED_TYPE);
			Message msg = builder.getMessage();

			outputStream.writeUnshared(msg);
			outputStream.flush();
		}catch(IOException e) {
			e.printStackTrace();		
		}
	}
	
	private void handleUnInterestedMsg()
	{
		try {

			System.out.println("HANDLING UNINTERESTED");
			logInstance.writeLogger(logInstance.notInterestedMessage(neighborPeerID));
			controller.removeInterestedPeer(neighborPeerID);
		    }catch(IOException e)
		    {
			e.printStackTrace();
		    }

	}

	private void sendInterestedMsg()
	{
		try 
		{
			
			InterestedMessage builder = new InterestedMessage(); 
			NormalMessageCreator creator = new NormalMessageCreator(builder);
			creator.createNormalMessage(Constants.MSG_INTERESTED_TYPE);
			Message msg = builder.getMessage();

			outputStream.writeUnshared(msg);
			outputStream.flush();
		}catch(IOException e) {
			e.printStackTrace();		
		}

	}
	
	private void handleInterestedMsg()
	{
		try {

			System.out.println("HANDLING INTERESTED");
			logInstance.writeLogger(logInstance.interestedMessage(neighborPeerID));
			controller.addInterestedPeer(neighborPeerID);
		    }catch(IOException e)
	            {		
			e.printStackTrace();
	            }	
	}
	
	private void sendPieceMsg(int index)
	{
		try 
		{			
			PieceMessage builder = new PieceMessage();
			NormalMessageCreator creator = new NormalMessageCreator(builder);
			byte[] payload = controller.getPiece(index);
			creator.createNormalMessage(Constants.MSG_PIECE_TYPE, index, payload);
			Message msg = builder.getMessage();

			outputStream.writeUnshared(msg);
			outputStream.flush();
		}catch(IOException e) {
			e.printStackTrace();		
		}
	}

	private void handlePieceMsg(Message msg)
	{
		System.out.println("HANDLING PIECE");
		byte payload[] = ((PieceMessage) msg).getMsgPayLoad();
		controller.writePiece(((PieceMessage) msg).getPieceIndex(), payload);
		bytesDownloaded += payload.length;
	}

	private void sendRequestMsg(int index)
	{
		try 
		{
			if(!isChokedByPeer)
			{ 
			
				RequestMessage builder = new RequestMessage();
				NormalMessageCreator creator = new NormalMessageCreator(builder);
				creator.createNormalMessage(Constants.MSG_REQUEST_TYPE, index);
				Message msg = builder.getMessage();

				outputStream.writeUnshared(msg);
				outputStream.flush();
			}
		}catch(IOException e) {
			e.printStackTrace();		
		}
		
	}

	private void handleRequestMsg(Message msg)
	{
		//create & send piece message	
		/*if(controller.getPreferredNeighbors().indexOf(neighborPeerID) != -1)
		{
			int index = ((RequestMessage) msg).getPieceIndex();
			sendPieceMsg(index);
		}
		*/
	}
	
	private void broadcastHaveMsg(int index)
	{
		controller.broadcastHaveMsg(index);
	}
	
	public void sendHaveMsg(int index)
	{
		try 
		{			
			HaveMessage builder = new HaveMessage();
			NormalMessageCreator creator = new NormalMessageCreator(builder);
			creator.createNormalMessage(Constants.MSG_HAVE_TYPE, index);
			Message msg = builder.getMessage();

			outputStream.writeUnshared(msg);
			outputStream.flush();
			
		}catch(IOException e) {
			e.printStackTrace();		
		}		
	}
	
	private void handleHaveMsg(Message msg)
	{
		System.out.println("HANDLING HAVE");
		controller.setPiece(((HaveMessage) msg).getPieceIndex(),neighborPeerID);
		printBitfield(neighborPeerID, controller.getBitfield(neighborPeerID));
		if(controller.getInterested(neighborPeerID))
		{
			sendInterestedMsg();
		}
		else
		{
			sendUnInterestedMsg();
		}		
	}
	
	private void sendChokeMsg()
	{
		try 
		{			
			ChokeMessage builder = new ChokeMessage();
			NormalMessageCreator creator = new NormalMessageCreator(builder);
			creator.createNormalMessage(Constants.MSG_CHOKE_TYPE);
			Message msg = builder.getMessage();

			outputStream.writeUnshared(msg);
			outputStream.flush();
			
		}catch(IOException e) {
			e.printStackTrace();		
		}	
	}
	
	private void handleChokeMsg(Message msg)
	{		
		isChokedByPeer = true;

	}
	
/*most likely a bad idea to make unchokemessage public but this is for testing purposes until we can find a better solution*/
	public void sendUnchokeMsg(boolean isOptimisticMessage)
	{
		this.isOptimisticMessage = isOptimisticMessage;
		try 
		{	
					
			UnchokeMessage builder = new UnchokeMessage();			
			NormalMessageCreator creator = new NormalMessageCreator(builder);
						
			creator.createNormalMessage(Constants.MSG_UNCHOKE_TYPE);
		
			System.out.println("\nUNCHOKING: " + neighborPeerID);
			Message msg = builder.getMessage();

			outputStream.writeUnshared(msg);
			outputStream.flush();
			
		}catch(IOException e) {
			e.printStackTrace();		
		}	
	}
	
	private void handleUnchokeMsg(Message msg)
	{
		System.out.println("FINALLY UNCHOKED: " );
		startTimer();
		if(isOptimisticMessage){controller.setOptimisticPeerID(peerID);}
		isChokedByPeer = false;	
		controller.setNeighbor(this);	
		sendRequestMsg(controller.getInterestedIndex(neighborPeerID));
	}
			 
	public String getPeerID()
	{
		return peerID;
	}

	public String getNeighborPeerID()
	{
		return neighborPeerID;
	}

	public boolean isChokedByPeer()
	{
		return isChokedByPeer;
	}

	public void isChoked(boolean state)
	{
		isChokedByPeer = state;
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
	
	public float getDownloadRate()
	{
		long totalTime = System.currentTimeMillis() - startTime;
		downloadRate = bytesDownloaded/totalTime;
		return downloadRate;
	}
	
	public void startTimer()
	{
		startTime = System.currentTimeMillis();
	}

	public void restartData()
	{
		bytesDownloaded = 0;
	}
}
