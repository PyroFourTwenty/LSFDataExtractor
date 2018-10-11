import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class DownloadThread extends Thread {

    private DownloadThreadManager parent;
    private StringHrefPair stringHrefPair;
    private Document doc;
    private int hrefType;
    public int id;


    public DownloadThread(StringHrefPair stringHrefPair, DownloadThreadManager parent, int id){
        //System.out.println("Thread with id "+id+" created");
        this.stringHrefPair=stringHrefPair;
        this.hrefType=stringHrefPair.type;
        this.parent=parent;
        this.id=id;
    }

    public void run(){
        try {
            this.doc= Jsoup.connect(stringHrefPair.href).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch(this.hrefType){
            //Semester
            case 0:
                Main.findModulHrefs(doc);
                break;
            //Modul
            case 1:
                Main.findGroups(doc);
                break;
        }

        try{
            this.parent.sem.acquire();
            this.parent.runningThreadsCounter--;
            this.parent.sem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //System.out.println("Thread with "+id+" executed, runningThreads: "+this.parent.runningThreadsCounter);

    }

}
