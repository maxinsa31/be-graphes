package core;

import java.awt.Color;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import base.Readarg;

public class EchangeColis extends Algo {

	private Voyageur robot1;
	
	private Voyageur robot2;
	
	private Isochrone iso;
	
	private Pcc pcc;
	
	private HashMap<Node,Double> sommeDesCouts;
	
	public EchangeColis(Voyageur robot1, Voyageur robot2, Graphe gr, PrintStream fichierSortie, Readarg readarg){
		super(gr,fichierSortie,readarg);
		this.robot1 = robot1 ;
		this.robot2 = robot2;
		this.iso = new Isochrone(0f,gr,fichierSortie,readarg,0); // cout max et noeud de depart de l'isochrone fixés à 0 par convention au depart
		this.pcc = new Pcc(gr,fichierSortie,readarg,this.robot1.noeudDepart.getNumNode(),0);
		this.sommeDesCouts = new HashMap<Node,Double>();
	}
	
	public Node situerCentre(){
		
		float lon = (this.robot1.noeudDepart.getLong() + this.robot1.noeudArrivee.getLong() + this.robot2.noeudDepart.getLong() + this.robot2.noeudArrivee.getLong()) / 4.0f ;
		float lat = (this.robot1.noeudDepart.getLat() + this.robot1.noeudArrivee.getLat() + this.robot2.noeudDepart.getLat() + this.robot2.noeudArrivee.getLat()) / 4.0f ;
    
		// On cherche le noeud le plus proche. O(n)
		float minDist = Float.MAX_VALUE ;
		int   noeud   = 0 ;
    
		for (int num_node = 0 ; num_node < this.graphe.getTabNodes().length ; num_node++) {
			float londiff = (this.graphe.getTabNodes()[num_node].getLong() - lon) ;
			float latdiff = (this.graphe.getTabNodes()[num_node].getLat() - lat) ;
			float dist = londiff*londiff + latdiff*latdiff ;
			if (dist < minDist) {
				noeud = num_node ;
				minDist = dist ;
			}
		}
		return this.graphe.getTabNodes()[noeud];
	}
	
public void Dijkstra1versN(boolean inverse){ // inverse vaut 0 si normal et 1 si inverse
		
		int reste = this.sommeDesCouts.size();
		this.pcc.tabLabel[this.pcc.origine].setCout(0.0d); // cout de 0 pour le sommet origine
		this.pcc.Tas.insert(this.pcc.tabLabel[this.pcc.origine],this.pcc.origine); // insertion dans le tas du sommet origine
		int numSommetMin = this.pcc.origine;
		Node sommetMin;
		if(!inverse){
			sommetMin=this.graphe.getTabNodes()[this.pcc.origine]; // numero du sommet min du tas
		}
		else{
			sommetMin=this.graphe.getTabNodesInverse()[this.pcc.origine]; // numero du sommet min du tas
		}
		int numSommetSuccesseur;
		Node SommetSuccesseur; // numero d'un sommet successeur au sommet min du tas
		while(reste >0 && !this.pcc.Tas.isEmpty()){ //tant qu'il existe des sommets non marques
			if(!inverse){
				sommetMin = this.graphe.getTabNodes()[this.pcc.Tas.findMin().getSommetCourant()]; // recuperation du numero du sommet min du tas dans HashMap
			}
			else{
				sommetMin = this.graphe.getTabNodesInverse()[this.pcc.Tas.findMin().getSommetCourant()];
			}
			if(this.sommeDesCouts.containsKey(sommetMin)){
				reste--;
			}
			numSommetMin = sommetMin.getNumNode();
			Label labelSommetMin = this.pcc.tabLabel[numSommetMin];
			labelSommetMin.setCout(this.pcc.Tas.deleteMin().getCout()); //mise a jour du cout du label du sommet min 
			labelSommetMin.setMarq(); // mise  a jour du marquage du sommet min : marque
			for (Route r : sommetMin.getRoutesSuccesseurs()){ //pour tous les successeurs de sommet min
				SommetSuccesseur = r.getNodeSucc(); // on recupere son numero de sommet
				numSommetSuccesseur = SommetSuccesseur.getNumNode();
				Label labelSommetSucc=this.pcc.tabLabel[numSommetSuccesseur];
				if(!labelSommetSucc.getMarq()){ // si ce sommet n'est pas marque
					if(labelSommetSucc.getCout()>(this.pcc.tabLabel[numSommetMin].getCout()+r.getCoutRoute())){
						labelSommetSucc.setCout(this.pcc.tabLabel[numSommetMin].getCout()+r.getCoutRoute());
						labelSommetSucc.setPere(numSommetMin); // mise a jour du pere					
						if(this.pcc.Tas.hmapContainsKey(labelSommetSucc)){							
							this.pcc.Tas.update(labelSommetSucc);
						}
						else{
						this.pcc.Tas.insert(labelSommetSucc,numSommetSuccesseur);
						}						
					}
				}
			}
		}
	
	}
	
	public void run(){
		// Tracé des 4 points (départ + arrivée des 2 robots)
		this.graphe.getDessin().setColor(Color.cyan);
		this.graphe.getDessin().drawPoint(this.robot1.noeudDepart.getLong(), this.robot1.noeudDepart.getLat(), 7);
		this.graphe.getDessin().setColor(Color.blue);
		this.graphe.getDessin().drawPoint(this.robot1.noeudArrivee.getLong(), this.robot1.noeudArrivee.getLat(), 7);
		this.graphe.getDessin().setColor(Color.pink);
		this.graphe.getDessin().drawPoint(this.robot2.noeudDepart.getLong(), this.robot2.noeudDepart.getLat(), 7);
		this.graphe.getDessin().setColor(Color.magenta);
		this.graphe.getDessin().drawPoint(this.robot2.noeudArrivee.getLong(), this.robot2.noeudArrivee.getLat(), 7);
		
		// Détermination du centre de la forme engendrée par les 4 points du dessus
		int centreIsochrone = this.situerCentre().getNumNode();
		// Ce point est le point de départ d'un isochrone que nous allons utiliser
		this.iso.origine = centreIsochrone; 
		
		// PCC d'un des 4 points vers le centre de la figure
		this.pcc.destination = centreIsochrone;
		this.pcc.run();
		this.pcc.chemin.calculCoutChemin();
		
		// cout max de l'isochrone : 1/3 du temps de trajet entre un des points et le centre de l'isochrone
		this.iso.setCoutMax(this.pcc.chemin.getCout()/2.0d);
		
		// Isochrone
		this.iso.runColis();
		
		// PCC depuis les 4 points (départ + arrivée des 2 robots) vers la zone créée par l'isochrone
		System.out.println("Pcc de robot 1 vers la zone de l'isochrone");
		this.pcc.origine = this.robot1.noeudDepart.getNumNode();
		this.Dijkstra1versN(false); // argument false car dijkstra pas inverse
		for(Node N : this.iso.nodesAtteignables){
			this.sommeDesCouts.put(N, this.pcc.tabLabel[N.getNumNode()].getCout());
		}
		
		Label [] label1versIso = new Label[this.graphe.getTabNodes().length];		
		for(int i = 0 ; i< this.graphe.getTabNodes().length ; i++){
			Label intermediaire = this.pcc.tabLabel[i];
			label1versIso[i] = new Label(intermediaire.getSommetCourant(),intermediaire.getMarq(),intermediaire.getPere(),intermediaire.getCout());
		}
		
		System.out.println("Pcc de robot 2 vers la zone de l'isochrone");
		this.pcc.origine = this.robot2.noeudDepart.getNumNode();
		for(int i = 0; i < this.pcc.tabLabel.length ; i++){
			this.pcc.tabLabel[i] = new Label(i); // reinitialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
		}
		this.pcc.Tas = new BinaryHeap<Label>();
		this.Dijkstra1versN(false); // argument false car dijkstra pas inverse
		for(Node N : this.iso.nodesAtteignables){
			if(this.pcc.tabLabel[N.getNumNode()].getCout()> this.sommeDesCouts.get(N)){
				this.sommeDesCouts.put(N, this.sommeDesCouts.get(N)+this.pcc.tabLabel[N.getNumNode()].getCout());
			}
		}
		
		Label [] label2versIso = new Label[this.graphe.getTabNodes().length];		
		for(int i = 0 ; i< this.graphe.getTabNodes().length ; i++){
			Label intermediaire = this.pcc.tabLabel[i];
			label2versIso[i] = new Label(intermediaire.getSommetCourant(),intermediaire.getMarq(),intermediaire.getPere(),intermediaire.getCout());
		}
		
		System.out.println("Pcc de la destination 1 vers la zone de l'isochrone");
		
		HashMap <Node,Double> coutsDest = new HashMap<Node,Double>();
		
		this.pcc.origine = this.robot1.noeudArrivee.getNumNode();
		for(int i = 0; i < this.pcc.tabLabel.length ; i++){
			this.pcc.tabLabel[i] = new Label(i); // reinitialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
		}
		this.pcc.Tas = new BinaryHeap<Label>();
		this.Dijkstra1versN(true); // argument false car dijkstra pas inverse
		for(Node N : this.iso.nodesAtteignables){
			coutsDest.put(N,this.pcc.tabLabel[N.getNumNode()].getCout());
		}
		
		Label [] labelDest1versIso = new Label[this.graphe.getTabNodes().length];		
		for(int i = 0 ; i< this.graphe.getTabNodes().length ; i++){
			Label intermediaire = this.pcc.tabLabel[i];
			labelDest1versIso[i] = new Label(intermediaire.getSommetCourant(),intermediaire.getMarq(),intermediaire.getPere(),intermediaire.getCout());
		}
		
		System.out.println("Pcc de la destination 2 vers la zone de l'isochrone");
		this.pcc.origine = this.robot2.noeudArrivee.getNumNode();
		for(int i = 0; i < this.pcc.tabLabel.length ; i++){
			this.pcc.tabLabel[i] = new Label(i); // reinitialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
		}
		this.pcc.Tas = new BinaryHeap<Label>();
		this.Dijkstra1versN(true); // argument false car dijkstra pas inverse
		for(Node N : this.iso.nodesAtteignables){
			if(this.pcc.tabLabel[N.getNumNode()].getCout() > coutsDest.get(N)){
				this.sommeDesCouts.put(N, this.sommeDesCouts.get(N)+this.pcc.tabLabel[N.getNumNode()].getCout());
			}
			else{
				this.sommeDesCouts.put(N, this.sommeDesCouts.get(N)+coutsDest.get(N));
			}
		}
		
		Label [] labelDest2versIso = new Label[this.graphe.getTabNodes().length];		
		for(int i = 0 ; i< this.graphe.getTabNodes().length ; i++){
			Label intermediaire = this.pcc.tabLabel[i];
			labelDest2versIso[i] = new Label(intermediaire.getSommetCourant(),intermediaire.getMarq(),intermediaire.getPere(),intermediaire.getCout());
		}
		
		if(this.iso.nodesAtteignables.size()>0){
			Node nodeRencontre = this.iso.nodesAtteignables.get(0);
			double nouveauCout=0;
			for(Node N : this.iso.nodesAtteignables){
				nouveauCout = this.sommeDesCouts.get(N)+this.pcc.tabLabel[N.getNumNode()].getCout();
				this.sommeDesCouts.put(N, nouveauCout);
				if(nouveauCout<this.sommeDesCouts.get(nodeRencontre)){
					nodeRencontre=N;
				}
			}
			System.out.println("Noeud de rencontre : "+nodeRencontre.getNumNode());
			
			// Tracé du chemin du robot 1 vers le point de rencontre
			Chemin chemin = new Chemin();		
			int numSommet = nodeRencontre.getNumNode();
			ArrayList<Node> temp=new ArrayList<Node>();
			while(numSommet!=this.robot1.noeudDepart.getNumNode()){
				temp.add(this.graphe.getTabNodes()[numSommet]);
				numSommet=label1versIso[numSommet].getPere();
			}
			temp.add(this.graphe.getTabNodes()[numSommet]);
			chemin.reverseCopy(temp);
			this.graphe.getDessin().setColor(Color.cyan);
			chemin.DessinerChemin(this.graphe.getDessin());
			
			// Tracé du chemin du robot 2 vers le point de rencontre
			chemin = new Chemin();		
			numSommet = nodeRencontre.getNumNode();
			temp=new ArrayList<Node>();
			while(numSommet!=this.robot2.noeudDepart.getNumNode()){
				temp.add(this.graphe.getTabNodes()[numSommet]);
				numSommet=label2versIso[numSommet].getPere();
			}
			temp.add(this.graphe.getTabNodes()[numSommet]);
			chemin.reverseCopy(temp);
			this.graphe.getDessin().setColor(Color.pink);
			chemin.DessinerChemin(this.graphe.getDessin());
			
			// Tracé du chemin du point de rencontre vers la destination du robot 1
			chemin = new Chemin();		
			numSommet = nodeRencontre.getNumNode();
			while(numSommet!=this.robot1.noeudArrivee.getNumNode()){
				chemin.getChemin().add(this.graphe.getTabNodes()[numSommet]);
				numSommet=labelDest1versIso[numSommet].getPere();
			}
			chemin.getChemin().add(this.graphe.getTabNodes()[numSommet]);
			this.graphe.getDessin().setColor(Color.blue);
			chemin.DessinerChemin(this.graphe.getDessin());
			
			// Tracé du chemin du point de rencontre vers la destination du robot 2
			chemin = new Chemin();		
			numSommet = nodeRencontre.getNumNode();
			while(numSommet!=this.robot2.noeudArrivee.getNumNode()){
				chemin.getChemin().add(this.graphe.getTabNodes()[numSommet]);
				numSommet=labelDest2versIso[numSommet].getPere();
			}
			chemin.getChemin().add(this.graphe.getTabNodes()[numSommet]);
			this.graphe.getDessin().setColor(Color.magenta);
			chemin.DessinerChemin(this.graphe.getDessin());
			
			//Tracé du point de rencontre
			this.graphe.getDessin().setColor(Color.darkGray);
			this.graphe.getDessin().drawPoint(nodeRencontre.getLong(), nodeRencontre.getLat(), 5);
			
			System.out.println("Cout total : "+this.sommeDesCouts.get(nodeRencontre));
		}
		
	}
	
}
