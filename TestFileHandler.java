//test file handler

public class TestFileHandler
{
	public static void main(String[] args)
	{
		FileHandler fHandler = (FileHandler) ModuleFactory.createFileHandlerMod("peer_1001/test_a.txt", 180768, 32768);
		FileHandler fHandler2 = (FileHandler) ModuleFactory.createFileHandlerMod("peer_1002/test_b.txt", 180768, 32768);

		System.out.println("size: "+ fHandler.getNumOfPieces());
		
		//copy from a to b
		for(int i = 0; i < fHandler.getNumOfPieces(); i++)
		{			
			byte[] a = fHandler.getPiece(i);
			fHandler2.writePiece(i, a);
		}
	}
}
