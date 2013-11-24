import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
    int numPreferredNeighbors;
    
   
    
    PreferredNeighborManager(Controller peerController)
    {
        this.controller = peerController;
        configInstance = controller.getConfiguration();
        numPreferredNeighbors = Integer.parseInt(configInstance.getCommonInfo().get("NumberOfPreferredNeighbors"));
        
        scheduler = Executors.newScheduledThreadPool(1);
        int unchokingInterval = Integer.parseInt(configInstance.getCommonInfo().get("UnchokingInterval"));
        System.out.println(unchokingInterval);
        taskHandle = scheduler.scheduleAtFixedRate(this, unchokingInterval, unchokingInterval, TimeUnit.SECONDS);
    }
    
    PreferredNeighborManager(int unchokingInterval)
    {       
        scheduler = Executors.newScheduledThreadPool(1);
        taskHandle = scheduler.scheduleAtFixedRate(this, unchokingInterval, unchokingInterval, TimeUnit.SECONDS);
    }
    
    public void shutdown()
    {
    	
    	try{
			 System.err.println("TERMINATING PREFERRED******************");
			if (!scheduler.awaitTermination(15, TimeUnit.SECONDS)) {
				scheduler.shutdownNow(); // Cancel currently executing tasks
			       // Wait a while for tasks to respond to being cancelled
			       if (!scheduler.awaitTermination(15, TimeUnit.SECONDS))
			       {
			           System.out.println("PREFERRED did not terminate");
			       }
			       else
			       {
			    	   System.out.println("PREFERRED DID TERMINATE");
			       }
			}
			else
			{
				System.out.println("PREFERRED DID TERMINATE");
			}
		}
		catch(InterruptedException e)
		{
			// (Re-)Cancel if current thread also interrupted
			scheduler.shutdownNow();
		}
    	taskHandle.cancel(true);
    }

    @Override
    public void run() {
        //select neighbors that have transmitted to this peer at the highest rates
    	Map<String, Float> downloadRates = controller.getPeerDownloadRates();   
        downloadRates = MapUtil.sortByValue( downloadRates );
      //  System.out.println("downloadRates.size(): " + downloadRates.size());
        
    	for(Entry<String, Float> entry: downloadRates.entrySet())
    	{
    		System.out.println("PNM peerDownloadRates: " + entry.getKey() + ", " + entry.getValue());
    	}    	
    	
    	List<String> preferredNeighbors = new ArrayList<String>();
    	for(Entry<String, Float> entry: downloadRates.entrySet())
    	{
    		if(controller.getInterestedNeighbors().contains(entry.getKey()))
    		{
    			preferredNeighbors.add(entry.getKey());
    		}
    		
    		if(preferredNeighbors.size() == numPreferredNeighbors) break;
    	}
    	
    	controller.setPreferredNeighbors(preferredNeighbors);
    }
    
    
    
}
