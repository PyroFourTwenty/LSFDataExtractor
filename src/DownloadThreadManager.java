import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class DownloadThreadManager extends Thread{

    public ArrayList<Thread> threads = new ArrayList<>();
    public boolean isFinished = false;
    private boolean decrementingReady = false;
    public int runningThreadsCounter = 0;
    public Semaphore sem;
    public DownloadThreadManager(){

    }
    public void run(){
        System.out.println("Download-Manager started");
        Timestamp t1 = new Timestamp(System.currentTimeMillis());

        runningThreadsCounter=this.threads.size();
        sem = new Semaphore(1);
        for(Thread t : this.threads){
            t.run();
        }
        while (runningThreadsCounter>0){}
        Timestamp t2 = new Timestamp(System.currentTimeMillis());
        long delta = t2.getTime()-t1.getTime();
        System.out.println("Download-Manager finished. Time elapsed: "+delta + " ms");
        this.isFinished=true;
    }

}
