import java.util.*;

public class BitfieldHandler extends Module{
	HashMap<String, byte[]> bitfields;
	FileHandler fileHandler;
	int numPieces;
	String peerID;
	
	BitfieldHandler(FileHandler fileHandler)
	{
		this.fileHandler = fileHandler;
		numPieces = fileHandler.getNumOfPieces();
		peerID = fileHandler.getPeerID();
		boolean hasfile = fileHandler.getPeerList().get(peerID).getHasFile();
		initBitfield();
	}

	@Override
	public void initialConfiguration() {
		
		
	}
	
	//init own bitfield
	public void initBitfield(boolean hasFile)
	{
		if(hasFile)
		{
			for(int i = 0; i < numPieces; i++)
			{
				
			}
		}
	}
	
	public void setPeerBitfield(byte[] peerBitfield)
	{
		fileHandler.get
	}
	
	public void setPiece(int index, String peerID)
	{
		
	}
	
	public ArrayList<Integer> getInterestedPieceArray(String peerID)
	{
		ArrayList<Integer> interestedArray = new  ArrayList<Integer>();
		
		return interestedArray;
	}
	
}
