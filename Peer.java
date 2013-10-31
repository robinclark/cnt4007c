import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Peer extends Module implements Runnable{
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private String peerID;
	private Logger logInstance;
	private PeerController controller;
	private boolean isChockedByPeer;
	private int downloadDataSize;
	private int downloadTime;
	private Socket neighborPeer;
	
	public Peer(Socket socket, PeerController controller)
	{
			neighborPeer = socket;
			this.controller = controller;
                        initialConfiguration();
	}
	@Override
	public void initialConfiguration() 
	{
			
			if(logInstance == null)
			{
				logInstance = (Logger) controller.getLogger();
			}
			
			if(inputStream == null && outputStream  == null)
			{
				try {
					inputStream = (ObjectInputStream) neighborPeer.getInputStream();
					outputStream = (ObjectOutputStream) neighborPeer.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	}
	@Override
	public void run() {
	//send and recieve messages
		
	}
	
	private void sendHandShake()
	{
		
	}
	
	
	

}
