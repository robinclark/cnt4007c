import java.util.*;

public class BitfieldHandler extends Module{
	HashMap<String, byte[]> bitfields;
	FileHandler fileHandler;
	
	BitfieldHandler(FileHandler fileHandler)
	{
		this.fileHandler = fileHandler;
	}

	@Override
	public void initialConfiguration() {
		
		
	}
	
	public void setPeerBitfield(byte[] peerBitfield)
	{
		
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
