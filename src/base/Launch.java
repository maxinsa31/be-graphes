package base ;

/*
 * Ce programme propose de lancer divers algorithmes sur les graphes
 * a partir d'un menu texte, ou a partir de la ligne de commande (ou des deux).
 *
 * A chaque question posee par le programme (par exemple, le nom de la carte), 
 * la reponse est d'abord cherchee sur la ligne de commande.
 *
 * Pour executer en ligne de commande, ecrire les donnees dans l'ordre. Par exemple
 *   "java base.Launch insa 1 1 /tmp/sortie 0"
 * ce qui signifie : charge la carte "insa", calcule les composantes connexes avec une sortie graphique,
 * ecrit le resultat dans le fichier '/tmp/sortie', puis quitte le programme.
 */

import core.* ;
import java.io.* ;

public class Launch {

    private final Readarg readarg ;

    public Launch(String[] args) {
    	this.readarg = new Readarg(args) ;
    }

    public void afficherMenu () {
    	System.out.println () ;
    	System.out.println ("MENU") ;
    	System.out.println () ;
    	System.out.println ("0 - Quitter") ;
    	System.out.println ("1 - Composantes Connexes") ;
    	System.out.println ("2 - Plus court chemin standard") ;
    	System.out.println ("3 - Plus court chemin A-star") ;
    	System.out.println ("4 - Cliquer sur la carte pour obtenir un numero de sommet.") ;
    	System.out.println ("5 - Charger un fichier de chemin (.path) et le verifier.") ;
    	System.out.println ("6 - Covoiturage") ;
    	System.out.println ("7 - Echange de colis") ;
    	System.out.println ("8 - Test de performance") ;
	
    	System.out.println () ;
    }

    public static void main(String[] args) {
    	Launch launch = new Launch(args) ;
    	launch.go () ;
    }

	public void AfficherNb(String message, float nb){
		System.out.println(message+nb);
	}


    public void go() {

	try {
	    System.out.println ("**") ;
	    System.out.println ("** Programme de test des algorithmes de graphe.");
	    System.out.println ("**") ;
	    System.out.println () ;

	    // On obtient ici le nom de la carte a utiliser.
	    String nomcarte = this.readarg.lireString ("Nom du fichier .map a utiliser ? ") ;
	    DataInputStream mapdata = Openfile.open (nomcarte) ;

	    boolean display = (1 == this.readarg.lireInt ("Voulez-vous une sortie graphique (0 = non, 1 = oui) ? ")) ;	    
	    Dessin dessin = (display) ? new DessinVisible(800,600) : new DessinInvisible() ;

	    Graphe graphe = new Graphe(nomcarte, mapdata, dessin) ;
		graphe.nb_moy_succ();

	    // Boucle principale : le menu est accessible 
	    // jusqu'a ce que l'on quitte.
	    boolean continuer = true ;
	    int choix ;
	    
	    while (continuer) {
	    	this.afficherMenu () ;
	    	choix = this.readarg.lireInt ("Votre choix ? ") ;
		
	    	// Algorithme a executer
	    	Algo algo = null ;
		
	    	// Le choix correspond au numero du menu.
	    	switch (choix) {
	    		case 0 : continuer = false ; break ;

	    		case 1 : algo = new Connexite(graphe, this.fichierSortie (), this.readarg) ; 	break ;
		
	    		case 2 : algo = new Pcc(graphe, this.fichierSortie (), this.readarg) ; break ;
		
	    		case 3 : algo = new PccStar(graphe, this.fichierSortie (), this.readarg) ; break ;
	
	    		case 4 : graphe.situerClick() ; break ;

	    		case 5 :
	    			String nom_chemin = this.readarg.lireString ("Nom du fichier .path contenant le chemin ? ") ;
	    			Chemin c = graphe.verifierChemin(Openfile.open (nom_chemin), nom_chemin) ;
	    			c.calculCoutChemin();
	    			AfficherNb("Cout du chemin : ",(float)c.getCout());
	    			if (graphe.getTabNodes().length <= 1000000){
	    				c.DessinerChemin(dessin);
	    			}else{
	    				c.DessinerChemin2(dessin);
	    			}
	    			break ;
	    		case 6 :
	    			int numSommetPieton = this.readarg.lireInt("Sommet de depart du piÃ©ton ? ");
	    			int numSommetVoiture = this.readarg.lireInt("Sommet de depart de la voiture ? ");
	    			int numSommetArrivee = this.readarg.lireInt("Sommet de destination du covoiturage ? ");
	    			Node sommetPieton = graphe.getTabNodes()[numSommetPieton];
	    			Node sommetVoiture = graphe.getTabNodes()[numSommetVoiture];
	    			Node sommetArrivee = graphe.getTabNodes()[numSommetArrivee];
	    			Voyageur voiture = new Voyageur(sommetVoiture,sommetArrivee);
	    			Pieton pieton = new Pieton(sommetPieton,sommetArrivee);
			
	    			algo = new Covoiturage(pieton,voiture,graphe,this.fichierSortie(),this.readarg); 
			
	    			break ;
	    		case 7 :
	    			int departRobot1 = this.readarg.lireInt("Sommet de depart du robot 1 ? ");
	    			int departRobot2 = this.readarg.lireInt("Sommet de depart du robot 2 ? ");
	    			int arriveeRobot1 = this.readarg.lireInt("Sommet d'arrivee du robot 1 ? ");
	    			int arriveeRobot2 = this.readarg.lireInt("Sommet d'arrivee du robot 2 ? ");
	    			Node sommetDepartRobot1 = graphe.getTabNodes()[departRobot1];
	    			Node sommetDepartRobot2 = graphe.getTabNodes()[departRobot2];
	    			Node sommetArriveeRobot1 = graphe.getTabNodes()[arriveeRobot1];
	    			Node sommetArriveeRobot2 = graphe.getTabNodes()[arriveeRobot2];
	    			Voyageur robot1 = new Voyageur (sommetDepartRobot1,sommetArriveeRobot1);
	    			Voyageur robot2 = new Voyageur (sommetDepartRobot2,sommetArriveeRobot2);
			
	    			algo = new EchangeColis(robot1,robot2,graphe,this.fichierSortie(),this.readarg);
			
	    			break;
	    			
	    		case 8 :
	    			for(int i=0;i<5;i++){
	    				int numSommetDepart = (int)(Math.random()*(graphe.getTabNodes().length+1));
	    				int numSommetDestination = (int)(Math.random()*(graphe.getTabNodes().length+1));
	    				System.gc();
	    				algo = new Pcc(graphe,this.fichierSortie(),this.readarg,numSommetDepart,numSommetDestination);
	    				algo.run();
	    				System.gc();
	    				algo = new PccStar(graphe,this.fichierSortie(),this.readarg,numSommetDepart,numSommetDestination);
	    				algo.run();
	    			}
	    			algo = null;
	    			
	    			break;
			
	    		default:
	    			System.out.println ("Choix de menu incorrect : " + choix) ;
	    			System.exit(1) ;
	    	}
		
	    	if (algo != null) {
	    		System.gc();
	    		algo.run() ;
	    	}
	    }
	    
	    System.out.println ("Programme terminé.") ;
	    System.exit(0) ;
	    
	    
	} catch (Throwable t) {
	    t.printStackTrace() ;
	    System.exit(1) ;
	}
    }

    // Ouvre un fichier de sortie pour ecrire les reponses
    public PrintStream fichierSortie () {
    	PrintStream result = System.out ;

    	String nom = this.readarg.lireString ("Nom du fichier de sortie ? ") ;

    	if ("".equals(nom)) { nom = "/dev/null" ; }

    	try { result = new PrintStream(nom) ; }
    	catch (Exception e) {
    		System.err.println ("Erreur a l'ouverture du fichier " + nom) ;
    		System.exit(1) ;
    	}

    	return result ;
    }

}
