import java.util.*;

public class BitfieldHandler extends Module{
	private HashMap<String, byte[]> bitfields;
	private FileHandler fileHandler;
	private int numPieces;
	private String peerID;
	private boolean hasFile;
	private byte[] completeFile;
	
	BitfieldHandler(FileHandler fileHandler)
	{
		bitfields = new HashMap<String, byte[]>();
		this.fileHandler = fileHandler;		
		numPieces = fileHandler.getNumOfPieces();
		peerID = fileHandler.getPeerID();
		//System.out.println("PEERID BITFIELD: " + peerID);
		boolean hasFile = fileHandler.getPeerList().get(peerID).getHasFile();
		initBitfield(hasFile);
	}
	
	BitfieldHandler(int numPieces, String peerID, boolean hasFile)
	{
		bitfields = new HashMap<String, byte[]>();
		this.numPieces = numPieces;
		this.peerID = peerID;
		this.hasFile = hasFile;
		initBitfield(hasFile);
	}

	@Override
	public void initialConfiguration() {
		
	}
	
	//init own bitfield
	public void initBitfield(boolean hasFile)
	{
		byte[] b = new byte[numPieces];
		completeFile = new byte[numPieces];
		
		bitfields.put(peerID, b);
		//System.out.println("BIT: " + peerID);
		if(hasFile)
		{
			for(int i = 0; i < numPieces; i++)
			{
				bitfields.get(peerID)[i] = 1;
				completeFile[i] = 1;
			}
		}
		else
		{
			for(int i = 0; i < numPieces; i++)
			{
				bitfields.get(peerID)[i] = 0;
				completeFile[i] = 1;
			}
		}
	}
	
	public void setPeerBitfield(String peerID, byte[] peerBitfield)
	{
		byte[] temp = new byte[numPieces];
		bitfields.put(peerID, temp);
		//System.out.println("SETTING BITFIELD: " + peerID);
		System.arraycopy(peerBitfield, 0, bitfields.get(peerID), 0, numPieces);
	}
	
	public void setPiece(int index, String peerID)
	{
		
		//System.out.println("BIT: " + bitfields.get("1001") + " index: " + peerID);
		bitfields.get(peerID)[index] = 1;
	}
	
	public byte[] getBitfield(String peerID)
	{
		//System.out.println("IN BITFIELDHANDLER");
		byte[] field = bitfields.get(peerID);
		
		//printBitfield("BITFIELD HANDLER", field);
		return field;
	}
	
	//return indices that this peer doesn't have
	public ArrayList<Integer> getInterestedPieceArray(String id)
	{
		ArrayList<Integer> interestedPieceArray = new  ArrayList<Integer>();
		
		byte[] bPeer = bitfields.get(peerID);
		byte[] bNeighbor = bitfields.get(id);
		
		for(int i = 0; i < numPieces; i++)
		{
			if(bPeer[i] == 0 && bNeighbor[i] == 1)
			{
				interestedPieceArray.add(i);
			}
		}	
		return interestedPieceArray;
	}
	
	public boolean getInterested(String id)
	{
		byte[] bPeer = bitfields.get(peerID);
		byte[] bNeighbor = bitfields.get(id);
		
		for(int i = 0; i < numPieces; i++)
		{
			if(bPeer[i] == 0 && bNeighbor[i] == 1)
			{
				return true;
			}
		}
		return false;
	}
	
	public HashMap<String, byte[]> getBitfields()
	{
		return bitfields;
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
	
	public boolean hasCompleteFile(String id)
	{
			byte[] b = bitfields.get(id);
			return Arrays.equals(b, completeFile);
	}
	
}
