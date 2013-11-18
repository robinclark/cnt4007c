import java.util.ArrayList;
import java.util.HashMap;


public class BitfieldTester {
	
	
	public static void printBitfield(byte[] b)
	{
		for(int i = 0; i < b.length; i++)
		{
			System.out.print(b[i] + ", ");
		}
		System.out.println();
	}
	
	public static void main(String[] args)
	{
		/*int numPieces = 6;
		BitfieldHandler bHandler = new BitfieldHandler(numPieces, "1001", true);		
		
		printBitfield(bHandler.getBitfield("1001"));
		
		byte[] bfield = {0,0,0,0,0,0};
		bHandler.setPeerBitfield("1002", bfield);
		printBitfield(bHandler.getBitfield("1002"));
		
		bHandler.setPiece(3, "1002");
		printBitfield(bHandler.getBitfield("1002"));
		
		//byte[] bfield2 = bHandler.getBitfield("1002");
		
		ArrayList<Integer> a = bHandler.getInterestedPieceArray("1002");
		System.out.println(a);
		
		boolean b = bHandler.getInterested("1002");
		System.out.println(b);
		
		HashMap<String, byte[]> testfld = bHandler.getBitfields();*/
		
		FileHandler fHandler = new FileHandler("test_a.txt", 180768, 32768);
		//fHandler.getBitfield();
	}
}
