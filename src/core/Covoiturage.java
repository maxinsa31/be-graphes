package core;

import java.io.PrintStream;

import base.Readarg;
import java.util.*;

public class Covoiturage extends Algo{
	
	private Pieton pieton;
	
	private Voyageur voiture;
	
	private Pcc algo;
	
	private Isochrone iso;
	
	private HashMap<Node,Double> sommeDesCouts;
	
	public Covoiturage(Pieton pieton, Voyageur voiture, Graphe gr, PrintStream fichierSortie, Readarg readarg){
		super(gr,fichierSortie,readarg);
		this.sommeDesCouts = new HashMap<Node,Double>();
		this.pieton = pieton;
		this.voiture = voiture;
		this.algo = new Pcc(gr,fichierSortie,readarg,this.voiture.noeudDepart.getNumNode(),this.pieton.noeudDepart.getNumNode());
		this.iso = new Isochrone(0f,gr,fichierSortie,readarg,this.pieton.noeudDepart.getNumNode());
	}
	
	
	public void Dijkstra1versN(){
		
		int reste = this.sommeDesCouts.size();
		this.algo.tabLabel[this.algo.origine].setCout(0.0d); // cout de 0 pour le sommet origine
		this.algo.Tas.insert(this.algo.tabLabel[this.algo.origine],this.algo.origine); // insertion dans le tas du sommet origine
		int numSommetMin = this.algo.origine;
		Node sommetMin=this.graphe.getTabNodes()[this.algo.origine]; // numero du sommet min du tas
		int numSommetSuccesseur;
		Node SommetSuccesseur; // numero d'un sommet successeur au sommet min du tas
		while(reste >0 && !this.algo.Tas.isEmpty()){ //tant qu'il existe des sommets non marques
			sommetMin = this.graphe.getTabNodes()[this.algo.Tas.findMin().getSommetCourant()]; // recuperation du numero du sommet min du tas dans HashMap
			if(this.sommeDesCouts.containsKey(sommetMin)){
				reste--;
			}
			numSommetMin = sommetMin.getNumNode();
			Label labelSommetMin = this.algo.tabLabel[numSommetMin];
			labelSommetMin.setCout(this.algo.Tas.deleteMin().getCout()); //mise a jour du cout du label du sommet min 
			labelSommetMin.setMarq(); // mise  a jour du marquage du sommet min : marque
			for (Route r : sommetMin.getRoutesSuccesseurs()){ //pour tous les successeurs de sommet min
				SommetSuccesseur = r.getNodeSucc(); // on recupere son numero de sommet
				numSommetSuccesseur = SommetSuccesseur.getNumNode();
				Label labelSommetSucc=this.algo.tabLabel[numSommetSuccesseur];
				if(!labelSommetSucc.getMarq()){ // si ce sommet n'est pas marque
					if(labelSommetSucc.getCout()>(this.algo.tabLabel[numSommetMin].getCout()+r.getCoutRoute())){
						labelSommetSucc.setCout(this.algo.tabLabel[numSommetMin].getCout()+r.getCoutRoute());
						labelSommetSucc.setPere(numSommetMin); // mise a jour du pere					
						if(this.algo.Tas.hmapContainsKey(labelSommetSucc)){							
							this.algo.Tas.update(labelSommetSucc);
						}
						else{
						this.algo.Tas.insert(labelSommetSucc,numSommetSuccesseur);
						}						
					}
				}
			}
		}
	
	}
	
	public void run(){
		
		System.out.println("Pcc de Voiture vers pieton ");
		this.algo.run();
		
		System.out.println("Isochrone autour du pieton");
		double coutMax = this.algo.coutChemin*this.pieton.getVitessePieton()/130.0d; // A MODIFIER /////////////////////////////////
		this.iso.setCoutMax(coutMax);
		this.iso.run();
		
		for(Node N : this.iso.nodesAtteignables){
			this.sommeDesCouts.put(N,this.iso.tabLabel[N.getNumNode()].getCout());
			System.out.println("Noeud n°"+N.getNumNode()+",cout : "+this.sommeDesCouts.get(N));
		}
		
		System.out.println("Pcc (1->N) de Voiture vers la zone autour de pieton ");
		
		for(int i = 0; i < this.algo.tabLabel.length ; i++){
			this.algo.tabLabel[i] = new Label(i); // reinitialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
		}
		this.algo.Tas = new BinaryHeap<Label>();
		this.Dijkstra1versN();
		for(Node N : this.iso.nodesAtteignables){
			this.sommeDesCouts.put(N, this.sommeDesCouts.get(N)+this.algo.tabLabel[N.getNumNode()].getCout());
			System.out.println("Noeud n°"+N.getNumNode()+",cout : "+this.sommeDesCouts.get(N));
		}
		
		System.out.println("Pcc (1->N) de la destination vers la zone autour de pieton avec un graphe inverse ");
		  
		
	}

}
