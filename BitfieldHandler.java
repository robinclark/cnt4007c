import java.util.*;

public class BitfieldHandler extends Module{
	HashMap<String, byte[]> bitfields;
	FileHandler fileHandler;
	int numPieces;
	String peerID;
	
	BitfieldHandler(FileHandler fileHandler)
	{
		this.fileHandler = fileHandler;		
	}

	@Override
	public void initialConfiguration() {
		
		numPieces = fileHandler.getNumOfPieces();
		peerID = fileHandler.getPeerID();
		boolean hasFile = fileHandler.getPeerList().get(peerID).getHasFile();
		initBitfield(hasFile);
	}
	
	//init own bitfield
	public void initBitfield(boolean hasFile)
	{
		if(hasFile)
		{
			for(int i = 0; i < numPieces; i++)
			{
				bitfields.get(peerID)[i] = 1;
			}
		}
	}
	
	public void setPeerBitfield(String peerID, byte[] peerBitfield)
	{
		System.arraycopy(peerBitfield, 0, bitfields.get(peerID), 0, numPieces);
	}
	
	public void setPiece(int index, String peerID)
	{
		bitfields.get(peerID)[index] = 1;
	}
	
	//return indices that this peer doesn't have
	public ArrayList<Integer> getInterestedPieceArray()
	{
		ArrayList<Integer> interestedArray = new  ArrayList<Integer>();
		
		byte[] temp = new byte[numPieces];
		System.arraycopy(bitfields.get(peerID), 0, temp, 0, numPieces);
		
		for(int i = 0; i < numPieces; i++)
		{
			if(temp[i] == 0)
			{
				interestedArray.add(i);
			}
		}		
		return interestedArray;
	}
	
}
