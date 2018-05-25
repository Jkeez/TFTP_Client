/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;


import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GrapheDSat extends Graphe implements Comparator<Sommet>{
    
    private static List<Sommet> grapheSommetNonColorie;
    
    public GrapheDSat(){
        this.sommets=new LinkedList<Sommet>();
    }
    //recuperer le dSat du sommet s
    public int dSat(Sommet s){
        
        if(s==null) return -1;
        
        Iterator<Sommet> it= getSommetsEnRelation(s).iterator();
        //HashSet permet de ne pas avoir de doublons
        HashSet<Integer> couleurDifferente=new HashSet<Integer>();
        
        while(it.hasNext()){
            Sommet voisin=it.next();
            if(s.getCouleur()!=0) 
                couleurDifferente.add(voisin.getCouleur());
        }
        // si aucun de ces voisin n'est coloriés , alors Dsat= degré
        if(couleurDifferente.size()==0) 
            return degree(s);
        
        else //dsat=nb de couleurs différente 
            return couleurDifferente.size();
    }
    
    //rechercher si une couleur est prise par un voisin
    private boolean verifCouleur(Collection<Sommet> c, int x){    
        Iterator<Sommet> it=c.iterator();
        //tant qu'il y a un sommet
        while(it.hasNext())
            //si la couleur du commet == la couleur passé en paramètre
            if(it.next().getCouleur()==x)
                //elle n'est pas disponible
                return false;
        //si la couleur passé en paramètre n'est pas présente chez les voisins alors la couleur est dispo
        //on renvoie true
        return true;
    }
    
    //chercher la plus petite couleur disponible pour un sommet selon ces voisins.
    private int plusPetiteCouleur(Collection<Sommet> voisins){
        int i=1;
        while(true){
            if(verifCouleur(voisins,i)){
                return i;
            }
            i++;
        }
    }
    
    
    
    public void colorier(){
        //premier étape: Ordonner les sommets par ordre décroissant de degrés.
        Collections.sort(sommets,this);
        //deuxieme étape: recuperer tout les sommets dans une liste
        grapheSommetNonColorie=new LinkedList<Sommet>();
        grapheSommetNonColorie.addAll(sommets);
        //colorier le sommet ayant le plus grand degrés avec la première couleur
        Sommet s=grapheSommetNonColorie.get(0);
        //on colorie avec la plus petite couleur
        s.setCouleur(1);
        //une fois colorié on le retire.
        grapheSommetNonColorie.remove(0);
        
        //on boucle pour réaliser ce processus jusqu'a ce que tout les sommets soit coloriés
        Collections.sort(grapheSommetNonColorie,this);
        while(grapheSommetNonColorie.size()>0){
            s=grapheSommetNonColorie.get(0);
            Collection<Sommet> col=getSommetsEnRelation(s);
            int ppc=plusPetiteCouleur(col);
            s.setCouleur(ppc);
            grapheSommetNonColorie.remove(0);
            Collections.sort(grapheSommetNonColorie,this);
        }
    }
    
    
    //compare deux objets selon leurs degrés. Le max sera mit en priorité.
    public int compare(Sommet o1, Sommet o2) {
        if(dSat(o2)-dSat(o1)!=0)
            return dSat(o2)-dSat(o1);
        else 
            return this.degree(o2)-this.degree(o1);
    }
    
    
    public static void main(String[] args){
        GrapheDSat g=new GrapheDSat();
        g.ajouterSommet(new Sommet(1, 0));
        g.ajouterSommet(new Sommet(2, 0));
        g.ajouterSommet(new Sommet(3, 0));
        g.ajouterArete(new Arete(g.sommets.get(0),g.sommets.get(1) ));
        g.ajouterArete(new Arete(g.sommets.get(2),g.sommets.get(0) ));
        g.ajouterArete(new Arete(g.sommets.get(1),g.sommets.get(2) ));
        g.colorier();
        System.out.print(g.toString());
    }
}