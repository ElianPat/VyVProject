/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.fei.dataaccess.DataBaseManager;
import mx.uv.fei.gui.controller.App;
import mx.uv.fei.interfaces.IStudent;

/**
 *
 * @author alexs
 */
public class StudentDAO implements IStudent {

    /**
     * obtiene una lista de los estudiantes que tengan un trabajo recepcional asignado
     * de un director.
     * 
     * @param directorNumber número de personal del director.
     * @return List de los estudiantes con un trabajo recepcional, null si 
     * no hay estudiantes asignados aun trabajo recepcional.
     * @throws DAOException 
     */
    
    @Override
    public List<Student> getStudentsReceptionalWork(int directorNumber) throws DAOException {
        List<Student> listOfStudentReceptionalWork = new ArrayList<>();
        try {
            String query = "SELECT u.Nombre, e.Matricula, tr.ID_TrabajoRecepcional FROM proyecto.estudiante e "
                    + "inner join proyecto.usuario u on u.correo = e.correo inner join proyecto.trabajorecepcional tr "
                    + "on e.ID_TrabajoRecepcional = tr.ID_TrabajoRecepcional where tr.N_personalDirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, directorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setName(result.getString("Nombre"));
                student.setEnrollment(result.getString("Matricula"));
                student.setIdReceptionalWork(result.getInt("ID_TrabajoRecepcional"));
                listOfStudentReceptionalWork.add(student);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de trabajos recepcionales por estudiante", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfStudentReceptionalWork;
    }
    
    /**
     * obtiene una lista de los estudiantes que tengan un anteproyecto asignado de un
     * director en especifico.
     * 
     * @param directorNumber número de personal del director.
     * @return List con los estudiantes asociados a un anteproyecto, nulo
     * si no hay estudiantes asociados a algún anteproyecto.
     * @throws DAOException 
     */

    @Override
    public List<Student> getStudentsPreliminaryProject(int directorNumber) throws DAOException {
        List<Student> listOfStudentPreliminaryProject = new ArrayList<>();
        try {
            String query = "SELECT e.Matricula, u.Nombre, a.Nombre_An FROM proyecto.estudiante"
                    + " e inner join proyecto.usuario u on u.correo = e.correo inner join proyecto.anteproyecto"
                    + " a on e.ID_anteproyecto = a.ID_Anteproyecto where a.N_personalDirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, directorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setEnrollment(result.getString("Matricula"));
                student.setName(result.getString("Nombre"));
                student.setPreliminaryProjectName(result.getString("Nombre_An"));
                listOfStudentPreliminaryProject.add(student);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de anteproyectos por estudiante", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfStudentPreliminaryProject;
    }

    /**
     * obtiene una lista de los estudiantes que tienen un anteproyecto asociado a 
     * un codirector.
     * 
     * @param codirectorNumber número de personal de codirector.
     * @return List con los estudiantes que tienen un anteproyecto y este
     * a su vez este asociado a un codirector.
     * @throws DAOException 
     */
    
    @Override
    public List<Student> getStudentsPreliminaryProjectCodirector(int codirectorNumber) throws DAOException {
        List<Student> listOfStudentPreliminaryProject = new ArrayList<>();
        try {
            String query = "SELECT e.Matricula, u.Nombre, a.Nombre_An, ex.Materia FROM proyecto.estudiante e \n"
                    + "inner join proyecto.usuario u on u.correo = e.correo \n"
                    + "inner join proyecto.anteproyecto a on e.ID_anteproyecto = a.ID_Anteproyecto \n"
                    + "inner join proyecto.curso c on c.NRC = e.NRC_Curso\n"
                    + "inner join proyecto.experienciaeducativa ex on c.Experiencia_Educativa = ex.ID_EE  \n"
                    + "where a.N_personalCodirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codirectorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setEnrollment(result.getString("Matricula"));
                student.setName(result.getString("Nombre"));
                student.setPreliminaryProjectName(result.getString("Nombre_An"));
                student.setEducativeExperience(result.getString("Materia"));
                listOfStudentPreliminaryProject.add(student);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de anteproyectos por estudiante", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfStudentPreliminaryProject;
    }

    /**
     * registra un nuevo estudiante a la base de datos.
     * 
     * @param student objeto de tipo estudiante.
     * @return 1 si el registro del estudiante fue exitoso, 0 si esto no fue así.
     * @throws DAOException 
     */
    
    @Override
    public int registerStudent(Student student) throws DAOException {
        int result;
        try {
            String query = "insert into estudiante(Matricula, correo, NRC_Curso) values (?,?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, student.getEnrollment());
            statement.setString(2, student.getEmail());
            statement.setString(3, student.getNrcCourse());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar al estudiante", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * obtiene una lista de todos los estudiantes con su información.
     * 
     * @return List con todos los estudiantes registrados, nulo si es que no
     * hay estudiantes.
     * @throws DAOException 
     */

    @Override
    public List<Student> getAllStudent() throws DAOException {
        List<Student> listOfStudent = new ArrayList<>();
        try {
            String query = "SELECT Nombre, Matricula FROM  proyecto.usuario INNER JOIN  proyecto.estudiante "
                    + "ON proyecto.usuario.Correo = proyecto.estudiante.correo";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setName(result.getString("Nombre"));
                student.setEnrollment(result.getString("Matricula"));
                listOfStudent.add(student);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfStudent;
    }
    
    /**
     * actualiza la información de un estudiante mediante su matricula.
     * 
     * @param student objeto de tipo estudiante.
     * @param enrollment matricula de un estudiante.
     * @return 1 si la actualización de la información del estudiante fue exitosa, 0 si
     * esto no fue posible.
     * @throws DAOException 
     */

    @Override
    public int modifyStudent(Student student, String enrollment) throws DAOException {
        int result;
        try {
            String query = "UPDATE proyecto.estudiante SET Matricula = ?, NRC_Curso = ? WHERE  Matricula = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, student.getEnrollment());
            statement.setString(2, student.getNrcCourse());
            statement.setString(3, enrollment);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible actualizar los datos del estudiante", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * obtiene en una lista el nombre de los estudiantes que no tienen un trabajo recepcional asignado.
     * 
     * @return List con los nombres de los estudiantes, null si no hay estudiantes
     * faltantes por asignar.
     * @throws DAOException 
     */

    @Override
    public List<Student> getStudentsWithNotReceptionalWork() throws DAOException {
        List<Student> listOfStudent = new ArrayList<>();
        try {
            String query = "SELECT Nombre FROM  proyecto.usuario INNER JOIN  proyecto.estudiante "
                    + "ON proyecto.usuario.Correo = proyecto.estudiante.correo";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setName(result.getString("Nombre"));
                listOfStudent.add(student);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfStudent;
    }

    /**
     * regresa una lista de los estudiantes que no se encuentran asignados a un 
     * anteproyecto.
     * 
     * @return List con los estudiantes faltantes por asignar, null si 
     * es que no hay estudiantes o ya fueron todos asignados.
     * @throws DAOException 
     */
    
    @Override
    public List<Student> getStudentForAssign() throws DAOException {
        List<Student> listOfStudent = new ArrayList<>();
        try {
            String query = "select u.Nombre, e.Matricula from proyecto.estudiante e inner join proyecto.usuario u "
                    + "on e.correo = u.correo where e.ID_anteproyecto is null";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setName(result.getString("Nombre"));
                student.setEnrollment(result.getString("Matricula"));
                listOfStudent.add(student);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener los estudiantes en espera de anteproyecto", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfStudent;
    }

    /**
     * asigna un anteproyecto previamente registrado a un estudiante.
     * 
     * @param idPreliminaryProyect id del anteproyecto
     * @param idReceptionalWork id del trabajo recepcional.
     * @param enrollment matricula del estudiante.
     * @return 1 si la asignación del anteproyecto fue exitoso, 0 si esto no fue así.
     * @throws DAOException 
     */
    
    @Override
    public int assignPreliminaryProjectStudent(int idPreliminaryProyect, int idReceptionalWork, String enrollment) throws DAOException {
        int result;
        try {
            String query = "UPDATE proyecto.estudiante SET ID_anteproyecto = ?, ID_TrabajoRecepcional = ? "
                    + "WHERE Matricula = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idPreliminaryProyect);
            statement.setInt(2, idReceptionalWork);
            statement.setString(3, enrollment);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible asignar el anteproyecto al estudiante", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }

    /**
     * regresa una lista de los estudiantes de un curso, esto pasandole 
     * el número de personal del profesor que tenga el curso.
     * 
     * @param personalNumber número de personal del profesor.
     * @return List con los estudiantes de un curso, null si no hay estudiantes
     * asignados a algún profesor.
     * @throws DAOException 
     */
    
    @Override
    public List<Student> getStudentsFromCourse(int personalNumber) throws DAOException {
        List<Student> listOfStudent = new ArrayList<>();
        try {
            String query = "SELECT u.Nombre, e.Matricula, e.NRC_Curso FROM proyecto.estudiante e "
                    + "inner join proyecto.usuario u on u.correo = e.correo "
                    + "inner join proyecto.curso c on e.NRC_Curso = c.NRC where c.Num_personal = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, personalNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setName(result.getString("Nombre"));
                student.setEnrollment(result.getString("Matricula"));
                student.setNrcCourse(result.getString("NRC_Curso"));
                listOfStudent.add(student);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener los estudiantes del curso", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfStudent;
    }

    /**
     * obtiene una lista de los estudiantes (solo su matricula) que tienen
     * un anteproyecto.
     * 
     * @param directorNumber número de personal del director.
     * @return List con las matrículas de los estudiantes que tienen
     * un anteproyecto, null si esto no se cumple.
     * @throws DAOException 
     */
    
    @Override
    public List<Student> getEnrollmentPerProject(int directorNumber) throws DAOException {
        List<Student> listOfEnrollment = new ArrayList<>();
        try {
            String query = "select e.matricula from proyecto.estudiante e inner join proyecto.anteproyecto tr on "
                    + "tr.ID_Anteproyecto = e.ID_anteproyecto where tr.N_personalDirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, directorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setEnrollment(result.getString("Matricula"));
                listOfEnrollment.add(student);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de matriculas para el director", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfEnrollment;
    }
    
    /**
     * obtiene la matrícula de todos los estudiantes que tengan un anteproyecto asignado
     * mediante el número de personal del codirector.
     *  
     * @param codirectorNumber número de personal del codirector.
     * @return List con las matriculas de los estudiantes que tienen un anteproyecto,
     * de un codirector en especifico, nulo si esto no se cumple.
     * @throws DAOException 
     */

    @Override
    public List<Student> getEnrollmentPerProjectCodirector(int codirectorNumber) throws DAOException {
        List<Student> listOfEnrollment = new ArrayList<>();
        try {
            String query = "select e.matricula from proyecto.estudiante e inner join proyecto.anteproyecto tr on "
                    + "tr.ID_Anteproyecto = e.ID_anteproyecto where tr.N_personalCodirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codirectorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setEnrollment(result.getString("Matricula"));
                listOfEnrollment.add(student);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de matriculas para el director", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfEnrollment;
    }

    /**
     * regresa una lista de las matriculas de los estudiantes que esten en un curso
     * en especifico de un profesor.
     * 
     * @param personalNumber número de personal de profesor.
     * @return List con las matriculas de los estudiantes de un curso, null
     * si el curso no tiene estudiantes asignados. 
     * @throws DAOException 
     */
    
    @Override
    public List<Student> getEnrollmentPerCourse(int personalNumber) throws DAOException {
        List<Student> listOfEnrollment = new ArrayList<>();
        try {
            String query = "select e.Matricula from proyecto.estudiante e inner join proyecto.curso c on "
                    + "e.NRC_Curso = c.NRC where c.Num_personal = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, personalNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student();
                student.setEnrollment(result.getString("Matricula"));
                listOfEnrollment.add(student);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de matriculas para el profesor", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfEnrollment;
    }
    
    /**
     * obtiene la matricula de un estudiante mediante su email.
     * 
     * @param email correo electronico de un estudiante.
     * @return regresa la matricula de un estudiante, null si el correo pasado
     * no corresponde a un estudiante valido.
     * @throws DAOException 
     */

    @Override
    public String getEnrollmentStudent(String email) throws DAOException {
        String emailStudent = null;
        try {
            String query = "SELECT Matricula FROM proyecto.estudiante Where correo = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                emailStudent = result.getString("Matricula");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return emailStudent;
    }

    /**
     * obtiene la información de un estudiante mediante el email que se le pase.
     * 
     * @param email correo electronico de un estudiante.
     * @return objeto de tipo estudiante con la información de este. null si el email
     * pasado no corresponde a un estudiante valido.
     * @throws DAOException 
     */
    
    @Override
    public Student getInformation(String email) throws DAOException {
        Student student = new Student();
        try {
            String query = "Select proyecto.experienciaeducativa.materia, proyecto.Estudiante.matricula, "
                    + "proyecto.usuario.Nombre from proyecto.experienciaeducativa inner join proyecto.curso "
                    + "on proyecto.curso.Experiencia_Educativa = proyecto.experienciaeducativa.ID_EE inner Join "
                    + "proyecto.estudiante on proyecto.estudiante.NRC_Curso = proyecto.curso.NRC inner join "
                    + "proyecto.usuario on proyecto.usuario.correo = proyecto.estudiante.correo where "
                    + "proyecto.estudiante.correo = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                student.setName(result.getString("Nombre"));
                student.setEducativeExperience(result.getString("Materia"));
                student.setEnrollment(result.getString("matricula"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la info del estudiante ", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return student;
    }

    /**
     * obtiene la información de un estudiante para así poder modificarla. 
     * 
     * @param enrollment matricula de un estudiante.
     * @return objeto de tipo estudiante con la información de un estudiante, null
     * si la matricula pasada no corresponde un estudiante valido.
     * @throws DAOException 
     */
    
    @Override
    public Student getInfoToModifyStudent(String enrollment) throws DAOException {
        Student student = new Student();
        try {
            String query = "select u.nombre, e.Matricula, e.NRC_Curso, u.Habilitado, e.correo from proyecto.estudiante e inner join proyecto.usuario u on u.correo = e.correo where e.Matricula = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, enrollment);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                student.setName(result.getString("Nombre"));
                student.setEnrollment(result.getString("Matricula"));
                student.setNrcCourse(result.getString("NRC_Curso"));
                student.setEnable(result.getInt("Habilitado"));
                student.setEmail(result.getString("correo"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible recuperar la info del estudiante a modificar", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return student;
    }
}
