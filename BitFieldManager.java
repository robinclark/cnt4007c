
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Robin
 */
public class BitFieldManager {
	 private static volatile HashMap<String, byte[]> bitFields = new HashMap<String, byte[]>();
	 private static int[] a = new int[2];
	 private HashMap<String, String> commonInfo;
	 private int fileSize;
	 private int pieceSize;
	 private int numOfPieces;

	public BitFieldManager(Controller ctrl)
	{
		commonInfo = ctrl.getConfiguration().getCommonInfo();
		fileSize = Integer.parseInt(commonInfo.get("FileSize"));
		pieceSize = Integer.parseInt(commonInfo.get("PieceSize"));
		numOfPieces = (int) Math.ceil(fileSize/pieceSize);
	}

	/* clone() is unsafe it does a shallow copy unless you intend to do this.
	public void setMap(String peerID, boolean peerBitfield[])
	{
	bitFields.put(peerID, peerBitfield.clone());
	}
	*/  

	public void addPiece(String peerID, int index)
	{
		bitFields.get(peerID)[index] = (byte)1;
	}

	public byte[] getBitFields(String peerID)
	{
		return this.bitFields.get(peerID);
	}

	public HashMap<String,byte[]> getBitFields()
	{
		return this.bitFields;
	}

	
	public byte[] setBits(String peerID, boolean hasFile)
	{
		byte[] bits = new byte[numOfPieces];
		if(hasFile)
		{
			Arrays.fill(bits, (byte)1);
		}
		else
		{
			Arrays.fill(bits,(byte)0);
		}
		
		synchronized(this)
		{
			bitFields.put(peerID, bits);
			a[0] = Integer.parseInt(peerID);
			System.out.println("BITFIELDS: " + bitFields);
			System.out.println("A: " + a);
	
		}
			
		return bits;
		
	}
	
	public boolean compareBytesForInterested(byte[] bitFieldA, byte[] bitFieldB)
	{
		boolean flag = false;
		for(int i = 0; i < numOfPieces; i++)
		{
			if((byte)bitFieldA[i] != (byte)bitFieldB[i] && bitFieldA[i] == (byte)0)
			{
				flag = true;
				break;
				
			}
			
		}
		
		return flag;
	
	}

}	
