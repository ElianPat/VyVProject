/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.util.Objects;

/**
 *
 * @author alexs
 */
public class Codirector extends Professor {

    private int codirectorNumber;

    public Codirector() {
        this.codirectorNumber = 0;
    }

    public int getCodirectorNumber() {
        return this.codirectorNumber;
    }

    public void setCodirectorNumber(int codirectorNumber) {
        this.codirectorNumber = codirectorNumber;
    }

    @Override
    public String toString() {
        return getName() + " ";
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Codirector) {
            Codirector other = (Codirector) object;
            return Objects.equals(this.getName(), other.getName())
                    && Objects.equals(this.getCodirectorNumber(), other.getCodirectorNumber());

        }
        return false;
    }
}
