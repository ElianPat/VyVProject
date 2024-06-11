/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.util.List;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Student;

/**
 *
 * @author alexs
 */
public interface IStudent {

    public List<Student> getStudentsPreliminaryProject(int directorNumber) throws DAOException;
    public List<Student> getStudentsReceptionalWork(int directorNumber) throws DAOException;
    public int registerStudent(Student student) throws DAOException;
    public List<Student> getEnrollmentPerProjectCodirector(int codirectorNumber) throws DAOException;
    public List<Student> getAllStudent() throws DAOException;  
    public int modifyStudent(Student student,  String enrollment) throws DAOException;
    public List<Student> getStudentsWithNotReceptionalWork() throws DAOException;
    public List<Student> getStudentForAssign() throws DAOException;
    public int assignPreliminaryProjectStudent(int idPreliminaryProyect, int idReceptionalWork, String enrollment) throws DAOException;
    public List<Student> getStudentsFromCourse(int personalNumber) throws DAOException;
    public List<Student> getEnrollmentPerProject(int directorNumber) throws DAOException;
    public List<Student> getEnrollmentPerCourse(int personalNumber) throws DAOException;
    public String getEnrollmentStudent(String email) throws DAOException;
    public Student getInformation(String email) throws DAOException;
    public List<Student> getStudentsPreliminaryProjectCodirector(int codirectorNumber) throws DAOException;
    public Student getInfoToModifyStudent(String enrollment) throws DAOException;
}
