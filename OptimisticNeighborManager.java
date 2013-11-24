import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class OptimisticNeighborManager implements Runnable{
    private Controller controller;
    private Configuration configInstance;
    private String preferredNeighbors[];
    private String optimisticNeighbor[];
    private ScheduledExecutorService scheduler = null;
    private ScheduledFuture<?> taskHandle = null;
    
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
	
	System.out.println("DOING OPTIMISTIC CHANGING WORK");
	HashMap<Integer,Peer> InterestedAndChokedPeers = new HashMap<Integer,Peer>();
	Random rdx = new Random();
	Peer ChokedPeer;	
	int x = 0;
	System.out.println("optim: " + controller);
	for(Peer neighborPeer : controller.getNeighborsList())
	{

		System.out.print(" PEER: " + neighborPeer.getPeerID() + " Neighbor: " + neighborPeer.getNeighborPeerID());
		for(String neighborPeerID : controller.getInterestedPeers())
		{
	
			if(neighborPeer.getNeighborPeerID() == neighborPeerID && neighborPeer.isChokedByPeer())
			{
				InterestedAndChokedPeers.put(x,neighborPeer);
				x++;
				
			}
			
		} 

	}
	System.out.println();

	if(x != 0)
	{
		ChokedPeer = InterestedAndChokedPeers.get(rdx.nextInt(x));
		
		for(Peer neighborPeer : controller.getNeighborsList())
		{
			if(ChokedPeer.getPeerID() == neighborPeer.getPeerID())
			{
					
				neighborPeer.sendUnchokeMsg(true);
				neighborPeer.isChoked(false);
				controller.setNeighbor(neighborPeer);
				controller.setOptimisticPeerID(neighborPeer.getPeerID());
			}

		
		}
	}
	

    }
    
    
    
}
