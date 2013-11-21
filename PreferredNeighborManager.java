import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Robin
 */
public class PreferredNeighborManager implements Runnable{
    private Controller controller;
    private Configuration configInstance;
    String preferredNeighbors[];
    String optimisticNeighbor[];
    ScheduledExecutorService scheduler = null;
    ScheduledFuture<?> taskHandle = null;
    
    PreferredNeighborManager(Controller peerController)
    {
        this.controller = peerController;
        configInstance = controller.getConfiguration();
        
        scheduler = Executors.newScheduledThreadPool(1);
        int unchokingInterval = Integer.parseInt(configInstance.getCommonInfo().get("OptimisticUnchokingInterval"));
        System.out.println(unchokingInterval);
        taskHandle = scheduler.scheduleAtFixedRate(this, unchokingInterval, unchokingInterval, TimeUnit.SECONDS);
    }
    
    PreferredNeighborManager(int unchokingInterval)
    {       
        scheduler = Executors.newScheduledThreadPool(1);
        taskHandle = scheduler.scheduleAtFixedRate(this, unchokingInterval, unchokingInterval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        //select neighbors that have transmitted to this peer at the highest rates
    	HashMap<String, Float> uploadRates = controller.getPeerUploadRates();    	
    }
    
    
    
}
