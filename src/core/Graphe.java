package core ;

/**
 *   Classe representant un graphe.
 *   A vous de completer selon vos choix de conception.
 */

import java.io.* ;
import base.* ;
import java.awt.* ;
import java.util.*;

public class Graphe {

    // Nom de la carte utilisee pour construire ce graphe
    private final String nomCarte ;

    // Fenetre graphique
    private final Dessin dessin ;

    // Version du format MAP utilise'.
    private static final int version_map = 4 ;
    private static final int magic_number_map = 0xbacaff ;

    // Version du format PATH.
    private static final int version_path = 1 ;
    private static final int magic_number_path = 0xdecafe ;

    // Identifiant de la carte
    private int idcarte ;

    // Numero de zone de la carte
    private int numzone ;

	//tableau de noeuds pour le graphe
	private Node[] tabNodes;
	
	//tableau de noeuds pour le graphe inverse
	private Node[] tabNodesInverse;
	
    //Getters
    public Dessin getDessin() { return dessin ; }

    public int getZone() { return numzone ; }

	public Node[] getTabNodes(){
		return this.tabNodes;
	}
	
	public Node[] getTabNodesInverse(){
		return this.tabNodesInverse;
	}

    // Le constructeur cree le graphe en lisant les donnees depuis le DataInputStream
    public Graphe (String nomCarte, DataInputStream dis, Dessin dessin) {

		Descripteur [] descripteurs;
		this.nomCarte = nomCarte ;
		this.dessin = dessin ;
		Utils.calibrer(nomCarte, dessin) ;	
	
		// Lecture du fichier MAP. 
		// Voir le fichier "FORMAT" pour le detail du format binaire.
		try {

			// Nombre d'aretes
			int edges = 0 ;

			// Verification du magic number et de la version du format du fichier .map
			int magic = dis.readInt () ;
			int version = dis.readInt () ;
			Utils.checkVersion(magic, magic_number_map, version, version_map, nomCarte, ".map") ;

			// Lecture de l'identifiant de carte et du numero de zone, 
			this.idcarte = dis.readInt () ;
			this.numzone = dis.readInt () ;

			// Lecture du nombre de descripteurs, nombre de noeuds.
			int nb_descripteurs = dis.readInt () ;
			int nb_nodes = dis.readInt () ;

			// Nombre de successeurs enregistrÃ©s dans le fichier.
			int[] nsuccesseurs_a_lire = new int[nb_nodes] ;
	  
			this.tabNodes = new Node[nb_nodes];
			this.tabNodesInverse = new Node[nb_nodes];
			descripteurs = new Descripteur[nb_descripteurs];

			int totalRoutes = 0;
			float longitude;
			float latitude;

			// Lecture des noeuds
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				// Lecture du noeud numero num_node
				longitude=((float)dis.readInt ()) / 1E6f;
				latitude=((float)dis.readInt ()) / 1E6f;
				this.tabNodes[num_node] = new Node(longitude,latitude,num_node);
				this.tabNodesInverse[num_node] = new Node(longitude,latitude,num_node);
				nsuccesseurs_a_lire[num_node] = dis.readUnsignedByte() ;
				totalRoutes+=nsuccesseurs_a_lire[num_node];
			}
	    

			Utils.checkByte(255, dis) ;
	    
			// Lecture des descripteurs
			for (int num_descr = 0 ; num_descr < nb_descripteurs ; num_descr++) {
				// Lecture du descripteur numero num_descr
				descripteurs[num_descr] = new Descripteur(dis) ;

				// On affiche quelques descripteurs parmi tous.
				if (0 == num_descr % (1 + nb_descripteurs / 400))
					System.out.println("Descripteur " + num_descr + " = " + descripteurs[num_descr]) ;
			}
	    
			Utils.checkByte(254, dis) ;
	    
			ArrayList<Segment> segTemp = new ArrayList<Segment>();
	   
			boolean sensUnique=false;

			// Lecture des successeurs
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				// Lecture de tous les successeurs du noeud num_node
				for (int num_succ = 0 ; num_succ < nsuccesseurs_a_lire[num_node] ; num_succ++) {
					// zone du successeur
					int succ_zone = dis.readUnsignedByte() ;
					// numero de noeud du successeur
					int dest_node = Utils.read24bits(dis) ;

					// descripteur de l'arete
					int descr_num = Utils.read24bits(dis) ;
	
					// longueur de l'arete en metres
					int longueur  = dis.readUnsignedShort() ;
					//if(num_node ==139){System.out.println("139 -> dest_node("+dest_node+") :"+longueur);}
					// Nombre de segments constituant l'arete
					int nb_segm   = dis.readUnsignedShort() ;

					edges++ ;


					Couleur.set(dessin, descripteurs[descr_num].getType()) ;

					float current_long = this.tabNodes[num_node].getLong();
					float current_lat  = this.tabNodes[num_node].getLat();

					// Chaque segment est dessine
					for (int i = 0 ; i < nb_segm ; i++) {
						float delta_lon = (dis.readShort()) / 2.0E5f ;
						float delta_lat = (dis.readShort()) / 2.0E5f ;
						dessin.drawLine(current_long, current_lat, (current_long + delta_lon), (current_lat + delta_lat)) ;
						if (nb_nodes < 2000000){
							segTemp.add(new Segment(delta_lon,delta_lat,false)); 
						}
						current_long += delta_lon ;
						current_lat  += delta_lat ;
					}
		    
					// Le dernier trait rejoint le sommet destination.
					// On le dessine si le noeud destination est dans la zone du graphe courant.
					if (succ_zone == numzone) {
						dessin.drawLine(current_long, current_lat, this.tabNodes[dest_node].getLong(), this.tabNodes[dest_node].getLat()) ;
						Route Road = new Route(this.tabNodes[dest_node],descripteurs[descr_num],longueur);
						this.tabNodes[num_node].add_Routes(Road);	
					
						sensUnique=Road.getDes().isSensUnique();
						Route RoadInverse = new Route(this.tabNodes[num_node],descripteurs[descr_num],longueur);
						if(!sensUnique){
						
							this.tabNodes[dest_node].add_Routes(RoadInverse);
							this.tabNodesInverse[num_node].add_Routes(Road);
							this.tabNodesInverse[dest_node].add_Routes(RoadInverse);
						}
						else{
							this.tabNodesInverse[dest_node].add_Routes(RoadInverse);
						}
					}
					if (nb_nodes < 2000000){
						if (succ_zone == numzone) {
							this.tabNodes[num_node].getRoutesSuccesseurs().get(this.tabNodes[num_node].getRoutesSuccesseurs().size()-1).copy(segTemp);	
							if(!sensUnique){
								this.tabNodes[dest_node].getRoutesSuccesseurs().get(this.tabNodes[dest_node].getRoutesSuccesseurs().size()-1).reverseCopy(segTemp);
							}	
							segTemp.clear();
						}
					}
			
				}
			}
	    
			Utils.checkByte(253, dis) ;

			System.out.println("Fichier lu : " + nb_nodes + " sommets, " + edges + " aretes, " 
			       + nb_descripteurs + " descripteurs.") ;

		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(1) ;
		}

    }

	
    // Rayon de la terre en metres
    private static final double rayon_terre = 6378137.0 ;

    /**
     *  Calcule de la distance orthodromique - plus court chemin entre deux points à la surface d'une sphère
     *  @param long1 longitude du premier point.
     *  @param lat1 latitude du premier point.
     *  @param long2 longitude du second point.
     *  @param lat2 latitude du second point.
     *  @return la distance entre les deux points en metres.
     *  Methode Ã©crite par Thomas Thiebaud, mai 2013
     */
    public static double distance(double long1, double lat1, double long2, double lat2) {
        double sinLat = Math.sin(Math.toRadians(lat1))*Math.sin(Math.toRadians(lat2));
        double cosLat = Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2));
        double cosLong = Math.cos(Math.toRadians(long2-long1));
        return rayon_terre*Math.acos(sinLat+cosLat*cosLong);
    }

   
    public void nb_moy_succ(){
        float resultat = 0.0f;
        for (int num_node = 0; num_node < this.tabNodes.length;num_node++){
            resultat += this.tabNodes[num_node].getRoutesSuccesseurs().size();
        }
        System.out.println("Nombre moyen de successeurs : "+ resultat/this.tabNodes.length);
    }


    /**
     *  Attend un clic sur la carte et affiche le numero de sommet le plus proche du clic.
     *  A n'utiliser que pour faire du debug ou des tests ponctuels.
     *  Ne pas utiliser automatiquement a chaque invocation des algorithmes.
     */
    public int situerClick() {

    	System.out.println("Allez-y, cliquez donc.") ;
    	int noeud = 0;
    	if (dessin.waitClick()) {
    		float lon = dessin.getClickLon() ;
    		float lat = dessin.getClickLat() ;
	    
    		System.out.println("Clic aux coordonnees lon = " + lon + "  lat = " + lat) ;

    		// On cherche le noeud le plus proche. O(n)
    		float minDist = Float.MAX_VALUE ;
    		//int   noeud   = 0 ;
	    
    		for (int num_node = 0 ; num_node < this.tabNodes.length ; num_node++) {
    			float londiff = (this.tabNodes[num_node].getLong() - lon) ;
    			float latdiff = (this.tabNodes[num_node].getLat() - lat) ;
    			float dist = londiff*londiff + latdiff*latdiff ;
    			if (dist < minDist) {
    				noeud = num_node ;
    				minDist = dist ;
    			}
    		}

    		System.out.println("Noeud le plus proche : " + noeud) ;
    		System.out.println() ;
    		dessin.setColor(java.awt.Color.red) ;
    		dessin.drawPoint(this.tabNodes[noeud].getLong(), this.tabNodes[noeud].getLat(), 5) ;
    	}
    	return noeud;
    }

    /**
     *  Charge un chemin depuis un fichier .path (voir le fichier FORMAT_PATH qui decrit le format)
     *  Verifie que le chemin est empruntable et calcule le temps de trajet.
     */
    public Chemin verifierChemin(DataInputStream dis, String nom_chemin) {
    	Chemin c = new Chemin();
    	try {
	    
    		// Verification du magic number et de la version du format du fichier .path
    		int magic = dis.readInt () ;
    		int version = dis.readInt () ;
    		Utils.checkVersion(magic, magic_number_path, version, version_path, nom_chemin, ".path") ;

    		// Lecture de l'identifiant de carte
    		int path_carte = dis.readInt () ;

    		if (path_carte != this.idcarte) {
    			System.out.println("Le chemin du fichier " + nom_chemin + " n'appartient pas a la carte actuellement chargee." ) ;
    			System.exit(1) ;
    		}

    		int nb_noeuds = dis.readInt () ;
	    
    		// Origine du chemin
    		int first_zone = dis.readUnsignedByte() ;
    		int first_node = Utils.read24bits(dis) ;

    		// Destination du chemin
    		int last_zone  = dis.readUnsignedByte() ;
    		int last_node = Utils.read24bits(dis) ;

    		System.out.println("Chemin de " + first_zone + ":" + first_node + " vers " + last_zone + ":" + last_node) ;

    		int current_zone = 0 ;
    		int current_node = 0 ;

    		// Tous les noeuds du chemin
    		for (int i = 0 ; i < nb_noeuds ; i++) {
    			current_zone = dis.readUnsignedByte() ;
    			current_node = Utils.read24bits(dis) ; 
    			// on mémorise le chemin dans un tableau contenant les noeuds du chemin
    			c.ajouterSommet(this.tabNodes[current_node]);
    			System.out.println(" --> " + current_zone + ":" + current_node) ;
    		}

    		if ((current_zone != last_zone) || (current_node != last_node)) {
    			System.out.println("Le chemin " + nom_chemin + " ne termine pas sur le bon noeud.") ;
    			System.exit(1) ;
    		}

    	} catch (IOException e) {
    		e.printStackTrace() ;
    		System.exit(1) ;
    	}

    	return c;
    }
}
