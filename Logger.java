import java.util.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Logger extends Module {

	private String peer_ID;
	private File loggerFile;
	private String fileName;
	private String Time;
	private Calendar cal;
	private FileWriter fw;
	private BufferedWriter bw;
	
	public Logger(String peerID)
	{
		this.peer_ID = peerID;
		this.fileName = "log_peer_" + peerID + ".log";
		DateFormat date = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy");
		cal = Calendar.getInstance();
		Time = date.format(cal.getTime());
	}
	
	@Override
	public void intialConfiguration() {
		
			if(loggerFile == null)
			{
				loggerFile = new File(Constants.PDIR + fileName);
				if(!loggerFile.exists())
				{
					try {
						loggerFile.createNewFile();
						fw = new FileWriter(loggerFile.getAbsoluteFile());
						bw = new BufferedWriter(fw);
					} catch (IOException e) {
							e.printStackTrace();
					}
				}
			
			}

	}
	
	public String TCPConnectLog(int destPeerID)
	{
		return "[" + Time + "]: Peer [" + peer_ID + "]  makes a connection to Peer [" + destPeerID + "].";
	}
	
	public String changeOfPeers(List<Integer> neighborList)
	{
		String list = formatPeerList(neighborList);
		return "[" + Time + "]: Peer [" + peer_ID + "] has the prefeered neighbors [" + list + "]"; 
	}
	
	public String changeOfOptimistic(int optimisticID)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] has the optimistically-unchoked neighbor [" + optimisticID + "].";
 	}
	
	public String unchoking(int destPeerID)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] is unchocked by [ " + destPeerID + "].";
	}
	
	public String chocking(int destPeerID)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] is chocked by [ " + destPeerID + "].";
	}
	
	public String haveMessage(int destPeerID, int pieceIndx)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] received a 'have' message from [" + destPeerID +"] for the piece [" + pieceIndx + "]."; 
	}
	
	public String interestedMessage(int destPeerID)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] recieved an 'interested' message from [ " + destPeerID + "].";
		
	}
	
	public String notInterestedMessage(int destPeerID)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] recieved an ' not interested' message from [ " + destPeerID + "].";
	}
	
	public String completionMessage()
	{
		return "[" + Time + "]: Peer [ " + peer_ID + " ] has downloaded the complete file."; 
 	}
	
	public String testMessage()
	{
		return "Testing Peer [ " + peer_ID + "].";
	}
	
	public void writeLogger(String msg) throws IOException
	{
		bw.write(msg);
	}
	private String formatPeerList(List<Integer> list)
	{
		Iterator<Integer> iter = list.iterator();
		StringBuilder peerList = new StringBuilder();
		int extracommaIndex;
		while(iter.hasNext())
		{
			peerList.append(iter.next() + ",");
		}
		
		extracommaIndex = peerList.lastIndexOf(",");
		return peerList.substring(0,extracommaIndex);
	}
	
	public void close() throws IOException
	{
		bw.close();
	}

}

