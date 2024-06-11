/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

/**
 *
 * @author aresj
 */
public final class ClassHolder {

    private final static ClassHolder INSTANCE = new ClassHolder();
    private User user;
    private Director director;
    private Student student;
    private Advance advance;
    private PreliminaryProject preliminaryProject;
    private AcademicGroup academicGroup;

    private ClassHolder() {
    }

    public static ClassHolder getInstance() {
        return INSTANCE;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Advance getAdvance() {
        return advance;
    }

    public void setAdvance(Advance advance) {
        this.advance = advance;
    }

    public PreliminaryProject getPreliminaryProject() {
        return preliminaryProject;
    }

    public void setPreliminaryProject(PreliminaryProject preliminaryProject) {
        this.preliminaryProject = preliminaryProject;
    }

    public AcademicGroup getAcademicGroup() {
        return academicGroup;
    }

    public void setAcademicGroup(AcademicGroup academicGroup) {
        this.academicGroup = academicGroup;
    }
}
