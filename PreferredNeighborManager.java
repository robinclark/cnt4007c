import java.util.concurrent.*;

/**
 *
 * @author Robin
 */
public class PreferredNeighborManager implements Runnable{
    private PeerController peerController;
    private Configuration configInstance;
    String preferredNeighbors[];
    String optimisticNeighbor[];
    
    PreferredNeighborManager(PeerController peerController)
    {
        this.peerController = peerController;
        configInstance = peerController.getConfiguration();
    }

    @Override
    public void run() {
        
    }
    
    
    
}
