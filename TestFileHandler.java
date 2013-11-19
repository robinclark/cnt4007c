//test file handler

public class TestFileHandler
{
	public static void main(String[] args)
	{
		FileHandler fileHandler0 = (FileHandler) ModuleFactory.createFileHandlerMod("peer_1001\test_a.txt", 180768, 32768);
		FileHandler fileHandler1 = (FileHandler) ModuleFactory.createFileHandlerMod("test_b.txt", 180768, 32768);

		System.out.println("size: "+ fileHandler0.getNumOfPieces());
		
		//copy from a to b
		for(int i = 0; i < fileHandler0.getNumOfPieces(); i++)
		{			
			byte[] a = fileHandler0.getPiece(i);
			fileHandler1.writePiece(i, a);
		}
	}
}
