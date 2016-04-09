package core ;

import java.awt.Color;
import java.io.* ;
import base.Readarg ;
import java.util.*;

public class Pcc extends Algo {

    // Numero des sommets origine et destination
    protected int zoneOrigine ;
    protected int origine ;

    protected int zoneDestination ;
    protected int destination ;
    
    protected float coutChemin;

	protected Label [] tabLabel;

	protected BinaryHeap<Label> Tas;

    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg) {
		super(gr, sortie, readarg) ;

		this.zoneOrigine = gr.getZone () ;
		this.origine = readarg.lireInt ("Numero du sommet d'origine ? ") ;

		// Demander la zone et le sommet destination.
		this.zoneOrigine = gr.getZone () ;
		this.destination = readarg.lireInt ("Numero du sommet destination ? ");

		this.tabLabel = new Label[gr.getTabNodes().length];
	
		this.Tas = new BinaryHeap<Label>();
    }
    
    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg, int numSommetDepart,int numSommetArrivee) { //constructeur pour le covoiturage
		super(gr, sortie, readarg) ;

		this.zoneOrigine = gr.getZone () ;
		this.origine = numSommetDepart ;

		// Demander la zone et le sommet destination.
		this.zoneOrigine = gr.getZone () ;
		this.destination = numSommetArrivee;

		this.tabLabel = new Label[gr.getTabNodes().length];
	
		this.Tas = new BinaryHeap<Label>();
    }



	public void reverseCopy(ArrayList<Node> tabC){
		for (Node N : tabC){
			this.graphe.getChemin().add(0,N);
		}
	}
	
	public boolean Dijkstra(){
		long startTime = System.currentTimeMillis();
		this.tabLabel[this.origine].setCout(0.0d); // cout de 0 pour le sommet origine
		this.Tas.insert(this.tabLabel[this.origine],this.origine); // insertion dans le tas du sommet origine
		int numSommetMin = this.origine;
		Node SommetMin=this.graphe.getTabNodes()[this.origine]; // numero du sommet min du tas
		int numSommetSuccesseur;
		Node SommetSuccesseur; // numero d'un sommet successeur au sommet min du tas
		while(!this.Tas.isEmpty()&& !(SommetMin.equals(this.graphe.getTabNodes()[this.destination]))){ //tant qu'il existe des sommets non marques
			SommetMin = this.graphe.getTabNodes()[this.Tas.findMin().getSommetCourant()]; // recuperation du numero du sommet min du tas dans HashMap
			numSommetMin = SommetMin.getNumNode();
			this.tabLabel[numSommetMin].setCout(this.Tas.deleteMin().getCout()); //mise a jour du cout du label du sommet min 
			this.tabLabel[numSommetMin].setMarq(); // mise  a jour du marquage du sommet min : marque
			for (Route r : this.graphe.getTabNodes()[numSommetMin].getRoutesSuccesseurs()){ //pour tous les successeurs de sommet min
				SommetSuccesseur=r.getNodeSucc(); // on recupere son numero de sommet
				numSommetSuccesseur = SommetSuccesseur.getNumNode();
				if(!this.tabLabel[numSommetSuccesseur].getMarq()){ // si ce sommet n'est pas marque
					if(this.tabLabel[numSommetSuccesseur].getCout()>(this.tabLabel[numSommetMin].getCout()+r.getCoutRoute())){
						this.tabLabel[numSommetSuccesseur].setCout(this.tabLabel[numSommetMin].getCout()+r.getCoutRoute());
						this.tabLabel[numSommetSuccesseur].setPere(numSommetMin); // mise a jour du pere					
						if(this.Tas.getHmap().containsKey(this.tabLabel[numSommetSuccesseur])){	
							this.Tas.update(this.tabLabel[numSommetSuccesseur]);
						}
						else{
							this.Tas.insert(this.tabLabel[numSommetSuccesseur],numSommetSuccesseur);
						}						
					}
				}
			}
		}
		long endTime = System.currentTimeMillis();
		float executionTime = (endTime - startTime) / 1000f;
		System.out.println("Temps d'execution : "+executionTime+" secondes");
	return SommetMin.equals(this.graphe.getTabNodes()[this.destination]);
	
	}

    public void run() {

		System.out.println("Run PCC de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;
		
		
		for(int i = 0; i < this.tabLabel.length ; i++){
			this.tabLabel[i] = new Label(i); // initialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
		}
		
		// A vous d'implementer la recherche de plus court chemin.
		
				
		
		
		this.graphe.getDessin().setColor(Color.green);
		if(Dijkstra()){ // dijkstra de pcc
			int numSommet=this.destination;

			ArrayList<Node> tempN=new ArrayList<Node>();
			while(numSommet!=this.origine){
				tempN.add(this.graphe.getTabNodes()[numSommet]);
				numSommet=this.tabLabel[numSommet].getPere();
			}
			tempN.add(this.graphe.getTabNodes()[this.origine]);
			this.coutChemin = this.graphe.calculCoutChemin();
			this.reverseCopy(tempN);
			System.out.println("Cout du plus court chemin : "+this.coutChemin);
			System.out.println("Nombre de sommets explores : "+this.Tas.getNbSommetsExplores());
			System.out.println("Nombre de sommets marques : "+Label.getNbSommetsMarques());
			Label.resetSommetsMarques();
			System.out.println("Nombre maximum de sommets dans le tas : "+this.Tas.getNbMaxElementsTas());
		}
		else{
			System.out.println("Ce chemin est inexistant ...");
		}
    
    }
	
}
