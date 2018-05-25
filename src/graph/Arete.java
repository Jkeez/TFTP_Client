/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.Objects;

/**
 *
 * @author riwan
 */
public class Arete {
    private Sommet Sommet1;
    private Sommet Sommet2;

    public Arete(Sommet Sommet1, Sommet Sommet2) {
        this.Sommet1 = Sommet1;
        this.Sommet2 = Sommet2;
    }

    public Sommet getSommet1() {
        return Sommet1;
    }

    public void setSommet1(Sommet Sommet1) {
        this.Sommet1 = Sommet1;
    }

    public Sommet getSommet2() {
        return Sommet2;
    }

    public void setSommet2(Sommet Sommet2) {
        this.Sommet2 = Sommet2;
    }

    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Arete other = (Arete) obj;
        if (!Objects.equals(this.Sommet1, other.Sommet1)) {
            return false;
        }
        if (!Objects.equals(this.Sommet2, other.Sommet2)) {
            return false;
        }
        return true;
    }
    
    
}
