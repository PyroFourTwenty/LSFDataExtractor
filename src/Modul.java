import java.util.ArrayList;

public class Modul {
    public String veranstaltung, sprache, nr, information, studiengang, typ;
    public int semester;
    public ArrayList<Gruppe> gruppen = new ArrayList<Gruppe>(0);
    public Modul parent;
    public Modul (){
    }
}
