/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

/**
 *
 * @author alexs
 */
public class Director extends Professor {

    private int directorNumber;

    public int getDirectorNumber() {
        return directorNumber;
    }

    public void setDirectorNumber(int directorNumber) {
        this.directorNumber = directorNumber;
    }

    @Override
    public String toString() {
        return getDirectorNumber() + " " + getName();
    }
}
