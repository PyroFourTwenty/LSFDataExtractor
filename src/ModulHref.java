public class ModulHref extends StringHrefPair {
    String modul, href;

    public ModulHref(String modul, String href){
        super(modul,href, HrefType.Modul.value);
        this.modul = modul;
        this.href=href;
    }

}
