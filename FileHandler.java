//handle file segments
import java.util.*;
import java.lang.Exception.*;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileHandler extends Module
{
	private HashMap<String, String> commonInfo;
	private HashMap<String, Configuration.PeerInfo> peerList;
	private RandomAccessFile outputFile;
	private String outFileName;
	private int fileSize;
	private int pieceSize;
	private int numOfPieces;
	private BitfieldHandler bitfieldHandler;
	private Controller controller;
	private String peerID;

	public FileHandler(Controller controller)
	{
		this.controller = controller;
		commonInfo = controller.getConfiguration().getCommonInfo();
		outFileName = commonInfo.get("FileName");
		fileSize = Integer.parseInt(commonInfo.get("FileSize"));
		pieceSize = Integer.parseInt(commonInfo.get("PieceSize"));
		numOfPieces = (int) Math.ceil((double)fileSize/(double)pieceSize);
	}

	public FileHandler(String outFileName, int fileSize, int pieceSize)
	{
		this.outFileName = outFileName;
		this.fileSize = fileSize;
		this.pieceSize = pieceSize;
		numOfPieces = (int) Math.ceil((double)fileSize/(double)pieceSize);
	}
	
	public void initialConfiguration()
	{
		try
		{
			outputFile = new RandomAccessFile(outFileName, "rw");
			//outputFile.setLength(fileSize);
			System.out.println("file length: " + outputFile.length());
			outputFile.setLength(fileSize);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//System.out.println("FILEHANDLER GET CONFIG");
		//peerList = controller.getConfiguration().getPeerList();***
		//peerID = controller.getPeerID();***
		
		if(bitfieldHandler == null)
		{
			bitfieldHandler = (BitfieldHandler) ModuleFactory.createBitfieldHandlerMod(this);
		}
		
	}

	//write piece to file
	public void writePiece(int index, Message m)
	{
		//outputFile.seek(index*pieceSize);
		//outputFile.write(
	}

	public void writePiece(int index, byte[] piece)
	{
			try
			{
				outputFile.seek(index*pieceSize);
				outputFile.write(piece);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
	}

	//return piece from file
	public byte[] getPiece(int index)
	{
		//if bitfield bit off return null
		byte[] data = new byte[pieceSize];
		try
		{
			outputFile.seek(index*pieceSize);
			int readSize = outputFile.read(data);
			if(readSize != pieceSize)
			{
				byte[] smallerData = new byte[readSize];
				System.arraycopy(smallerData, 0, data,0, smallerData.length);
			}				
			return data;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
	
	}
	
	public HashMap<String, Configuration.PeerInfo> getPeerList()
	{
		return peerList;
	}
	
	public String getPeerID()
	{
		return peerID;
	}

	public String getOutFileName()
	{
		return outFileName;
	}

	public double getFileSize()
	{
		return fileSize;
	}

	public double getPieceSize()
	{
		return pieceSize;
	}

	public int getNumOfPieces()
	{
		return numOfPieces;
	}
	
	public byte[] getBitfield(String id)
	{
		//System.out.println("IN FILEHANDLER");
		byte[] field = bitfieldHandler.getBitfield(id);
		//printBitfield("FILEHANDLER", field);
		return field;
	}
	
	public void setPeerBitfield(String id, byte[] bitfield)
	{
		bitfieldHandler.setPeerBitfield(id, bitfield);
		//System.out.println("ADDED PEER BITFIELD");
	}
	
	public boolean getInterested(String id)
	{
		return bitfieldHandler.getInterested(id);
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
}
