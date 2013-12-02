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
	public void initialConfiguration() {
		
			if(loggerFile == null)
			{
				loggerFile = new File(Constants.PDIR + fileName);
				try 
				{
					if(!loggerFile.exists())
					{
						loggerFile.createNewFile();
					}
			
					fw = new FileWriter(loggerFile.getName(),true);
					bw = new BufferedWriter(fw);
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			
			}
	
	public String TCPConnectToLog(String destPeerID)
	{
		return "[" + Time + "]: Peer [" + peer_ID + "]  makes a connection to Peer [" + destPeerID + "].\n";
	}
	
	public String TCPConnectFromLog(String destPeerID)
	{
		return "[" + Time + "]: Peer [" + peer_ID + "]  makes a connection to Peer [" + destPeerID + "].\n";
	}
	
	public String changeOfPeers(List<String> neighborList)
	{
		String list = formatPeerList(neighborList);
		return "[" + Time + "]: Peer [" + peer_ID + "] has the prefeered neighbors [" + list + "].\n"; 
	}
	
	public String changeOfOptimistic(String optimisticID)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] has the optimistically-unchoked neighbor [" + optimisticID + "].\n";
 	}
	
	public String unchoking(String destPeerID)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] is unchoked by [ " + destPeerID + "].\n";
	}
	
	public String choking(String destPeerID)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] is choked by [ " + destPeerID + "].\n";
	}
	
	public String haveMessage(String destPeerID, int pieceIndx)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] received a 'have' message from [" + destPeerID +"] for the piece [" + pieceIndx + "].\n"; 
	}
	
	public String interestedMessage(String destPeerID)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] recieved an 'interested' message from [ " + destPeerID + "].\n";
		
	}
	
	public String notInterestedMessage(String destPeerID)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] recieved an ' not interested' message from [ " + destPeerID + "].\n";
	}
	
	public String downloadPiece(String destPeerID, int index, int numPieces)
	{
		return "[" + Time + "]: Peer [ " + peer_ID + "] has downloaded the piece " + index + " from [ " + destPeerID + "]. Now the number of pieces it has is " + numPieces + "\n";
	}
	
	public String completionMessage()
	{
		return "[" + Time + "]: Peer [ " + peer_ID + " ] has downloaded the complete file.\n"; 
 	}
	
	public String testMessage()
	{
		return "Testing Peer [ " + peer_ID + "].\n";
	}
	
	public void writeLogger(String msg) throws IOException
	{
		bw.write(msg);
	}
	private String formatPeerList(List<String> list)
	{
		Iterator<String> iter = list.iterator();
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

