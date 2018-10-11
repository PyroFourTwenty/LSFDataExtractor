public class SemesterHref extends StringHrefPair{
    String semester, href;

    public SemesterHref(String semester, String href){
        super(semester,href,HrefType.Semester.value);
        this.semester = semester;
        this.href = href;
    }
}
