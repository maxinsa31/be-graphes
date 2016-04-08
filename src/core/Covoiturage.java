package core;

import java.io.PrintStream;

import base.Readarg;
import java.util.*;

public class Covoiturage extends Algo{
	
	private Pieton pieton;
	
	private Voyageur voiture;
	
	private int nbNodeZone;
	
	private Pcc algo;
	
	public Covoiturage(Pieton pieton, Voyageur voiture, Graphe gr, PrintStream fichierSortie, Readarg readarg){
		super(gr,fichierSortie,readarg);
		this.pieton = pieton;
		this.voiture = voiture;
		this.algo = new Pcc(gr,fichierSortie,readarg,this.voiture.noeudDepart.getNumNode(),this.pieton.noeudDepart.getNumNode());
	}
	
	
	public boolean Dijkstra1versN(int nbNoeudsEligibles){
		
		int reste = nbNoeudsEligibles;
		this.algo.tabLabel[this.algo.origine].setCout(0.0d); // cout de 0 pour le sommet origine
		this.algo.Tas.insert(this.algo.tabLabel[this.algo.origine],this.algo.origine); // insertion dans le tas du sommet origine
		int numSommetMin = this.algo.origine;
		Node SommetMin=this.graphe.getTabNodes()[this.algo.origine]; // numero du sommet min du tas
		int numSommetSuccesseur;
		Node SommetSuccesseur; // numero d'un sommet successeur au sommet min du tas
		while(reste >0){ //tant qu'il existe des sommets non marques
			SommetMin = this.graphe.getTabNodes()[this.algo.Tas.getArray().get(0).getSommetCourant()]; // recuperation du numero du sommet min du tas dans HashMap
			numSommetMin = SommetMin.getNumNode();
			this.algo.tabLabel[numSommetMin].setCout(this.algo.Tas.deleteMin().getCout()); //mise a jour du cout du label du sommet min 
			this.algo.tabLabel[numSommetMin].setMarq(); // mise  a jour du marquage du sommet min : marque
			for (Route r : this.graphe.getTabNodes()[numSommetMin].getRoutesSuccesseurs()){ //pour tous les successeurs de sommet min
				SommetSuccesseur = r.getNodeSucc(); // on recupere son numero de sommet
				numSommetSuccesseur = SommetSuccesseur.getNumNode();
				Label labelSommetSucc=this.algo.tabLabel[numSommetSuccesseur];
				if(!labelSommetSucc.getMarq()){ // si ce sommet n'est pas marque
					if(labelSommetSucc.getCout()>(this.algo.tabLabel[numSommetMin].getCout()+r.getCoutRoute())){
						labelSommetSucc.setCout(this.algo.tabLabel[numSommetMin].getCout()+r.getCoutRoute());
						labelSommetSucc.setPere(numSommetMin); // mise a jour du pere					
						if(this.algo.Tas.getHmap().containsKey(labelSommetSucc)){							
							this.algo.Tas.update(labelSommetSucc);
						}
						else{
						this.algo.Tas.insert(labelSommetSucc,numSommetSuccesseur);
						}						
					}
				}
			}
		}
	return SommetMin.equals(this.graphe.getTabNodes()[this.algo.destination]);
	
	}
	
	public void run(){
		
		System.out.println("Pcc de Voiture vers pieton ");
		this.algo.run();
		ArrayList<Node> noeudsEligibles = new ArrayList<Node>();
		for(int i = 0;i< this.graphe.getTabNodes().length;i++){
			if (Graphe.distance(this.pieton.noeudDepart.getLong(),this.pieton.noeudDepart.getLat(),this.graphe.getTabNodes()[i].getLong(),this.graphe.getTabNodes()[i].getLat())/(100d*(double)this.pieton.getVitessePieton()/6d)<= this.algo.coutChemin){
				// cercle de noeud pour limiter la zone de recherche du point de rencontre 
				noeudsEligibles.add(this.graphe.getTabNodes()[i]);
			}
		}
		System.out.println("Pcc (1->N) de Voiture vers la zone autour de pieton ");
		
		for(int i = 0; i < this.algo.tabLabel.length ; i++){
			this.algo.tabLabel[i] = new Label(i); // initialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
		}
		
		float tab[] = new float[this.algo.tabLabel.length];
		
		
		  
		
	}

	public int getNbNodeZone(){
		return this.nbNodeZone;
	}
}
