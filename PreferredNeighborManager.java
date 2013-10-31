import java.util.concurrent.*;

/**
 *
 * @author Robin
 */
public class PreferredNeighborManager implements Runnable{
    private Controller controller;
    private Configuration configInstance;
    String preferredNeighbors[];
    String optimisticNeighbor[];
    
    PreferredNeighborManager(Controller peerController)
    {
        this.controller = peerController;
        configInstance = controller.getConfiguration();
    }

    @Override
    public void run() {
        
    }
    
    
    
}
