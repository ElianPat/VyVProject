/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

/**
 *
 * @author alexs
 */
public class DAOException extends Exception {

    private final Status status;

    public DAOException(String message, Status status) {
        super(message);
        this.status = status;
    }
    
    public Status getStatus() {
        return status;
    }
}
