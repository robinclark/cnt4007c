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

    @Override
    public void run() {
        //select neighbors that have transmitted to this peer at the highest rates
    	Map<String, Float> downloadRates = controller.getPeerDownloadRates();    	
        
        System.out.println("downloadrate size: " + downloadRates.size());
        downloadRates = MapUtil.sortByValue( downloadRates );
    
    	for(Entry<String, Float> entry: downloadRates.entrySet())
    	{
    		System.out.println("peerDownloadRates: " + entry.getKey() + ", " + entry.getValue());
    	}    	
    	
    	List<String> preferredNeighbors = new ArrayList<String>();
    	for(Entry<String, Float> entry: downloadRates.entrySet())
    	{
    		preferredNeighbors.add(entry.getKey());
    		if(preferredNeighbors.size() == numPreferredNeighbors) break;
    	}
    	
    	controller.setPreferredNeighbors(preferredNeighbors);
    }
    
    
    
}
