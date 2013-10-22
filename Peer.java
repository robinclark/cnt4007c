import java.io.*;
import java.net.*;
public class Peer implements Runnable {

		private int peerID;
		private String hostname;
		private int portNumber;
		private boolean hasFile;
		private Controller controller;
		private Socket peerSocket; //socket for peer
		private ObjectOutputStream dataOutputStream; //where data is going to...
		private ObjectInputStream dataInputStream; // where data is coming from...

		
		public Peer(int peerID, String hostname, int portNumber)
		{
			this.peerID = peerID;
			this.hostname = hostname;
			this.portNumber = portNumber;
		}
		public Peer()
		{
			
		}
		

		public void startProcess() throws IOException, ClassNotFoundException
		{
			/**
				Establishing connetions and data streams for each peer
			*/

		
			ServerSocket serverConnection = new ServerSocket(portNumber);
			peerSocket = serverConnection.accept();	
			dataOutputStream = new ObjectOutputStream(peerSocket.getOutputStream());
			dataInputStream = new ObjectInputStream(peerSocket.getInputStream());

			while(true)
			{
				String message = (String)dataInputStream.readObject();
				System.out.println("DATA: " + message);
			}
				
		}

		public void establishConnectionsAndStreams(Controller controller)
		{
			ServerSocket serverConnection = new ServerSocket(portNumber);
			peerSocket = serverConenection.accept();
			dataOutputStream = new ObjectOutputStream(peerSocket.getOutputStream());
			dataInputStream = new ObjectInputStream(peerSocket.getInputStream());

			this.controller = controller;
		}

		public void run() 
		{
				if(peerID != 0)
				{
					try
					{	
					 		sendTestMessage();
					}catch(IOException e)
					{
						e.printStackTrace();
					}
				}
		}

		public void sendTestMessage() throws IOException
		{
			String message = "HELLO THERE :)";
			dataOutputStream.writeUnshared(message);
			dataOutputStream.flush();
			dataOutputStream.reset(); //clears cache from stream
		}
		
		public int getPeerID()
		{
			return peerID;
		}
		
		public void setPeerID(int peerID)
		{
			this.peerID = peerID;
		}
		
		public String getHostName()
		{
			return hostname;
		}
		
		public void setHostName(String hostname)
		{
			this.hostname = hostname;
		}
		
		public int portNumber()
		{
			return portNumber;
		}
		
		public void setPortNumber(int portNumber)
		{
			this.portNumber = portNumber;
		}
		
		public boolean hasFile()
		{
			return hasFile;
		}
		
		public void setHasFile(boolean hasFile)
		{
			this.hasFile = hasFile;
		}
		
}
