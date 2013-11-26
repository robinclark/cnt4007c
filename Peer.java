import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Peer extends Module implements Runnable{
	private ObjectInputStream inputStream;
	private ArrayList<String> peers = new ArrayList<String>();
	private ObjectOutputStream outputStream;
	private String peerID;
	private String neighborPeerID;
	private Logger logInstance;
	private  Controller controller;
	private boolean isChokedByPeer = true;
	private int downloadDataSize;
	private int downloadTime;
	private Socket neighborPeer;
	private boolean isShuttingDown;
	private byte[] readBuffer;
	private boolean hasFile;
	private byte[] bitField;
	private boolean handshakeSent = false;
	private boolean handshakeReceived = false;
	private float downloadRate = 0.0f;
	private long startTime = 0;
	private int bytesDownloaded = 0; 

	//private Timer timer;

	private boolean isOptimisticMessage = false;

	
	public Peer(Socket socket, Controller controller)
	{
			neighborPeer = socket;
			this.controller = controller;	
			System.out.println(peerID + " CONTROLLER: " + controller);
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
			
			//timer = new Timer();
	}
	
	private class WaitForPiece extends TimerTask
	{
		int index = -1;
		
		public WaitForPiece(int index)
		{
			this.index = index;
		}
		public void run()
		{
			System.out.println("*****TIMEOUT***** " + index);
			
			
			byte b[] = controller.getBitfield(peerID);
			printBitfield("TIMER", b);
			if(b[index] == 0)
			{
				System.out.println("*****PIECE NOT SENT IN TIME***** " + index);
				//controller.removeRequestedPiece(index);
			}	
			//timer.cancel();
		}
	}
	
	@Override
	public void run() {
		

		

			try {
				sendHandShake();
				
				while(!isShuttingDown)
				{
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
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e)
			{
				e.printStackTrace();
			}
	}
	
	public void shutdown()
	{
		try
		{
			inputStream.close();
			outputStream.close();
		}
		catch (IOException e) {
			
            e.printStackTrace();
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
		try {
			 if(newMsg.getHeader() == Constants.HANDSHAKE_HEADER)
			 {
				 if(handshakeSent)
				 {
					neighborPeerID = newMsg.getPeerID();
					logInstance.writeLogger(logInstance.TCPConnectToLog(neighborPeerID));
					sendBitFieldMsg();
				 }
				 else
				 {
					 sendHandShake();
				 }
				 handshakeReceived = true;
				 System.out.println("HANDSHAKE HANDLED");
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

			//printBitfield(peerID, field);
			
			creator.createNormalMessage(Constants.MSG_BITFIELD_TYPE, field);
			Message msg = builder.getMessage();
			
			byte[] payLd = ((BitFieldMessage) msg).getMsgPayLoad();
			
			
			
			outputStream.writeUnshared(msg);
			outputStream.flush();
			System.out.println("BITFIELD SENT");
			printBitfield(peerID, field);
		}catch(IOException e) {
			e.printStackTrace();		
		}

	}

	private void handleBitFieldMsg(Message msg)
	{
		controller.setPeerBitfield(neighborPeerID, ((BitFieldMessage) msg).getMsgPayLoad());
	
		if(controller.getInterested(neighborPeerID))
		{
			sendInterestedMsg();
		}
		else
		{
			sendUnInterestedMsg();
		}
		System.out.println("BITFIELD HANDLED");
		printBitfield(neighborPeerID, controller.getBitfield(neighborPeerID));
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
		try {

			logInstance.writeLogger(logInstance.notInterestedMessage(neighborPeerID));
			controller.removeInterestedPeer(neighborPeerID);
			System.out.println("UNINTERESTED HANDLED");
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
			System.out.println("INTERESTED SENT");
		}catch(IOException e) {
			e.printStackTrace();		
		}

	}
	
	private void handleInterestedMsg()
	{
		try {


			logInstance.writeLogger(logInstance.interestedMessage(neighborPeerID));
			controller.addInterestedPeer(neighborPeerID);
			System.out.println("INTERESTED HANDLED");
		    }catch(IOException e)
	            {		
			e.printStackTrace();
	            }	
	}
	
	private void sendPieceMsg(int index)
	{
		try 
		{			
			System.out.println("CONSTRUCTING PIECE");
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
		//timer.cancel();//whenever recieve a piece cancel the timer
		System.out.println("HANDLING PIECE");
		byte payload[] = ((PieceMessage) msg).getMsgPayLoad();
		int index = ((PieceMessage) msg).getPieceIndex();
		broadcastHaveMsg(index);
		//controller.removeRequestedPiece(index);//remove piece fr requested pieces
		sendHaveMsg(index);//--send have b4 writing piece; is this a problem?
		controller.writePiece(index, payload);
		bytesDownloaded += payload.length;
		System.out.println("PIECE HANDLED");
		printBitfield("PIECE: ", controller.getBitfield(peerID));
		
		
		if(!controller.getHasFile())
		{
			sendRequestMsg(controller.getInterestedIndex(neighborPeerID));
		}		
		
		
	}

	private void sendRequestMsg(int index)
	{
		try 
		{
			if(!isChokedByPeer)
			{ 
				if(index >= 0)
				{
					//controller.addRequestedPiece(index);//add requested piece when send req
					
					RequestMessage builder = new RequestMessage();
					NormalMessageCreator creator = new NormalMessageCreator(builder);
					creator.createNormalMessage(Constants.MSG_REQUEST_TYPE, index);
					Message msg = builder.getMessage();
					System.out.println("REQUEST INDEX: " + index);
	
					outputStream.writeUnshared(msg);
					outputStream.flush();
					System.out.println("REQUEST SENT");
					
					//System.out.println("**********STARTING TIMER*******");
					//startPieceTime(index);//start timer
				}
			}
		}catch(IOException e) {
			e.printStackTrace();		
		}
		
	}

	private void handleRequestMsg(Message msg)
	{
		//create & send piece message	

		System.out.println(neighborPeerID + " FOUND: " + controller.getPreferredNeighbors().indexOf(neighborPeerID));

		if(controller.getPreferredNeighbors().indexOf(neighborPeerID) != -1)
		{
			int index = ((RequestMessage) msg).getPieceIndex();
			System.out.println("REQUESTING INDEX: " + index);
			sendPieceMsg(index);		
		}
		System.out.println("REQUEST HANDLED");

	}
	
	private void broadcastHaveMsg(int index)
	{
		controller.broadcastHaveMsg(index);
		System.out.println("BROADCAST HAVE");
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
			System.out.println("HAVE SENT");
		}catch(IOException e) {
			e.printStackTrace();		
		}		
	}
	
	private void handleHaveMsg(Message msg)
	{
		controller.setPiece(((HaveMessage) msg).getPieceIndex(),neighborPeerID);		
		if(controller.getInterested(neighborPeerID))
		{
			sendInterestedMsg();
		}
		else
		{
			sendUnInterestedMsg();
		}		

		System.out.println("HAVE HANDLED");
		printBitfield(neighborPeerID, controller.getBitfield(neighborPeerID));
	}
	
	public void sendChokeMsg()
	{
		try 
		{			
			System.out.println("TRYING TO CHOKE");
			ChokeMessage builder = new ChokeMessage();
			NormalMessageCreator creator = new NormalMessageCreator(builder);
			creator.createNormalMessage(Constants.MSG_CHOKE_TYPE);
			Message msg = builder.getMessage();

			outputStream.writeUnshared(msg);
			outputStream.flush();
			System.out.println("CHOKE SENT");
		}catch(IOException e) {
			e.printStackTrace();		
		}	
	}
	
	private void handleChokeMsg(Message msg)
	{		
		System.out.println("TRYING TO HANDLE CHOKE");
		isChokedByPeer = true;
		System.out.println("CHOKE HANDLED");

	}
	

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
			System.out.println("MESSAGE TYPE: " + msg.getMsgType());
			
			System.out.println("TRYING TO UNCHOKE");
			outputStream.writeUnshared(msg);
			outputStream.flush();
			System.out.println("UNCHOKE SENT");
		}catch(IOException e) {
			e.printStackTrace();		
		}	
	}
	
	private void handleUnchokeMsg(Message msg)
	{

		//System.out.println("TRYING TO HANDLE UNCHOKE");
		startTimer();
		isChokedByPeer = false;		
		if(!controller.getHasFile())
		{
			sendRequestMsg(controller.getInterestedIndex(neighborPeerID));
		}
		System.out.println("UNCHOKE HANDLED");

		System.out.println("FINALLY UNCHOKED: " );
		startTimer();
		if(isOptimisticMessage){controller.setOptimisticPeerID(neighborPeerID);}
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
		startTimer();
		return downloadRate;
	}
	
	public void startTimer()
	{
		startTime = System.currentTimeMillis();
		bytesDownloaded = 0;
	}
	
	public boolean getHandshakeSent()
	{
		return handshakeSent;
	}
	
	public boolean getHandshakeReceived()
	{
		return handshakeReceived;
	}
	
	public boolean getIsChokedByPeer()
	{
		return isChokedByPeer;
	}
	
	public void startPieceTime(int index)
	{
		//timer.schedule(new WaitForPiece(index), 1000);
	}
}
