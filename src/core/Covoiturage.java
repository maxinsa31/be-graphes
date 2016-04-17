package core;

import java.awt.Color;
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
	
	
	public void Dijkstra1versN(boolean inverse){ // inverse vaut 0 si normal et 1 si inverse
		
		int reste = this.sommeDesCouts.size();
		this.algo.tabLabel[this.algo.origine].setCout(0.0d); // cout de 0 pour le sommet origine
		this.algo.Tas.insert(this.algo.tabLabel[this.algo.origine],this.algo.origine); // insertion dans le tas du sommet origine
		int numSommetMin = this.algo.origine;
		Node sommetMin;
		if(!inverse){
			sommetMin=this.graphe.getTabNodes()[this.algo.origine]; // numero du sommet min du tas
		}
		else{
			sommetMin=this.graphe.getTabNodesInverse()[this.algo.origine]; // numero du sommet min du tas
		}
		int numSommetSuccesseur;
		Node SommetSuccesseur; // numero d'un sommet successeur au sommet min du tas
		while(reste >0 && !this.algo.Tas.isEmpty()){ //tant qu'il existe des sommets non marques
			if(!inverse){
				sommetMin = this.graphe.getTabNodes()[this.algo.Tas.findMin().getSommetCourant()]; // recuperation du numero du sommet min du tas dans HashMap
			}
			else{
				sommetMin = this.graphe.getTabNodesInverse()[this.algo.Tas.findMin().getSommetCourant()];
			}
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
		double coutMax = this.algo.getChemin().getCout();
		this.iso.setCoutMax(coutMax);
		this.iso.run();
		this.iso.afficherNodesAtteignables();
		
		for(Node N : this.iso.nodesAtteignables){
			this.sommeDesCouts.put(N,this.iso.tabLabel[N.getNumNode()].getCout());
		}
		
		System.out.println("Pcc (1->N) de Voiture vers la zone autour de pieton ");
		
		for(int i = 0; i < this.algo.tabLabel.length ; i++){
			this.algo.tabLabel[i] = new Label(i); // reinitialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
		}
		this.algo.Tas = new BinaryHeap<Label>();
		this.Dijkstra1versN(false); // argument false car dijkstra pas inverse
		for(Node N : this.iso.nodesAtteignables){
			this.sommeDesCouts.put(N, this.sommeDesCouts.get(N)+this.algo.tabLabel[N.getNumNode()].getCout());
		}
		
		System.out.println("Pcc (1->N) de la destination vers la zone autour de pieton avec un graphe inverse ");
		this.algo.origine = this.voiture.noeudArrivee.getNumNode();
		for(int i = 0; i < this.algo.tabLabel.length ; i++){
			this.algo.tabLabel[i] = new Label(i); // reinitialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
		}
		this.algo.Tas = new BinaryHeap<Label>();
		this.Dijkstra1versN(true); // argument false car dijkstra pas inverse
		Node nodeRencontre = this.iso.nodesAtteignables.get(0);
		double nouveauCout=0;
		for(Node N : this.iso.nodesAtteignables){
			nouveauCout = this.sommeDesCouts.get(N)+this.algo.tabLabel[N.getNumNode()].getCout();
			this.sommeDesCouts.put(N, nouveauCout);
			if(nouveauCout<this.sommeDesCouts.get(nodeRencontre)){
				nodeRencontre=N;
			}
		}
		System.out.println("temps total "+nouveauCout);
		this.graphe.getDessin().setColor(Color.pink);
		this.graphe.getDessin().drawPoint(nodeRencontre.getLong(), nodeRencontre.getLat(), 10);
	}

}
