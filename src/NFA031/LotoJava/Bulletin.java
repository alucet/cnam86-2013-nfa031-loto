package NFA031.LotoJava;

import java.util.Random;

/**
 * Classe représentant un bulletin de jeu de Loto.
 * @author Aurélie Lucet <aurelie.lucet at gmail.com>
 */
public class Bulletin {
    /* TODO Ajouter identifiant */
    
    /** Nombre minimum de grilles par bulletin. */
    public final static byte NB_MIN_GRILLES = 1;
    /** Nombre maximum de grilles par bulletin. */
    public final static byte NB_MAX_GRILLES = 6;
    
    /** Identifiant. */
    private int id = 0;
    /** Tableau interne des grilles du bulletin. */
    private Grille grilles[];
    /** Grille gagnante. */
    private Grille gagnante;
    /** Mise totale des grilles */
    private short totalMises;
    
    /** Constructeur par défaut: création d'un bulletin complet. */
    public Bulletin () {
        this( 0, Bulletin.NB_MAX_GRILLES, new Grille() );
    }
    
    /**
     * Constructeur: création d'un bulletin avec un nombre défini de grilles.
     * @param id L'identifiant à donner au bulletin.
     * @param nbGrilles Nombre de grilles du bulletin à créer. 
     *                  Valeur entre Bulletin.minGrilles et Bulletin.maxGrilles.
     * @param gagnante Une grille gagnante à fournir pour les calculs de gain.
     */
    public Bulletin ( int id,  byte nbGrilles, Grille gagnante ) {
        if ( id > 0 ) {
            this.id = id;
        }
        this.gagnante = gagnante;
        if ( nbGrilles <= 0 ) {  // Bulletin random
            this.create( (byte) 0 );
        } else {
            this.create(nbGrilles);
        }
    }
    
    /**
     * Création d'un bulletin.
     * @param nbGrilles Nombre de grilles à créer dans le bulletin.
     * @throws IndexOutOfBoundsException Si le nombre de grilles désiré est hors limites.
     */
    private void create ( byte nbGrilles ) {
        boolean aleatoire = false;
        
        // Si le bulletin doit être généré aléatoirement
        if ( nbGrilles <= 0 ) {
            aleatoire = true;
            Random r = new Random();
            do {
                nbGrilles = (byte) r.nextInt(Bulletin.NB_MAX_GRILLES + 1);
            } while ( nbGrilles < Bulletin.NB_MIN_GRILLES );
        }
        
        // Vérification du nombre de grilles à créer
        if ( nbGrilles <= Bulletin.NB_MAX_GRILLES && nbGrilles >= Bulletin.NB_MIN_GRILLES ) {
            // Initialisation du tableau de grilles
            this.grilles = new Grille[nbGrilles];
            this.totalMises = 0;
            byte i, n = Grille.NB_NUMEROS_MIN, nc = Grille.CHANCE_NB_NUMEROS_MIN;
            
            // Création des nouveaux objets Grille
            for ( i = 0; i < this.grilles.length; i++ ) {
                if ( aleatoire ) {
                    // Création d'une grille aléatoire
                    n = Grille.getRandomNbNumeros();
                    nc = Grille.getRandomNbNumerosChance(n);
                }
                this.grilles[i] = new Grille( n, nc );
                if ( this.gagnante != null ) {
                    this.grilles[i].setGrilleGagnante(this.gagnante);
                }
                this.totalMises += this.grilles[i].getMise();
            }
        } else { // Exception si le nombre de grilles à créer est non valide
            throw new IndexOutOfBoundsException("Le nombre de grilles à créer (" + nbGrilles + ") "
                    + "est hors limites (de " + Bulletin.NB_MIN_GRILLES + " à " + Bulletin.NB_MAX_GRILLES + ")");
        }
    }
    
    /** Accesseur compteur (identifiant du bulletin) */
    public int getId () {
        return this.id;
    }
    
    /**
     * Retourne le nombre de grilles utilisées dans le bulletin.
     * @return Le nombre de grilles utilisées dans le bulletin.
     */
    public byte getNombreGrilles() {
        return (byte) this.grilles.length;
    }
    
    /**
     * Renvoie les grilles du bulletin sous forme de tableau d'objets.
     * @return Le tableau des grilles du bulletin.
     */
    public Grille[] getGrilles() {
        return this.grilles;
    }
    
    /**
     * Accesseur pour le total des mises des grilles du bulletin.
     * @return Le total des mises pour tout le bulletin.
     */
    public short getTotalMises() {
        return this.totalMises;
    }
    
    /**
     * Retourne le bulletin sous forme de chaîne.
     * @return La chaîne de caractères du contenu du bulletin.
     */
    @Override
    public String toString () {
        String retour = "======================= Grille n°" + this.getId() + " =======================\n";
        byte i, nbNumerosGagnants, nbNumerosChanceGagnants, rang;
        
        for ( i = 0; i < this.grilles.length; i++ ) {
            retour += "Grille n°" + (i+1) + ": " + this.grilles[i];
            nbNumerosGagnants = this.grilles[i].getNbNumerosGagnants();
            nbNumerosChanceGagnants = this.grilles[i].getNbNumerosChanceGagnants();
            if ( nbNumerosGagnants > 0 ) {
                retour += "-> " + nbNumerosGagnants + " n°";
            }
            if ( nbNumerosChanceGagnants > 0 ) {
                if ( nbNumerosGagnants > 0 ) {
                    retour += " & ";
                }
                retour += nbNumerosChanceGagnants + " n° chance";
            }
            rang = Grille.calculRangGagnant(nbNumerosGagnants, nbNumerosChanceGagnants);
            if ( rang > 0 ) {
                retour += " -> gain de rang " + rang;
            }
            retour += "\n";
        }
        retour += "-----------------------Total: " + this.totalMises + "E-------------------------\n\n";
        return retour;
    }

}
