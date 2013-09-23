package NFA031.LotoJava;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Classe principale du jeu de Loto.
 * @author Aurélie Lucet <aurelie.lucet at gmail.com>
 */
public class Loto {
    /** Division du total des mises selon le rang de gain. */
    public final static float PARTS_MISES[] = {
        0.1953F, // Rang 1: 19,53% des mises
        0.0506F, // Rang 2: 5,06% des mises
        0.1089F, // etc...
        0.0472F, 
        0.3372F, 
        0.1887F
    };
    /** Rang dont les gagnants de certains rangs prennent aussi les gains. */
    public final static byte RANG_CHANCE = 6;
    /** Rangs ayant une part du rang "Chance" dans leur gains. */
    public final static byte RANGS_COMPLEMENTAIRES[] = { 
        9,  // Rangs 3 et 6 (4 n° + 1 n° chance)
        10, // Rangs 4 et 6 (3 n° + 1 n° chance)
        11  // Rangs 5 et 6 (2 n° + 1 n° chance)
    };
    
    private static int compteurBulletins = 1;
    
    public static void main (String args[]) {
        int i, nbBulletins;
        byte b, j, nbGrillesBulletin, rang;
        long nbGrilles = 0;
        double gain;
        Bulletin bulletins[];                   // Bulletins générés pour le jeu.
        Grille gagnante;                        // Grille gagnante.
        long totalMisesTirage = 0L;             // Total des sommes misées
        double remisEnJeu;                      // Somme misée mais non gagnée
        double misesParRang[] = new double[6];  // Totaux des parts des rangs calculés
        int gagnantsParRang[] = new int[6];     // Totaux des nombres de grilles gagnantes par rang
        String dateSaisie;                      // Date du tirage
        long debutExec, tpsExec;
        boolean verbeux = true;
        char v = '\0';
        
        Scanner input = new Scanner(System.in);
        
        // Saisie du nombre de bulletins à créer
        System.out.println("========== LOTO ==========");
        do {
            System.out.print("Nombre de bulletins à créer (de 1 à 1000000): ");
            nbBulletins = input.nextInt();
        } while ( nbBulletins <= 0 && nbBulletins > 1000000 );
        bulletins = new Bulletin[nbBulletins];
        
        // Saisie de la date
        do {
            System.out.print("Saisir la date du tirage (dd-mm-yyyy): ");
            dateSaisie = input.next();
        } while ( !isDateValide(dateSaisie) );
        
        // Mode verbeux (affichage des bulletins)
        do {
            System.out.print("Mode verbeux (o/n) ? ");
            v = input.next().toLowerCase().charAt(0);
        } while ( v != 'o' && v != 'n' );
        if ( v == 'n' ) {
            verbeux = false;
        }
        
        // Tirage de la grille gagnante
        gagnante = new Grille();
        if ( verbeux ) {
            System.out.println("\n******************** Grille gagnante ********************");
            System.out.println("\t" + gagnante + "\n");
        }
        
        // Création des bulletins
        debutExec = System.currentTimeMillis();
        for ( i = 0; i < nbBulletins; i++ ) {
            bulletins[i] = new Bulletin(Loto.compteurBulletins++, (byte) 0, gagnante);
            
            totalMisesTirage += bulletins[i].getTotalMises();
            nbGrillesBulletin = bulletins[i].getNombreGrilles();
            for ( b = 0; b < nbGrillesBulletin; b++ ) {
                rang = bulletins[i].getGrilles()[b].getRang();
                if ( rang > 0 ) {
                    // Rangs complémentaires: 9 (3+6), 10 (4+6), 11 (5+6): les grilles sont comptées dans deux rangs
                    if ( rang >= Loto.RANGS_COMPLEMENTAIRES[0] 
                            && rang <= Loto.RANGS_COMPLEMENTAIRES[Loto.RANGS_COMPLEMENTAIRES.length-1] ) {
                        gagnantsParRang[rang-Loto.RANG_CHANCE-1]++;
                        gagnantsParRang[Loto.RANG_CHANCE-1]++;
                    } else {
                        // Rangs "simples" (1 à 6)
                        gagnantsParRang[rang-1]++;
                    }
                }
            }
            
            nbGrilles += bulletins[i].getNombreGrilles();
            if ( verbeux ) {
                System.out.print(bulletins[i]);
            }
        }
        remisEnJeu = totalMisesTirage;
        
        // Calcul des totaux du jeu.
        System.out.println("Date du tirage: " + dateSaisie);
        System.out.println("Nombre total de bulletins joués: " + nbBulletins);
        System.out.println("Nombre total de grilles jouées: " + nbGrilles);
        System.out.println("Total des mises jouées: " + totalMisesTirage + " €.");
        
        // Gains totaux par rang
        System.out.println("Répartition des gains totaux: ");
        for ( b = 0; b < PARTS_MISES.length; b++ ) {
            misesParRang[b] = (double) (Math.round(PARTS_MISES[b] * totalMisesTirage * 100.0D)) / 100.0D;
            System.out.println("Rang " + (b+1) + ": " + misesParRang[b] + " €");
        }
        
        // Affichage du nombre de bulletins et des gains par bulletin, par rang
        System.out.println("Gains des grilles gagnantes par rang: ");
        for ( b = 0; b < gagnantsParRang.length; b++ ) {
            System.out.print("Rang " + (b+1) + ": " + gagnantsParRang[b]);
            if ( gagnantsParRang[b] > 0 ) {     // Affichage des gains par bulletin
                gain = misesParRang[b]/gagnantsParRang[b];
                remisEnJeu -= misesParRang[b];
                System.out.print("\t-> " + (double) (Math.round(gain*100.0D))/100.0D + " €\tpar grille");
            }
            System.out.print("\n");
        }
        
        System.out.print("\nMises non remportées: " + (double) (Math.round(remisEnJeu*100.0D))/100.0D + " €\n");
        
        // Affichage du temps d'exécution
        tpsExec = System.currentTimeMillis() - debutExec;
        System.out.println("**********\nTemps d'exécution: " + String.format("%d min., %d sec.", 
                                TimeUnit.MILLISECONDS.toMinutes(tpsExec),
                                TimeUnit.MILLISECONDS.toSeconds(tpsExec) - 
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tpsExec))
                            ));
        
    }
    
    /**
     * Valide une date saisie sous le format "dd-mm-yyyy" ou "dd-mm-yy"
     * @param date
     * @return True si la date saisie est valide.
     */
    public static boolean isDateValide ( String date ) {
        boolean ret = false;
        byte mois[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };   // Pour les années non-bissextiles
        byte d, m;
        short y;
        
        
        if ( date.length() == 8 || date.length() == 10 ) {          // Taille de la chaîne
            if ( date.charAt(2) == '-' && date.charAt(5) == '-' ) { // Présence des tirets au bon endroit
                try {   // Les parses peuvent lancer des exceptions
                    d = Byte.parseByte(date.substring(0, 2));
                    m = Byte.parseByte(date.substring(3, 5));
                    y = Short.parseShort(date.substring(6));

                    if ( y < 100 ) {        // Cas de l'année sur deux chiffres
                        if ( y >= 70 ) {    // On assume le calendrier à partir de 1970
                            y += 1900;
                        } else {
                            y += 2000;
                        }
                    }
                    if ( isBissextile(y) ) {    // Année bissextile: on ajoute un jour à Février
                        mois[1]++;
                    }

                    if ( m >= 1 && m <= 12 ) {              // Vérif. du mois (1 à 12)
                        if ( d <= mois[m-1] && d >= 1 ) {   // Vérif. du jour (1 à 28/29/30/31)
                            ret = true;
                        }
                    }
                } catch ( NumberFormatException e ) {
                    // d, m & y n'ont pas pu être parsés (la date ne contenait pas de chiffres valides)
                }
            }
        }
        
        return ret;
    }
    
    /**
     * Détermine si une année fournie est bissextile ou non.
     * @param annee
     * @return True si l'année est bissextile, false sinon.
     */
    public static boolean isBissextile ( short annee ) {
        /* Années bissextiles :
            - soit divisibles par 4 mais pas par 100;
            - soit divisibles par 400 */
        return ( (annee % 400 == 0) || (annee % 4 == 0 && !(annee % 100 == 0)) );
    }
    
}
