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
	
	public Peer(Socket socket, Controller controller)
	{
			neighborPeer = socket;
			this.controller = controller;	
	}
	
	@Override
	public void initialConfiguration() 
	{
		
			peerID = controller.getPeerID();
			
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			Configuration config = controller.getConfiguration();
			HashMap<String, Configuration.PeerInfo> peers = config.getPeerList();
		
			Configuration.PeerInfo peer = peers.get(peerID);
			hasFile = peer.getHasFile();

			System.out.println("hasFile: " + hasFile);

			bitField = controller.setBits(hasFile);			
	}
	
	@Override
	public void run() {
		sendHandShake();
		while(!isShuttingDown)
		{

			try {
				Message message = (Message) inputStream.readObject();
				
				if(message.getUID() == Constants.HANDSHAKE_UID) //why isnt this handled like other messages?
				{
					handleHandShakeMessage(message);
				}
				else
				{
					System.out.println("MSGTYPE: " + message.getMsgType());
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
				// TODO Auto-generated catch block
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
			System.out.println("HANDSHAKE SENT");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void handleHandShakeMessage(Message msg)
	{
		 HandShakeMessage newMsg = (HandShakeMessage) msg;
		 System.out.println("CTRL: " + controller);
		 System.out.println("peers: " + controller.getNeighborsList());
	
		 
		try {
			 if(newMsg.getHeader() == Constants.HANDSHAKE_HEADER)
			 {
				 if(handshakeSent)
				 {
					neighborPeerID = newMsg.getPeerID();
					logInstance.writeLogger(logInstance.TCPConnectLog(neighborPeerID));
					System.out.println("SENDING BITFIELD");
					sendBitFieldMsg();
				 }
				 else
				 {
					 sendHandShake();
				 }
			 }	
			 
			 System.out.println("HANDSHAKE HANDLED");
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
			byte[] field = new byte[controller.getFileSize()];
			
			field = controller.getBitfield(peerID);
			//System.arraycopy(controller.getBitfield(peerID),0,field,0,controller.getNumOfPieces());
			System.out.println("BUILDING BITFIELD MESSAGE");
			printBitfield(peerID, field);
			
			creator.createNormalMessage(Constants.MSG_BITFIELD_TYPE, field);
			Message msg = builder.getMessage();
			
			byte[] payLd = ((BitFieldMessage) msg).getMsgPayLoad();
			printBitfield(peerID, field);
			System.out.println("message type: " + msg.getMsgType());
			
			outputStream.writeUnshared(msg);
			outputStream.flush();
			
			System.out.println("BITFIELD SENT");
		}catch(IOException e) {
			e.printStackTrace();		
		}

	}

	private void handleBitFieldMsg(Message msg)
	{
		controller.setPeerBitfield(neighborPeerID, ((BitFieldMessage) msg).getMsgPayLoad());
		printBitfield(neighborPeerID, controller.getBitfield(neighborPeerID));
		if(controller.getInterested(neighborPeerID))
		{
			sendInterestedMsg();
		}
		else
		{
			sendUnInterestedMsg();
		}
		System.out.println("BITFIELD HANDLED");
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
			System.out.println("UNINTERESTED SENT");
		}catch(IOException e) {
			e.printStackTrace();		
		}
	}
	
	private void handleUnInterestedMsg()
	{
		System.out.println("HANDLING UNINTERESTED");
		logInstance.notInterestedMessage(neighborPeerID);
		controller.removeInterestedPeer(neighborPeerID);
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
			System.out.println("INTERESTED SENT");
		}catch(IOException e) {
			e.printStackTrace();		
		}

	}
	
	private void handleInterestedMsg()
	{
		System.out.println("HANDLING INTERESTED");
		logInstance.interestedMessage(neighborPeerID);
		controller.addInterestedPeer(neighborPeerID);
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
			System.out.println("PIECE SENT");
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
		if(controller.getPreferredNeighbors().indexOf(neighborPeerID) != -1)
		{
			int index = ((RequestMessage) msg).getPieceIndex();
			sendPieceMsg(index);
		}
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
	
	public void sendChokeMsg()
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
	
	public void sendUnchokeMsg()
	{
		try 
		{			
			UnchokeMessage builder = new UnchokeMessage();
			NormalMessageCreator creator = new NormalMessageCreator(builder);
			creator.createNormalMessage(Constants.MSG_UNCHOKE_TYPE);
			Message msg = builder.getMessage();

			outputStream.writeUnshared(msg);
			outputStream.flush();
			
		}catch(IOException e) {
			e.printStackTrace();		
		}	
	}
	
	private void handleUnchokeMsg(Message msg)
	{
		startTimer();
		isChokedByPeer = false;		
		sendRequestMsg(controller.getInterestedIndex(neighborPeerID));
	}
			 
	public String getPeerID()
	{
		return peerID;
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
		startTimer();
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
	
	public String getNeighborPeerID()
	{
		return neighborPeerID;
	}
	
	public boolean getHandshakeSent()
	{
		return handshakeSent;
	}
}
