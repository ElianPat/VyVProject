
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.util.List;
import mx.uv.fei.logic.Course;
import mx.uv.fei.logic.DAOException;

/**
 *
 * @author aresj
 */
public interface ICourse {
    
    public List<Course> getAllNrc() throws DAOException;
    public List<Course> getCoursesFromProfessor(int personalNumber) throws DAOException;
    public List<Course> getAllCourse() throws DAOException;
    public List<Course> getNrcPerProfessor (int personalNumber) throws DAOException;
    public int addCourse (Course course) throws DAOException;
    public List<Course> getCoursesToModify () throws DAOException;
    public int updateCourse (Course course, String nrc) throws DAOException;
    public Course getInfoCourse (String nrc) throws DAOException;
    public boolean isNrcExisting(String nrc) throws DAOException;
    public List<Course> getPeriod() throws DAOException;
}
