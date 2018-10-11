import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static int n = 10;
    public static String html = "";
    public static ArrayList<Modul> modules = new ArrayList<Modul>(0);
    /*
    This String array contains all combinations and is used to create the bitBoolPairArray
     */
    public static String[] bitArr ={};
    /*
    This Array stores BitBoolPairs, containing a individual string of bits and the corresponding boolean value to determine if this combination is possible
     */
    public static BitBoolPair [] bitBoolPairArray = {};

    /*
    This String array stores all found hrefs from the root page
     */
    public static ArrayList<SemesterHref>  semesterHrefs = new ArrayList<>();

    public static ArrayList<ModulHref> modulHrefs = new ArrayList<>();

    public static synchronized void findGroups(Document doc){
        System.out.println("asdfsdf");
        Elements e = doc.getElementsByTag("tr");
        String [] s;
        String veranstaltung = "", gr = "", termin = "", information = "", studiengang = "";
        Modul m;
        Gruppe g;
        for(int i = 0; i<e.size(); i++){
            s = e.get(i).text().split(" ");
            if(s[0].equals("Veranstaltung")){
                m= new Modul();
                veranstaltung = String.join(" ", s).replace("Veranstaltung", "");
                //System.out.println("Veranstaltungstitel: "+veranstaltung);
                m.veranstaltung = veranstaltung;
                s=e.get(i+1).text().split(" ");
                if (s[0].equals("Sprache")){
                    //System.out.println("Sprache: "+s[1]);
                    m.sprache=s[1];
                    s=e.get(i+2).text().split(" ");
                    if(s[0].equals("Nr.:")){
                        //System.out.println("Nr.: "+s[1]);
                        m.nr=s[1];
                        s=e.get(i+3).text().split(" ");
                        if (s[0].equals("Information")){
                            //for (int o = 1; o<s.length; o++ ){information=information+" "+s[o];} //Alle restlichen Wörter zusammenführen;
                            information = String.join(" ", s).replace("Information", "");
                            //System.out.println("Informationen: "+information);
                            m.information=information;
                            s=e.get(i+4).text().split(" ");
                            if(s[0].equals("Studiengang")){
                                //for (int o = 1; o<s.length; o++ ){studiengang=studiengang+" "+s[o];} //Alle restlichen Wörter zusammenführen;
                                studiengang = String.join(" ", s).replace("Studiengang", "");
                                //System.out.println("Studiengang: "+studiengang);
                                m.studiengang = studiengang;
                                if (m.veranstaltung.contains("(Ü)")){
                                    m.parent = modules.get(modules.size()-1);
                                }
                                modules.add(m);
                            }
                        }
                    }
                }
            }
            else if (s[0].equals("Gruppe")){
                gr = String.join(" ", s).replace("Gruppe ", "");
                s=e.get(i+1).text().split(" ");
                if (s[0].equals("Termin")){
                    termin = String.join(" ", s).replace("Termin", "");
                    g = new Gruppe(gr, termin);
                    modules.get(modules.size()-1).gruppen.add(g);
                }
            }
        }
    }

    /**
     * This method finds urls to all modules of a semester
     * @param doc The document
     */

    public static void findModulHrefs(Document doc){
        String [] s;
        String href, modulName = "";
        ArrayList<ModulHref> hrefs = new ArrayList<>();
        Elements e = doc.getElementsByTag("a");
        for (int i = 0; i < e.size(); i++){
            s = e.get(i).text().split(" ");
            if(s.length>1){
                String lastWord = s[s.length-1];
                if(lastWord.equals("(SL)") || lastWord.equals("(Ü)")){
                    href = e.get(i).attr("href");
                    modulName = e.get(i).text();
                    hrefs.add(new ModulHref(modulName,href));
                    //System.out.println("Found Name: "+modulName+" Href:"+href);
                }
            }
        }
        modulHrefs.addAll(hrefs);
    }

    public static void findSemesterHrefs(Document doc){
        String [] s;
        ArrayList<SemesterHref> hrefs = new ArrayList<>();

        Elements e = doc.getElementsByTag("a");

        for(int i = 0; i< e.size(); i++){
            s = e.get(i).text().split(" ");
            if (s.length==2){
                if (s[1].equals("Semester")){

                    String href = e.get(i).attr("href");
                    hrefs.add(new SemesterHref(""+s[0]+" "+s[1], href));
                    //System.out.println("Found: "+s[0]+ " "+s[1] + " with href: "+href);

                }
            }
        }
        semesterHrefs.addAll(hrefs);
    }


    public static void main (String [] args) {

        /*
        ArrayList<Modul> chosenModules = new ArrayList<>();
        chosenModules.add(modules.get(4));
        chosenModules.add(modules.get(5));

        ArrayList<ArrayList<Gruppe>> allCombinations = new ArrayList<>();

        bitArr = generateBitArray(chosenModules.size());
        ArrayList<Gruppe>chosenGroups = new ArrayList<>();
        int x = 0; //This is set to 2 when a module has 4 exercise groups
        */
        try {
            Document source;
            Document root = Jsoup.connect("https://lsf.htw-berlin.de/qisserver/rds?state=wtree&search=1&trex=step&root120182=22933%7C23424%7C23493&P.vx=kurz").get();

            System.out.println("Getting content from page "+root.title()+" Please wait... ");
            findSemesterHrefs(root);

            DownloadThreadManager manager = new DownloadThreadManager();

            for (int i = 0; i<semesterHrefs.size();i++){
                //source = Jsoup.connect(semesterHrefs.get(i).href).get();
                //findModulHrefs(source);
                manager.threads.add(new DownloadThread(semesterHrefs.get(i), manager, i));

            }
            manager.run();
            while(!manager.isFinished){
                //WAIT until manager is finished
            }
            manager = new DownloadThreadManager();
            //System.out.println("Suche Module: "+modulHrefs.size());

            for (int i = 0 ; i<modulHrefs.size(); i++){
                manager.threads.add(new DownloadThread(modulHrefs.get(i), manager, i));
                //findGroups(Jsoup.connect(modulHrefs.get(i).href).get());
            }
            manager.run();
            while(!manager.isFinished){
                //WAIT until manager is finished
            }

            //nur zum Zählen der Gruppen
            int temp = 0;
            for (Modul m : modules){
                for (Gruppe g: m.gruppen){
                    temp++;
                }
            }
            System.out.println("Found modules: "+modules.size());
            System.out.println("Found groups: "+temp);
            /*
            System.out.println("Gefundene Module: "+modules.size());

            for (int i = 0; i< modules.size(); i++){
                if(modules.get(i).parent!=null)
                    System.out.println("<<<<<<<<<<<<<<<<<<<Current Ü: "+modules.get(i).veranstaltung+">>>>>>>>>>>>>>>>>");
                else
                    System.out.println("<<<<<<<<<<<<<<<<<<<Current SL: "+modules.get(i).veranstaltung+">>>>>>>>>>>>>>>>>");

                for (int o = 0; o< modules.get(i).gruppen.size(); o++){
                    System.out.println("Group "+(o+1)+": "+modules.get(i).gruppen.get(o).titel);
                }
            }*/


        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        for(int i = 0; i< bitArr.length; i++){
            System.out.println("Current combination: "+bitArr[i]+ "");
            char [] c = bitArr[i].toCharArray();
            for (int o = 0; o<c.length; o++){
                System.out.println("c[o] -->"+c[o]+"  i: "+i+"  o:"+o);
                if (chosenModules.get(o).gruppen.size()==4){
                    x = 2*Integer.parseInt(""+c[o-1]);
                }
                chosenGroups.add(chosenModules.get(o).gruppen.get(Integer.parseInt(""+c[o])+x));
            }
            allCombinations.add(new ArrayList<Gruppe>());
            allCombinations.get(allCombinations.size()-1).addAll(chosenGroups);
            chosenGroups.clear();
        }
        for (int i= 0; i< allCombinations.size(); i++){
            System.out.println("<<<<<<<<Combination for bits: "+bitArr[i]);
            for (int o = 0; o < allCombinations.get(i).size();o++ ){
                System.out.println("Index: "+o+ " Veranstaltung: "+chosenModules.get(o).veranstaltung+" Gruppetitel: "+allCombinations.get(i).get(o).titel);
            }
        }*/
    }
         /*

        //Modul m1 = new Modul(); m1.veranstaltung="Grundlagen mobiler Anwendungen";

        readHTMLToString("C:/Users/Lucan/Documents/NetBeansProjects/LSFDataExtractor/src/res/prog3.htm");
        Document document = Jsoup.parse(html);

        Elements tableRows = document.getElementsByTag("tr");
        //Elements tableCells = document.getElementsByTag("a");
        findGroups(tableRows);

        for (int i = 0; i<modules.size();i++){
            Modul currentModule = modules.get(i);
            System.out.println("Modules.index "+i+": "+currentModule.veranstaltung + " mit "+currentModule.gruppen.size()+" Gruppen.");
            for (int o = 0; o<currentModule.gruppen.size(); o++){
                System.out.println("Gruppe: "+currentModule.gruppen.get(o).titel);
            }
        }


        findModulHrefs(tableCells);



        bitArr = generateBitArray(n);
 */
        //bitArr=generateBitArray(n);
        //generateBitBoolPairArray();

        //for(int i = 0; i< bitArr.length; i++){System.out.println(""+bitArr[i]);}




    /**
     * This method generates and returns a string array of bits representing all combinations of the modules and their times
     * @param n Number of modules
     * @return string array of bitwise combinations
     */
    public static String [] generateBitArray(int n){
        String s = "";
        int limit = (int) Math.pow(2, n);
        System.out.println("Limit: "+limit);
        String [] arr = new String[limit];
        s = fillBitString(s, n);
        for (int i = 0; i<limit; i++){
            arr[i]=fillBitString(s,n);
            s= Integer.toBinaryString(Integer.valueOf(s,2)+1);
        }
        return arr;
    }

    /**
     * This is a helper method which fills the given string with zeros to a certain length
     * @param s The string which should be filled
     * @param n The wanted length of the string
     * @return The string with the wanted length will be returned;
     */
    private static String fillBitString(String s, int n){
        int sLength = s.length();
        if (sLength<n){
            for (int i = n-sLength; i>0; i--){
                s= "0"+s;
            }
        }
        return s;
    }

    /**
     * This method generates an Array of BitBoolPair objects which is needed to determine and store whether the combination is possible or not
     */
    public static void generateBitBoolPairArray(){
        bitBoolPairArray = new BitBoolPair[bitArr.length];
        for (int i = 0; i < bitBoolPairArray.length;i++ ){
            bitBoolPairArray[i] = new BitBoolPair(bitArr[i]);
            System.out.println(""+bitBoolPairArray[i].toString());
        }
    }

    public static void checkPossibilities(){


    }

    public static void readHTMLToString(String filePath){
        StringBuilder contentBuilder = new StringBuilder();
        try{
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String str;
            while ((str = in.readLine()) != null){
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e){
            System.out.println("Beim Lesen der Datei ist ein Problem aufgetreten:"+System.lineSeparator()+e);
        }
        html = contentBuilder.toString();

    }


}