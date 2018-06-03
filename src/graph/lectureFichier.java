package graph;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;

public class lectureFichier {

    private String nomFichier;
    private boolean estOriente;
    private int nombreSommets;
    private int nombreValSommet;
    private int nombreArcs;
    private int nombreValArc;
    private ArrayList<Sommet> listeSommets;
    private ArrayList<Arete> listeAretes;

    lectureFichier() throws IOException {
        listeSommets = new ArrayList<>();
        listeAretes = new ArrayList<>();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(new Component() {
        });
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Fichier choisi: " + selectedFile.getAbsolutePath());
            lireFichier(selectedFile);
            //afficherFichier();
        }
    }

    public void lireFichier(File f) throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {//lit le fichier ligne par ligne
            String line;
            boolean lectureArete = false;
            int NumeroSommetDepart;
            int NumeroSommetArrive;
            Sommet sommetDepart;
            Sommet sommetArrive;
            while ((line = br.readLine()) != null) {
                if (lectureArete) {
                    int debut = 0;
                    int fin = line.indexOf(" ");
                    NumeroSommetDepart=Integer.parseInt(line.substring(debut, fin).trim());
                    NumeroSommetArrive=Integer.parseInt(line.substring(fin, line.length()).trim());
                    sommetDepart=listeSommets.get(NumeroSommetDepart);
                    sommetArrive=listeSommets.get(NumeroSommetArrive);
                    listeAretes.add(new Arete(sommetDepart,sommetArrive));
                } else {
                    if (line.contains("Nom:")) {//lecture du nom du fichier
                        int debut = line.indexOf(":");
                        int fin = line.length();
                        setNomFichier((line.substring(debut + 1, fin)).trim());//recupere le nom du fichier
                    }
                    if (line.contains("Oriente")) {//lecture du type de graph etudie
                        int debut = line.indexOf(":");
                        int fin = line.length();
                        line = (line.substring(debut + 1, fin)).trim();
                        if (line.equals("non")) {
                            setEstOriente(false);
                        } else {
                            setEstOriente(true);
                        }
                    }
                    if (line.contains("NbSommets:")) {//lecture du nombre de sommets
                        int debut = line.indexOf(":");
                        int fin = line.length();
                        line = (line.substring(debut + 1, fin)).trim();
                        setNombreSommets(Integer.parseInt(line));
                    }
                    if (line.contains("NbValSommet:")) {//lecture du NbValSommet
                        int debut = line.indexOf(":");
                        int fin = line.length();
                        line = (line.substring(debut + 1, fin)).trim();
                        setNombreValSommet(Integer.parseInt(line));
                    }
                    if (line.contains("NbArcs:")) {//lecture du nombre d'arcs
                        int debut = line.indexOf(":");
                        int fin = line.length();
                        line = (line.substring(debut + 1, fin)).trim();
                        setNombreArcs(Integer.parseInt(line));
                    }
                    if (line.contains("NbValArc:")) {//lecture du NbValArc
                        int debut = line.indexOf(":");
                        int fin = line.length();
                        line = (line.substring(debut + 1, fin)).trim();
                        setNombreValArc(Integer.parseInt(line));
                    }
                    if (line.contains("id")) {//lecture des sommets
                        int debut = 1;
                        int fin = line.indexOf("i");
                        line = (line.substring(debut - 1, fin)).trim();
                        listeSommets.add(new Sommet(Integer.parseInt(line), 0));
                    }
                }
                if (line.contains("aretes")) {
                    lectureArete = true;
                }

            }
        }

    }

    void afficherFichier() {
        System.out.println("++++++++++ Informations Fichier ++++++++++ ");
        System.out.println("Nom: " + getNomFichier());
        System.out.println("Est oriente: " + getEstOriente());
        System.out.println("Nombre de sommets: " + getNombreSommets());
        System.out.println("Nombre Val Sommet: " + getNombreValSommet());
        System.out.println("Nombre d'arcs: " + getNombreArcs());
        System.out.println("Nombre Val Arc: " + getNombreValArc());
        System.out.println("++++++++++ Liste des sommets ++++++++++");
        for (int i = 0; i <= listeSommets.size() - 1; i++) {
            System.out.println("Sommet: " + listeSommets.get(i).getNumero());
        }
        System.out.println("++++++++++ Liste des aretes ++++++++++");
        for (int i = 0; i <= listeAretes.size() - 1; i++) {
            System.out.println("Arete de: " + listeAretes.get(i).getSommet1().
                    getNumero()+" vers "+listeAretes.get(i).getSommet2().
                    getNumero());
        }
        
    }

    
    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public boolean getEstOriente() {
        return estOriente;
    }

    public void setEstOriente(boolean estOriente) {
        this.estOriente = estOriente;
    }

    public int getNombreSommets() {
        return nombreSommets;
    }

    public void setNombreSommets(int nombreSommets) {
        this.nombreSommets = nombreSommets;
    }

    public int getNombreValSommet() {
        return nombreValSommet;
    }

    public void setNombreValSommet(int nombreValSommet) {
        this.nombreValSommet = nombreValSommet;
    }

    public int getNombreArcs() {
        return nombreArcs;
    }

    public void setNombreArcs(int nombreArcs) {
        this.nombreArcs = nombreArcs;
    }

    public int getNombreValArc() {
        return nombreValArc;
    }

    public void setNombreValArc(int nombreValArc) {
        this.nombreValArc = nombreValArc;
    }

    public ArrayList<Sommet> getListeSommets() {
        return listeSommets;
    }

    public void setListeSommets(ArrayList<Sommet> listeSommets) {
        this.listeSommets = listeSommets;
    }

    public ArrayList<Arete> getListeAretes() {
        return listeAretes;
    }

    public void setListeAretes(ArrayList<Arete> listeAretes) {
        this.listeAretes = listeAretes;
    }

}
