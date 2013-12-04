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
    
    public void shutdown()
    {
    	
    	try{
			 System.err.println("TERMINATING optimistic******************");
			if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
				scheduler.shutdownNow(); // Cancel currently executing tasks
			       // Wait a while for tasks to respond to being cancelled
			       if (!scheduler.awaitTermination(1, TimeUnit.SECONDS))
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
			System.out.println("Optimistic exception shutdown *********************************");
			scheduler.shutdownNow();
		}
    	taskHandle.cancel(true);
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
        
                        if(neighborPeer.getNeighborPeerID().equals(neighborPeerID) && neighborPeer.isChokedByPeer())
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
                        if(ChokedPeer.getPeerID().equals( neighborPeer.getPeerID()))
                        {
                                try{
                                        controller.getLogger().writeLogger(controller.getLogger().changeOfOptimistic(neighborPeer.getNeighborPeerID()));                                
                                        neighborPeer.sendUnchokeMsg(true);
                                        
                                        //neighborPeer.isChoked(false);//this is handled by peer when recieve message
                                        //controller.setNeighbor(neighborPeer);//what is this?
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