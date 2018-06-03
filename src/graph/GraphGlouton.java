/*
Greedy coloring
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

public class GraphGlouton extends Graphe implements Comparator<Sommet>{
    
    private static List<Sommet> grapheSommetNonColorie;
    
    public GraphGlouton(){
        this.sommets=new LinkedList<Sommet>();
    }
    
    
    //rechercher si une couleur est prise par un voisin
    private boolean verifCouleur(Collection<Sommet> c, int x){    
        Iterator<Sommet> it=c.iterator();
        //tant qu'il y a un sommet
        while(it.hasNext())
            //si la couleur du sommet == la couleur passé en paramètre
            if(it.next().getCouleur()==x)
                //elle n'est pas disponible
                return false;
        //si la couleur passé en paramètre n'est pas présente chez les voisins alors la couleur est dispo
        //on renvoie true
        return true;
    }
    
    //chercher la plus petite couleur disponible pour un sommet selon une collection de voisins
    private int plusPetiteCouleur(Collection<Sommet> voisins){
        int i=1;
        while(true){
            if(verifCouleur(voisins,i)){
                return i;//couleur i disponible
            }
            i++;
        }
    }
    
    
    
    public void colorier(){
        //premier étape: Ordonner les sommets par leur numero
        sommets.sort(Comparator.comparing(Sommet::getDegree).reversed());
        
        
        //deuxieme étape: recuperer tout les sommets dans une liste
        grapheSommetNonColorie=new LinkedList<Sommet>();
        grapheSommetNonColorie.addAll(sommets);
        //colorier le premier sommet
        Sommet s=grapheSommetNonColorie.get(0);
        //on colorie avec la plus petite couleur
        s.setCouleur(1);
        //une fois colorié on le retire.
        grapheSommetNonColorie.remove(0);
        
        //on boucle pour réaliser ce processus jusqu'a ce que tout les sommets soit coloriés
        while(grapheSommetNonColorie.size()>0){
            s=grapheSommetNonColorie.get(0);
            Collection<Sommet> sommetVoisins=getSommetsEnRelation(s);//collection des sommets adjacents
            int ppc=plusPetiteCouleur(sommetVoisins);
            s.setCouleur(ppc);
            grapheSommetNonColorie.remove(0);
        }
    }
    
    
   
    
    
    public static void main(String[] args) throws IOException{
        lectureFichier lecteur= new lectureFichier();
        System.out.println("Lancement greedy coloring");
        GraphGlouton g=new GraphGlouton();
        //ajoute les sommets au graph
        for (int i = 0; i <= lecteur.getListeSommets().size()-1; i++) {
            g.ajouterSommet(lecteur.getListeSommets().get(i));
        }
        //ajoute les aretes au graph
        for (int i = 0; i <= lecteur.getListeAretes().size() - 1; i++) {
            g.ajouterArete(lecteur.getListeAretes().get(i));
        }
        //calcul des degrees de chaque sommets
        for (int i = 0; i <= g.getSommets().size()-1; i++) {
            g.degree(g.getSommets().get(i));
        }
        
        g.colorier();
        System.out.print(g.toString());

    }

    @Override
    public int compare(Sommet o1, Sommet o2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
