import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Peer extends Module implements Runnable{
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private String peerID;
	private String neighborPeerID;
	private Logger logInstance;
	private Controller controller;
	private boolean isChockedByPeer;
	private int downloadDataSize;
	private int downloadTime;
	private Socket neighborPeer;
	private boolean isShuttingDown;
	private byte[] readBuffer;
	
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
		 
		try {
			 if(newMsg.getHeader() == Constants.HANDSHAKE_HEADER)
			 {
				neighborPeerID = newMsg.getPeerID();
				logInstance.writeLogger(logInstance.TCPConnectLog(neighborPeerID));
				logInstance.close(); //temp closing writer

				//sendBitFieldMessage();
			 }	
		   }catch(IOException e)
		    {
			e.printStackTrace();
		    }
		 
	}
		 
	public String getPeerID()
	{
		return peerID;
	}
	
	

}
