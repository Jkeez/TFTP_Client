/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

/**
 *
 * @author riwan
 */
public class Sommet {

    private int numero;
    private int couleur;
    private int degree;

    public Sommet(int numero, int couleur) {
        this.setNumero(numero);
        this.setCouleur(couleur);
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getCouleur() {
        return couleur;
    }

    public void setCouleur(int couleur) {
        this.couleur = couleur;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    //permet de déterminer si deux sommets sont différents
    @Override
    public boolean equals(Object sommet) {
        if (sommet == null) {
            return false;
        }
        if (getClass() != sommet.getClass()) {
            return false;
        }
        final Sommet other = (Sommet) sommet;
        return this.numero == other.numero;
    }

    //Mise en forme classe couleur
    @Override
    public String toString() {
        String tmp = "------------\n";
        tmp += "Numéro: " + this.numero + "\n";
        tmp += "Couleur: " + this.couleur + "\n";
        return tmp;
    }

}
