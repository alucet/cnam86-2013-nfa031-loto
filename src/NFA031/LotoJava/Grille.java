package NFA031.LotoJava;

import java.util.Random;

/**
 * Classe représentant une grille classique de jeu de Loto.
 * @author Aurélie Lucet <aurelie.lucet at gmail.com>
 */
public class Grille {
    
    /** Premier numéro d'une grille. */
    public final static byte NUMERO_MIN = 1;
    /** Dernier numéro d'une grille. */
    public final static byte NUMERO_MAX = 49;
    /** Nombre de numéros minimum à cocher pour la grille. */
    public final static byte NB_NUMEROS_MIN = 5;
    /** Nombre de numéros maximum à cocher pour la grille. */
    public final static byte NB_NUMEROS_MAX = 9;
    /** Premier numéro d'une grille chance. */
    public final static byte CHANCE_NUMERO_MIN = 1;
    /** Dernier numéro d'une grille chance. */
    public final static byte CHANCE_NUMERO_MAX = 10;
    /** Nombre de numéros minimum à cocher pour la grille chance. */
    public final static byte CHANCE_NB_NUMEROS_MIN = 1;
    /** Nombre de numéros maximum à cocher pour la grille chance. */
    public final static byte CHANCE_NB_NUMEROS_MAX = 10;
    /** Mise de base en euros, par grille. */
    public final static byte MISE_DE_BASE = 2;
    
    // Tableau interne des numéros cochés.
    private byte numerosChoisis[];
    // Tableau interne des numéros chance cochés.
    private byte numerosChanceChoisis[];
    // Mise de la grille
    private short miseGrille = 0;
    // Grille gagnante
    private Grille gagnante;
    // Nombre de numéros gagnants
    private byte nbNumerosGagnants = 0;
    // Nombre de numéros chance gagnants
    private byte nbNumerosChanceGagnants = 0;
    // Rang du bulletin
    private byte rang = 0;
    
    /** 
     * Constructeur: tirage des numéros à cocher pour la grille.
     * Par défaut: Grille.NB_NUMEROS_MIN & Grille.CHANCE_NB_NUMEROS_MIN
     */
    public Grille () {
        this( Grille.NB_NUMEROS_MIN, Grille.CHANCE_NB_NUMEROS_MIN );
    }
    
    /**
     * Constructeur: tirage des numéros à cocher pour la grille.
     * Par défaut: Grille.NB_NUMEROS_MIN.
     * @param nbNumerosChance Le nombre de numéros chance à tirer.
     */
    public Grille ( byte nbNumerosChance ) {
        this( Grille.NB_NUMEROS_MIN, nbNumerosChance );
    }
    
    /**
     * Constructeur: tirage des numéros à cocher pour la grille.
     * @param nbNumeros Le nombre de numéros à cocher.
     * @param nbNumerosChance Le nombre de numéros chance à cocher.
     * @throws IndexOutOfBoundsException Si les paramètres sont hors limites.
     */
    public Grille ( byte nbNumeros, byte nbNumerosChance ) {
        if ( nbNumeros >= Grille.NB_NUMEROS_MIN 
                && nbNumeros <= Grille.NB_NUMEROS_MAX ) {
            if ( nbNumerosChance >= Grille.CHANCE_NB_NUMEROS_MIN 
                    && nbNumerosChance <= Grille.CHANCE_NB_NUMEROS_MAX ) {
                this.numerosChoisis = new byte[nbNumeros];
                this.numerosChanceChoisis = new byte[nbNumerosChance];
                this.cocherGrille();
                this.calculMise();
            } else {
                throw new IndexOutOfBoundsException("Le nombre de numéros chance demandé ("
                        + nbNumerosChance + ") est hors limites (de " 
                        + Grille.CHANCE_NB_NUMEROS_MIN + " à " + Grille.CHANCE_NB_NUMEROS_MAX + ").");
            }
        } else {
            throw new IndexOutOfBoundsException("Le nombre de numéros demandé ("
                    + nbNumeros + ") est hors limites (de " 
                    + Grille.NB_NUMEROS_MIN + " à " + Grille.NB_NUMEROS_MAX + ").");
        }
    }
    
    /** Choix des numéros par random. */
    private void cocherGrille () {
        byte i, n;
        Random r = new Random();
        
        // RAZ
        this.numerosChoisis = new byte[this.numerosChoisis.length];
        this.numerosChanceChoisis = new byte[this.numerosChanceChoisis.length];
        
        // Cochage des numéros
        for ( i = 0; i < this.numerosChoisis.length; i++ ) {
            do {
                n = (byte) r.nextInt(Grille.NUMERO_MAX + 1); // Génère un entier de 0 (inclus) à max+1 (exclus)
            // Tant que 0 est tiré OU que le numéro tiré est déjà coché, on en génère un autre:
            } while ( n <= 0 || this.estCoche(n, false) );
            this.numerosChoisis[i] = n;
        }
        // Cochage des numéros chance
        for ( i = 0; i < this.numerosChanceChoisis.length; i++ ) {
            do {
                n = (byte) r.nextInt(Grille.CHANCE_NUMERO_MAX + 1); // Génère un entier de 0 (inclus) à max+1 (exclus)
            // Tant que 0 est tiré OU que le numéro tiré est déjà coché, on en génère un autre:
            } while ( n <= 0 || this.estCoche(n, true) );
            this.numerosChanceChoisis[i] = n;
        }
    }
    
    /** Trie le tableau des numéros cochés. */
    private void trierNumeros () {
        byte i, j, nb;
        /**
        * Tri par insertion:
        * On parcourt le tableau en insérant chaque élément [i] à sa place parmi les précédents.
        * On commence donc par le 2e élément, pour pouvoir le comparer au 1er.
        */
        // Tri des numéros
        for ( i = 1; i < this.numerosChoisis.length; i++ ) {
                nb = this.numerosChoisis[i];
                j = i; // Compteur descendant, à décrémenter au fur et à mesure qu'on remonte au début
                // Tant qu'on est pas au début du tableau
                // et que le nombre qu'on veut placer est inférieur au précédent, on décale
                while ( j >= 1 && this.numerosChoisis[j-1] > nb ) {
                        this.numerosChoisis[j] = this.numerosChoisis[j-1];
                        j--;
                }
                this.numerosChoisis[j] = nb;
        }
        // Tri des numéros chance
        for ( i = 1; i < this.numerosChanceChoisis.length; i++ ) {
                nb = this.numerosChanceChoisis[i];
                j = i;
                while ( j >= 1 && this.numerosChanceChoisis[j-1] > nb ) {
                        this.numerosChanceChoisis[j] = this.numerosChanceChoisis[j-1];
                        j--;
                }
                this.numerosChanceChoisis[j] = nb;
        }
		
    }
    
    /**
     * Retourne le tableau des numéros cochés de la grille.
     * @return Le tableau des numéros cochés de la grille.
     */
    public byte[] getNumeros() {
        return this.numerosChoisis;
    }
    
    /**
     * Retourne le tableau des numéros chance cochés de la grille.
     * @return Le tableau des numéros cochés de la grille.
     */
    public byte[] getNumerosChance() {
        return this.numerosChanceChoisis;
    }
    
    /**
     * Retourne la mise calculée de la grille.
     * @return La mise de la grille.
     */
    public short getMise() {
        return this.miseGrille;
    }
    
    /**
     * Recherche un numéro dans les grilles.
     * @param numero Le numéro dont on doit vérifier s'il est déjà coché.
     * @param chercherChance Indique si l'on doit chercher dans la grille
     *                          régulière (false), ou celle des numéros
     *                          chance (true).
     * @throws IndexOutOfBoundsException Si le numéro fourni est hors des limites des numéros cochables.
     */
    private boolean estCoche (int numero, boolean chercherChance) {
        boolean trouve = false;
        
        if ( !chercherChance ) {
            // Validation du numéro demandé, pour ne pas rechercher inutilement
            if ( numero <= Grille.NUMERO_MAX && numero >= Grille.NUMERO_MIN  ) {
                // Le numéro est valide, on le recherche
                byte i;
                for ( i = 0; i < this.numerosChoisis.length; i++ ) {
                    if ( numero == this.numerosChoisis[i] ) {
                        trouve = true;
                    }
                }
            } else {
                throw new IndexOutOfBoundsException("Le numero demande (" + numero + ")"
                        + " est hors des cases de la grille (de " + Grille.NUMERO_MIN 
                        + " a " + Grille.NUMERO_MAX + ").");
            }
        } else {
            // Validation du numéro demandé, pour ne pas rechercher inutilement
            if ( numero <= Grille.CHANCE_NUMERO_MAX && numero >= Grille.CHANCE_NUMERO_MIN  ) {
                // Le numéro est valide, on le recherche
                byte i;
                for ( i = 0; i < this.numerosChanceChoisis.length; i++ ) {
                    if ( numero == this.numerosChanceChoisis[i] ) {
                        trouve = true;
                    }
                }
            } else {
                throw new IndexOutOfBoundsException("Le numero chance demande (" + numero + ")"
                        + " est hors des cases de la grille (de " + Grille.CHANCE_NUMERO_MIN 
                        + " a " + Grille.CHANCE_NUMERO_MAX + ").");
            }
        }
        return trouve;
    }
    
    /**
     * Définit la grille gagnante du tirage associé.
     * @param gagnante La grille gagnante.
     * @throws NullPointerException Si l'objet fourni est vide.
     */
    public void setGrilleGagnante ( Grille gagnante ) {
        if ( gagnante != null ) {
            this.gagnante = gagnante;
            this.nbNumerosGagnants = 0;
            this.nbNumerosChanceGagnants = 0;
            calculerNbNumerosGagnants();
        } else {
            throw new NullPointerException("La grille gagnante fournie est nulle !");
        }
    }
    
    /**
     * Compare la grille gagnante à la grille en cours.
     * @throws IllegalArgumentException Si pas de grille gagnante fournie.
     */
    private void calculerNbNumerosGagnants ( ) {
        byte i, j;
        
        if ( this.gagnante != null ) {
            for ( i = 0; i < this.gagnante.getNumeros().length; i++ ) {   // Pour chaque numéro gagnant,
                for ( j = 0; j < this.numerosChoisis.length; j++ ) { // On parcourt cette grille à sa recherche
                    if ( this.numerosChoisis[j] == this.gagnante.getNumeros()[i] ) {
                        this.nbNumerosGagnants++;
                    }
                }
            }
            for ( i = 0; i < this.gagnante.getNumerosChance().length; i++ ) {   // Pour chaque numéro chance gagnant,
                for ( j = 0; j < this.numerosChanceChoisis.length; j++ ) { // On parcourt cette grille à sa recherche
                    if ( this.numerosChanceChoisis[j] == this.gagnante.getNumerosChance()[i] ) {
                        this.nbNumerosChanceGagnants++;
                    }
                }
            }
            this.rang = Grille.calculRangGagnant(this.nbNumerosGagnants, this.nbNumerosChanceGagnants);
        } else {
            throw new IllegalArgumentException("Aucune grille gagnante passée au comparateur.");
        }
    }
    
    /**
     * Retourne le nombre de bons numéros.
     * @return Le nombre de numéros gagnants.
     */
    public byte getNbNumerosGagnants() {
        return this.nbNumerosGagnants;
    }
    
    /**
     * Retourne le nombre de bons numéros chance.
     * @return Le nombre de numéros chance gagnants.
     */
    public byte getNbNumerosChanceGagnants() {
        return this.nbNumerosChanceGagnants;
    }
    
    /**
     * Calcul de la mise d'une grille:
     * nbNumerosChance * Nombre de combinaisons possibles * Mise pour une grille = total pour la grille
     */
    private void calculMise() {
        this.miseGrille = (short) ( this.numerosChanceChoisis.length 
                            * Grille.nbCombinaisonsPossibles( this.numerosChoisis.length, Grille.NB_NUMEROS_MIN) 
                            * Grille.MISE_DE_BASE);
    }
    
    /**
     * Calcul du nombre de combinaisons possibles.
     * @param n Total de nombres à disposition.
     * @param k Nombre de nombres à choisir.
     * @return Le nombre de combinaisons possibles.
     */
    public static long nbCombinaisonsPossibles ( int n, int k ) {
        /* Ckn = nombre de combinaisons possibles
        * n = nombre total de numéros
        * k = nombre de numéros par combinaison
        *   k
        * C   = (n!) / (k! * (n-k)!)
        *   n
        */
        return factorielle(n) / ( factorielle(k) * factorielle(n - k) );
    }
    
    /**
     * Retourne le numéro de rang de la grille.
     * @return Le numéro de rang de gain calculé de la grille. Retourne 0 si la grille est perdante
     *          ou si aucune grille gagnante n'a été fournie en référence.
     */
    public byte getRang() {
        return this.rang;
    }
    
    /**
     * Génère un nombre aléatoire de numéros à cocher.
     * @return Un nombre aléatoire de numéros à cocher.
     */
    public static byte getRandomNbNumeros () {
        Random r = new Random();
        byte n;
        do {
            n = (byte) r.nextInt(Grille.NB_NUMEROS_MAX + 1);
        } while ( n < Grille.NB_NUMEROS_MIN ); // Tant que le générateur sort un nombre trop petit, on recommence
        return n;
    }
    
    /**
     * Génère un nombre aléatoire de numéros chance à cocher selon le nombre de numéros déjà cochés.
     * @param nbNumerosGrille Le nombre de numéros cochés dans la grille principale.
     * @return Un nombre aléatoire de numéros chance à cocher.
     */
    public static byte getRandomNbNumerosChance ( byte nbNumerosGrille ) {
        Random r = new Random();
        byte n, max;
        switch ( nbNumerosGrille ) {
            case Grille.NB_NUMEROS_MAX:
                max = Grille.CHANCE_NB_NUMEROS_MIN;
                break;
            case (Grille.NB_NUMEROS_MAX-1):
                max = Grille.CHANCE_NB_NUMEROS_MIN + 2;
                break;
            case (Grille.NB_NUMEROS_MAX-2):
                max = Grille.CHANCE_NB_NUMEROS_MIN + 7;
                break;
            default:
                max = Grille.CHANCE_NB_NUMEROS_MAX;
        }
        do {
            n = (byte) r.nextInt(max + 1);
        } while ( n == 0 ); // Tant que le générateur sort un 0, on recommence
        return n;
    }
    
    /**
     * Calcule le rang de gain de la grille.
     * NB: le 6e rang de gain est ajouté si un numéro chance a été trouvé.
     * @param nbNumerosGagnants Le nombre de numéros trouvés.
     * @param nbNumerosChanceGagnants Le nombre de numéros chance trouvés.
     * @return Le numéro du rang de gain correspondant.
     */
    public static byte calculRangGagnant( byte nbNumerosGagnants, byte nbNumerosChanceGagnants ) {
        byte rang = 0;
        
        switch ( nbNumerosGagnants ) {
            case 5:
                if ( nbNumerosChanceGagnants > 0 ) {
                    rang = 1;
                } else {
                    rang = 2;
                }
                break;
            case 4:
                if ( nbNumerosChanceGagnants > 0 ) {
                    rang = 9;
                } else {
                    rang = 3;
                }
                break;
            case 3:
                if ( nbNumerosChanceGagnants > 0 ) {
                    rang = 10;
                } else {
                    rang = 4;
                }
                break;
            case 2:
                if ( nbNumerosChanceGagnants > 0 ) {
                    rang = 11;
                } else {
                    rang = 5;
                }
                break;
            case 1:
                if ( nbNumerosChanceGagnants > 0 ) {
                    rang = 6;
                }
                break;
            case 0:
                if ( nbNumerosChanceGagnants > 0 ) {
                    rang = 6;
                }
        }
        return rang;
    }
    
    /**
     * Retourne la grille sous forme de chaîne tabulée.
     * @return Le contenu de la grille sous forme de chaîne de caractères.
     */
    @Override
    public String toString () {
        String retour = "\t";
        byte i;
        
        this.trierNumeros();
        for ( i = 0; i < this.numerosChoisis.length; i++ ) {
            retour += this.numerosChoisis[i];
            if ( i != this.numerosChoisis.length-1 ) { // Si on n'est pas à la fin, tabulation
                retour += "\t";
            }
        }
        retour += " ||\t";
        for ( i = 0; i < this.numerosChanceChoisis.length; i++ ) {
            retour += this.numerosChanceChoisis[i];
            if ( i != this.numerosChanceChoisis.length-1 ) { // Si on n'est pas à la fin, tabulation
                retour += "\t";
            }
        }
        retour += "\t(" + this.miseGrille + " €)\t";
        return retour;
    }
    
    /**
     * Calcul de factorielle
     * @param n Le nombre dont calculer la factorielle.
     * @return La factorielle du nombre passé en paramètre.
     */
    public static long factorielle (int n) {
        int i;
        long res = 1L;
        for ( i = 1; i <= n; i++ ) {
                res = res * (long) i;
        }
        return res;
    }
    
}
