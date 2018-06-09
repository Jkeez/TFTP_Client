/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;


import java.io.IOException;
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
            if(voisin.getCouleur()!=0) {
                couleurDifferente.add(voisin.getCouleur());
            }
                
        }
        if(s.getNumero()==0){
            
            for(int i=0;i<=couleurDifferente.size()-1;i++)System.out.println(couleurDifferente.iterator().next().toString());
        }
        // si aucun de ces voisin n'est coloriés , alors Dsat= degré
        if(couleurDifferente.size()==0){ 
            s.setDegreeSaturee(s.getDegree());
            return degree(s);
        }
        
        else //dsat=nb de couleurs différente
            s.setDegreeSaturee(couleurDifferente.size());
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
        //Collections.sort(sommets,this);
        sommets.sort(Comparator.comparing(Sommet::getDegree).reversed());
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
        //grapheSommetNonColorie.sort(Comparator.comparing(Sommet::getDegreeSaturee).reversed());
        majDSat(grapheSommetNonColorie);
        Collections.sort(grapheSommetNonColorie, this);
        
        
        while(grapheSommetNonColorie.size()>0){
            s=grapheSommetNonColorie.get(0);
            Collection<Sommet> col=getSommetsEnRelation(s);
            int ppc=plusPetiteCouleur(col);
            s.setCouleur(ppc);
            grapheSommetNonColorie.remove(0);
            majDSat(grapheSommetNonColorie);
            grapheSommetNonColorie.sort(Comparator.comparing(Sommet::getDegreeSaturee).reversed());
        }
    }
    
    public void majDSat(List<Sommet> som){
         for (int i = 0; i <= som.size()-1; i++) {
           som.get(i).setDegreeSaturee(dSat(som.get(i)));
        }
    }
    //compare deux objets selon leurs degrés. Le max sera mit en priorité.
    public int compare(Sommet o1, Sommet o2) {
        if(o2.getDegreeSaturee()<o1.getDegreeSaturee())
            
            return -1;
        else if(o1.getDegreeSaturee()<o2.getDegreeSaturee())return 1;
        else{
        } 
            return -1 * Integer.compare(o1.getDegree(), o2.getDegree());
    }
    
    
    public static void main(String[] args) throws IOException{
        lectureFichier lecteur= new lectureFichier();
        
        GrapheDSat g=new GrapheDSat();
        for (int i = 0; i <= lecteur.getListeSommets().size()-1; i++) {
            g.ajouterSommet(lecteur.getListeSommets().get(i));
        }
        for (int i = 0; i <= lecteur.getListeAretes().size() - 1; i++) {
            g.ajouterArete(lecteur.getListeAretes().get(i));
        }
        //calcul des degrees de chaque sommets et du degree saturee
        for (int i = 0; i <= g.getSommets().size()-1; i++) {
            g.degree(g.getSommets().get(i));
            g.getSommets().get(i).setDegreeSaturee(g.dSat(g.getSommets().get(i)));
        }
      
        g.colorier();
        System.out.print(g.toString());

    }
}
