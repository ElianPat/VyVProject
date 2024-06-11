/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author alexs
 */
public class PreliminaryProject {

    private String projectName;
    private String description;
    private String lgac;
    private String lineOfResearch;
    private String studentRequirements;
    private int duration;
    private int codirectorNumber;
    private int directorNumber;
    private String keyAcademicGroup;
    private String expectedResults;
    private int quota;
    private String recommendedBibliography;
    private String notes;
    private String state;
    private String codirectorName;
    private String academicGroupName;
    private String directorName;

    private final String PROJECT_NAME_REGEX = "^[\\p{L}\\p{M}\\s,\\.]{1,250}$";
    private final String EXPECTED_RESULTS_REGEX = "^[\\p{L}\\p{M}\\s,:\\.\\-]{1,5000}$";
    private final String DESCRIPTION_REGEX = "^^(?s)(?=.|\\n{10,10000}$).*";
    private final String LINE_OF_RESEARCH_REGEX = "^[\\p{L}\\p{M}\\s,\\.]{1,500}$";
    private final String STUDENT_REQUERIMENTS_REGEX = "^[\\p{L}\\p{M}\\s,\\.]{1,500}$";
    private final String BIBLIOGRAPHY_REGEX = "^[\\p{L}\\p{M}\\p{N}\\p{P}\\s,/:\\.'\"_\\?-]{1,8000}$";
    private final String NOTES_REGEX = "^[\\p{L}\\p{M}\\p{N}\\p{P}\\s,\\.]{1,5000}$";

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCodirectorName() {
        return codirectorName;
    }

    public void setCodirectorName(String codirectorName) {
        this.codirectorName = codirectorName;
    }

    public String getAcademicGroupName() {
        return academicGroupName;
    }


    public void setAcademicGroupName(String academicGroupName) {
        this.academicGroupName = academicGroupName;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        checkProjectName(projectName);
        this.directorName = directorName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        checkDescripion(description);
        this.description = description;
    }

    public String getLgac() {
        return lgac;
    }

    public void setLgac(String lgac) {
        this.lgac = lgac;
    }

    public String getLineOfResearch() {
        return lineOfResearch;
    }

    public void setLineOfResearch(String lineOfResearch) {
        checkLineOfResearch(lineOfResearch);
        this.lineOfResearch = lineOfResearch;
    }

    public String getStudentRequirements() {
        return studentRequirements;
    }

    public void setStudentRequirements(String studentRequirements) {
        checkStudentRequeriments(studentRequirements);
        this.studentRequirements = studentRequirements;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCodirectorNumber() {
        return codirectorNumber;
    }

    public void setCodirectorNumber(int codirectorNumber) {
        this.codirectorNumber = codirectorNumber;
    }

    public int getDirectorNumber() {
        return directorNumber;
    }

    public void setDirectorNumber(int directorNumber) {
        this.directorNumber = directorNumber;
    }

    public String getKeyAcademicGroup() {
        return keyAcademicGroup;
    }

    public void setKeyAcademicGroup(String keyAcademicGroup) {
        this.keyAcademicGroup = keyAcademicGroup;
    }

    public String getExpectedResults() {
        return expectedResults;
    }

    public void setExpectedResults(String expectedResults) {
        checkExpectedResults(expectedResults);
        this.expectedResults = expectedResults;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public String getRecommendedBibliography() {
        return recommendedBibliography;
    }

    public void setRecommendedBibliography(String recommendedBibliography) {
        checkBibliography(recommendedBibliography);
        this.recommendedBibliography = recommendedBibliography;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        checkNotes(notes);
        this.notes = notes;
    }

    @Override
    public String toString() {
        return getProjectName();
    }

    private void checkProjectName(String projectName) {
        if (projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del anteproyecto no puede estar vacio");
        }
        Pattern pattern = Pattern.compile(PROJECT_NAME_REGEX);
        Matcher matcher = pattern.matcher(projectName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("El nombre del anteproyecto solo puede contener letras y un único espacio entre palabras y un máximo de 250 carácteres");
        }
    }

    private void checkExpectedResults(String expectedResult) {
        if (expectedResult.trim().isEmpty()) {
            throw new IllegalArgumentException("Los resultados esperados no pueden estar vacio");
        }
        Pattern pattern = Pattern.compile(EXPECTED_RESULTS_REGEX);
        Matcher matcher = pattern.matcher(expectedResult);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Los resultados esperados solo puede contener letras y un único espacio entre palabras y un máximo de 2000 carácteres");
        }
    }

    private void checkDescripion(String description) {
        if (description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacia");
        }
        Pattern pattern = Pattern.compile(DESCRIPTION_REGEX);
        Matcher matcher = pattern.matcher(description);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("La descripción solo puede tener un máximo de 5000 carácteres");
        }
    }

    private void checkLineOfResearch(String lineOfResearch) {
        if (lineOfResearch.trim().isEmpty()) {
            throw new IllegalArgumentException("La línea de investigación no puede estar vacia");
        }
        Pattern pattern = Pattern.compile(LINE_OF_RESEARCH_REGEX);
        Matcher matcher = pattern.matcher(lineOfResearch);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("La línea de investigación solo puede contener letras y un único espacio entre palabras y un máximo de 250 carácteres");
        }
    }

    private void checkStudentRequeriments(String studentRequeriments) {
        if (studentRequeriments.trim().isEmpty()) {
            throw new IllegalArgumentException("Los requisitos del estudiante no puede estar vacio");
        }
        Pattern pattern = Pattern.compile(STUDENT_REQUERIMENTS_REGEX);
        Matcher matcher = pattern.matcher(studentRequeriments);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Los requisitos del estudiante solo puede contener letras y un único espacio entre palabras y un máximo de 250 carácteres");
        }
    }

    private void checkBibliography(String bibliography) {
        if (bibliography.trim().isEmpty()) {
            throw new IllegalArgumentException("La bibliografía no puede estar vacia");
        }
        Pattern pattern = Pattern.compile(BIBLIOGRAPHY_REGEX);
        Matcher matcher = pattern.matcher(bibliography);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("La bibliografía solo puede contener un máximo de 2000 carácteres");
        }
    }

    private void checkNotes(String notes) {
        if (notes.trim().isEmpty()) {
            throw new IllegalArgumentException("Las notas no puede estar vacia");
        }
        Pattern pattern = Pattern.compile(NOTES_REGEX);
        Matcher matcher = pattern.matcher(notes);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Las notas solo puede contener un máximo de 2000 carácteres");
        }
    }
    
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof PreliminaryProject) {
            PreliminaryProject other = (PreliminaryProject) object;
            return Objects.equals(this.getProjectName(), other.getProjectName());
        }
        return false;
    }
}
