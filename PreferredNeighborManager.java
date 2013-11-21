import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
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
    //MapUtil mapUtil;
    
   
    
    PreferredNeighborManager(Controller peerController)
    {
        this.controller = peerController;
        configInstance = controller.getConfiguration();
        
        scheduler = Executors.newScheduledThreadPool(1);
        int unchokingInterval = Integer.parseInt(configInstance.getCommonInfo().get("UnchokingInterval"));
        System.out.println(unchokingInterval);
        taskHandle = scheduler.scheduleAtFixedRate(this, unchokingInterval, unchokingInterval, TimeUnit.SECONDS);
        
        //mapUtil = new MapUtil();
    }
    
    PreferredNeighborManager(int unchokingInterval)
    {       
        scheduler = Executors.newScheduledThreadPool(1);
        taskHandle = scheduler.scheduleAtFixedRate(this, unchokingInterval, unchokingInterval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        //select neighbors that have transmitted to this peer at the highest rates
    	//HashMap<String, Float> downloadRates = controller.getPeerDownloadRates();    	
    	
    	Random random = new Random(System.currentTimeMillis());
        Map<String, Float> testMap = new HashMap<String, Float>(10);
        for(int i = 0 ; i < 10 ; ++i) {
            testMap.put( "SomeString" + random.nextInt()%1000, (Float)(random.nextInt()%1000 + random.nextFloat()));
            
        }
        
        System.out.println("size: " + testMap.size());
        testMap = MapUtil.sortByValue( testMap );
    	
    		
    	for(Entry<String, Float> entry: testMap.entrySet())
    	{
    		System.out.println(entry.getKey() + ", " + entry.getValue());
    	}    	
    }
    
    
    
}
