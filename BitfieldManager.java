
import java.util.HashMap;

/**
 *
 * @author Robin
 */
public class BitfieldManager {
    HashMap<String, boolean[]> bitFields;
    
    BitfieldManager()
    {
        bitFields = new HashMap<String, boolean[]>();
    }
    
    public void setMap(String peerID, boolean peerBitfield[])
    {
        bitFields.put(peerID, peerBitfield.clone());
    }
    
    public void addPiece(String peerID, int index)
    {
        bitFields.get(peerID)[index] = true;
    }
}
