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
	private boolean isChockedByPeer;
	private int downloadDataSize;
	private int downloadTime;
	private Socket neighborPeer;
	private boolean isShuttingDown;
	private byte[] readBuffer;
	private boolean hasFile;
	private byte[] bitField;
	
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

			

				bitField = controller.setBits(peerID, hasFile);
			

			

					
			
	}
	@Override
	public void run() {
		sendHandShake();
		while(!isShuttingDown)
		{

			try {
				Message message = (Message) inputStream.readObject();
				
				if(message.getUID() == Constants.HANDSHAKE_UID)
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
				neighborPeerID = newMsg.getPeerID();
				logInstance.writeLogger(logInstance.TCPConnectLog(neighborPeerID));
				sendBitFieldMsg();
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
			creator.createNormalMessage(Constants.MSG_BITFIELD_TYPE, bitField);
			Message msg = builder.getMessage();

			outputStream.writeUnshared(msg);
			outputStream.flush();
		}catch(IOException e) {
			e.printStackTrace();		
		}

	}

	private void handleBitFieldMsg(Message msg)
	{
		
		//try {
			BitFieldMessage newMsg = (BitFieldMessage) msg;
			
			boolean isInterested = controller.compareBytesForInterested(bitField, newMsg.getMsgPayLoad());			
			if(isInterested)
			{
				System.out.println("Interested");
				logInstance.interestedMessage(neighborPeerID);
				//logInstance.close(); //temp closing writer
			}
		//}catch(IOException e)
		//{
		//	e.printStackTrace();
		//}
		
	}
		 
	public String getPeerID()
	{
		return peerID;
	}
	
	

}
