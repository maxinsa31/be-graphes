package core;

import java.io.PrintStream;
import java.util.*;
import java.awt.Color;

import base.Readarg;

public class Isochrone extends Pcc {
	
	private double coutMax;
	
	protected ArrayList<Node> nodesAtteignables;
	
	public void afficherNodesAtteignables(){
		System.out.print("Sommets atteignables depuis "+this.origine+" : ");
		for(Node N : nodesAtteignables){
			System.out.print(N.getNumNode()+" ");
		}
		System.out.println();
	}
	
	public void setCoutMax(double coutMax){
		this.coutMax = coutMax;
	}
	
	public Isochrone(double coutMax, Graphe gr, PrintStream sortie, Readarg readarg, int numSommetDepart){
		super(gr,sortie,readarg,numSommetDepart);
		this.coutMax = coutMax;
		this.nodesAtteignables = new ArrayList <Node>();
	}
	
	public void run(){
		for(int i = 0; i < this.tabLabel.length ; i++){
			this.tabLabel[i] = new Label(i); // initialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
		}
		System.out.println("Cout max autorise : "+this.coutMax);
		this.tabLabel[this.origine].setCout(0.0d); // cout de 0 pour le sommet origine
		this.Tas.insert(this.tabLabel[this.origine],this.origine); // insertion dans le tas du sommet origine
		int numSommetMin = this.origine;  // numero du sommet min du tas
		Node sommetMin=this.graphe.getTabNodes()[this.origine]; // sommet min du tas
		Label labelSommetMin=this.tabLabel[numSommetMin];
		while(labelSommetMin.getCout()<this.coutMax && !this.Tas.isEmpty()){ //tant que le cout min est inferieur au cout max autorisé et que le tas est non vide
			this.graphe.getDessin().setColor(Color.red);
			this.graphe.getDessin().drawPoint(sommetMin.getLong(), sommetMin.getLat(), 7);
			this.nodesAtteignables.add(sommetMin);
			sommetMin = this.graphe.getTabNodes()[this.Tas.findMin().getSommetCourant()]; // recuperation du sommet min du tas
			
			numSommetMin = sommetMin.getNumNode(); // et son numero
			labelSommetMin = this.tabLabel[numSommetMin];
			labelSommetMin.setCout(this.Tas.deleteMin().getCout()); //mise a jour du cout du label du sommet min 
			labelSommetMin.setMarq(); // mise  a jour du marquage du sommet min : marque
			this.nbSommetsMarques++;
			for (Route r : this.graphe.getTabNodes()[numSommetMin].getRoutesSuccesseurs()){ //pour tous les successeurs de sommet min
				Node sommetSuccesseur=r.getNodeSucc(); // on recupere le sommet successeur
				int numSommetSuccesseur = sommetSuccesseur.getNumNode(); //et son numero
				Label labelSommetSucc=this.tabLabel[numSommetSuccesseur]; // et son label
				if(!labelSommetSucc.getMarq()){ // si ce sommet n'est pas marque
					if(labelSommetSucc.getCout()>(labelSommetMin.getCout()+r.getCoutRoutePieton())){
						labelSommetSucc.setCout(labelSommetMin.getCout()+r.getCoutRoutePieton());
						labelSommetSucc.setPere(numSommetMin); // mise a jour du pere					
						if(this.Tas.hmapContainsKey(labelSommetSucc)){	
							this.Tas.update(labelSommetSucc);
						}
						else{
							this.Tas.insert(labelSommetSucc,numSommetSuccesseur);
							this.nbSommetsExplores++;
							this.graphe.getDessin().setColor(Color.green);
							this.graphe.getDessin().drawPoint(sommetSuccesseur.getLong(), sommetSuccesseur.getLat(), 2);
						}						
					}
				}
			}
			System.out.println("Cout sommet "+numSommetMin+" : "+labelSommetMin.getCout());
		}
	}
}
