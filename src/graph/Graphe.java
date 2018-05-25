/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author riwan
 */
public class Graphe {
    protected List<Sommet> sommets;
    protected List<Arete> aretes;
    private int size=0;
    
    
    public Graphe(List<Sommet> sommets, List<Arete> arrets) {
        this.sommets = sommets;
        this.aretes = arrets;
    }
    
    public Graphe(){
        sommets=new LinkedList<Sommet>();
        aretes=new LinkedList<Arete>();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    public List<Sommet> getSommets() {
        return sommets;
    }

    public void setSommets(List<Sommet> sommets) {
        this.sommets = sommets;
    }

    public List<Arete> getAretes() {
        return aretes;
    }

    public void setAretes(List<Arete> aretes) {
        this.aretes = aretes;
    }
    //code permettant d'ajouter un sommet
    public void ajouterSommet(Sommet s){
        size++;
        sommets.add(s);   
    }
    //code permettant d'ajouter une Arrete
    public boolean ajouterArete(Arete a){
        return aretes.add(a);
    }
    //Parcours la liste d'aretes afin de trouver tous les sommets reliés au 
    //sommet s passé en parametre, renvoie la collection de ces sommets
    public Collection<Sommet> getSommetsEnRelation(Sommet s){
        Collection<Sommet> col=new LinkedList<Sommet>();
        Iterator<Arete> it=aretes.iterator();
        while(it.hasNext()){
            Arete a=it.next();
            if(s.equals(a.getSommet1())) col.add(a.getSommet2());
            else if(s.equals(a.getSommet2())) col.add(a.getSommet1());
        }
        return col;
    }
    //calcul le degree du sommet
    public int degree(Sommet s){
        int degree=this.getSommetsEnRelation(s).size();
        s.setDegree(degree);
        return degree;
    }
    //expliquer choix, balaye parametre sur methodologie, grand jeu experience , tps calcul, different jeu data(ordre sommet,...),
    //trouver solution optimal, utiliser tas fibo pour dsat pour 
    //accelerer ou idees equivalentes, resultats exp, avis personnel sur solution
    @Override
    public String toString(){
        String tmp="";
        for(int i=0;i<sommets.size();i++){
            tmp+=sommets.get(i);
        }
        return tmp;

    }
    
    
    
}
