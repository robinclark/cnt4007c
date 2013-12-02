import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.io.IOException;


public class OptimisticNeighborManager implements Runnable{
    private Controller controller;
    private Configuration configInstance;
    private String preferredNeighbors[];
    private String optimisticNeighbor[];
    private ScheduledExecutorService scheduler = null;
    private ScheduledFuture<?> taskHandle = null;
    private Logger log;
    
    OptimisticNeighborManager(Controller peerController)
    {
        this.controller = peerController;
        configInstance = controller.getConfiguration();
        
        scheduler = Executors.newScheduledThreadPool(1);
        int optimisticInterval = Integer.parseInt(configInstance.getCommonInfo().get("OptimisticUnchokingInterval"));
        System.out.println(optimisticInterval);
        taskHandle = scheduler.scheduleAtFixedRate(this,optimisticInterval, optimisticInterval, TimeUnit.SECONDS);
    }
    
    OptimisticNeighborManager(Controller peerController, int optimisticInterval)
    {       
	this.controller = peerController;
        scheduler = Executors.newScheduledThreadPool(1);
        taskHandle = scheduler.scheduleAtFixedRate(this, optimisticInterval, optimisticInterval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
	
	HashMap<Integer,Peer> InterestedAndChokedPeers = new HashMap<Integer,Peer>();
	Random rdx = new Random(System.currentTimeMillis());
	Peer ChokedPeer;	
	int x = 0;
	for(Peer neighborPeer : controller.getNeighborsList())
	{
		for(String neighborPeerID : controller.getInterestedPeers())
		{
	
			if(neighborPeer.getNeighborPeerID() == neighborPeerID && neighborPeer.isChokedByPeer())
			{
				InterestedAndChokedPeers.put(x,neighborPeer);
				x++;
				
			}
			
		} 

	}

	if(x != 0)
	{
		ChokedPeer = InterestedAndChokedPeers.get(rdx.nextInt(x));
		
		for(Peer neighborPeer : controller.getNeighborsList())
		{
			if(ChokedPeer.getPeerID() == neighborPeer.getPeerID())
			{
				try{
					controller.getLogger().writeLogger(controller.getLogger().changeOfOptimistic(neighborPeer.getNeighborPeerID()));				
					neighborPeer.sendUnchokeMsg(true);
					neighborPeer.isChoked(false);
					controller.setNeighbor(neighborPeer);
					controller.setOptimisticPeerID(neighborPeer.getPeerID());
				   }catch(IOException e)
				   {
					System.out.println("logger has not been set up or is invaild");
				   }


			}

		
		}
	}
	

    }
    
    
    
}
