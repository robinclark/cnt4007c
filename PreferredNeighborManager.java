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
    ScheduledExecutorService scheduler = null;
    ScheduledFuture<?> taskHandle = null;
    
    PreferredNeighborManager(Controller peerController)
    {
        this.controller = peerController;
        configInstance = controller.getConfiguration();
        
        scheduler = Executors.newScheduledThreadPool(1);
        int unchokingInterval = Integer.parseInt(configInstance.getCommonInfo().get("OptimisticUnchokingInterval"));
        taskHandle = scheduler.scheduleAtFixedRate(this, 0, unchokingInterval, TimeUnit.SECONDS);
    }
    
    PreferredNeighborManager(int unchokingInterval)
    {       
        scheduler = Executors.newScheduledThreadPool(1);

        taskHandle = scheduler.scheduleAtFixedRate(this, 5, unchokingInterval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        System.out.println("Woo");
    }
    
    
    
}
