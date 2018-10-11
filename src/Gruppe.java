import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Gruppe {
    public boolean weekly;
    private String termin;
    public String titel;
    public Date anfangszeit, endzeit, anfangsdatum, enddatum, tag;

    public Gruppe(String titel, String termin){
        //System.out.println("New Group created: "+ titel);
        //System.out.println("Termin: "+ termin);
        this.titel = titel;
        this.termin = termin;
        this.parseTimes();
    }
    /*
    termin besteht aus: Wochentag | Anfangszeit - Endzeit | Wöchentlich/Einzeltermin | Beginn: Datum | Ende: Datum
     */
    private void parseTimes(){
        String [] s = this.termin.split("\\|");
        SimpleDateFormat zeitFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat tagFormat = new SimpleDateFormat("dd.MM.yyyy");
        try{
            String s1,s2,s3,s4;
            s1 = s[1].split("-")[0];
            s2= s[1].split("-")[1];
            s3=s[3].split(":")[1];
            s4 = s[4].split(":")[1];
            this.anfangszeit = zeitFormat.parse(s1);
            this.endzeit = zeitFormat.parse(s2);
            this.anfangsdatum = tagFormat.parse(s3);
            this.enddatum = tagFormat.parse(s4);
        } catch(ParseException e){
            System.out.println("Something went wrong: "+e.getMessage());
        }
    }
}
