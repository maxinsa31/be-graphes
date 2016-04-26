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
    
    protected int nbSommetsExplores;
    
    protected int nbSommetsMarques;

	protected Label [] tabLabel;

	protected BinaryHeap<Label> Tas;
	
	protected Chemin chemin;

    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg) {
		super(gr, sortie, readarg) ;
		
		int saisie = readarg.lireInt("Voulez-vous saisir les sommets au clavier ou au clic ? ( 0 si clavier, 1 si clic ) ");

		this.zoneOrigine = gr.getZone () ;
		if(saisie == 0){
			this.origine = readarg.lireInt ("Numero du sommet d'origine ? ") ;
		}
		else{
			System.out.print("Numero du sommet d'origine ? ");
			this.origine = this.getGraphe().situerClick();
		}

		// Demander la zone et le sommet destination.
		this.zoneOrigine = gr.getZone () ;
		if(saisie == 0){
			this.destination = readarg.lireInt ("Numero du sommet destination ? ");
		}
		else{
			System.out.print("Numero du sommet destination ? ");
			this.destination = this.getGraphe().situerClick();
		}

		this.tabLabel = new Label[gr.getTabNodes().length];
	
		this.Tas = new BinaryHeap<Label>();
		
		this.chemin = new Chemin();
    }
    
    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg, int numSommetDepart) { //constructeur pour isochrone
		super(gr, sortie, readarg) ;

		this.zoneOrigine = gr.getZone () ;
		this.origine = numSommetDepart ;

		// Demander la zone et le sommet destination.
		this.zoneOrigine = gr.getZone () ;

		this.nbSommetsExplores=0;
		this.nbSommetsMarques=0;
		
		this.tabLabel = new Label[gr.getTabNodes().length];
	
		this.Tas = new BinaryHeap<Label>();
		
		this.chemin = new Chemin();
    }
    
    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg, int numSommetDepart,int numSommetArrivee) { //constructeur pour le covoiturage
		super(gr, sortie, readarg) ;

		this.zoneOrigine = gr.getZone () ;
		this.origine = numSommetDepart ;

		// Demander la zone et le sommet destination.
		this.zoneOrigine = gr.getZone () ;
		this.destination = numSommetArrivee;

		this.nbSommetsExplores=0;
		this.nbSommetsMarques=0;
		
		this.tabLabel = new Label[gr.getTabNodes().length];
	
		this.Tas = new BinaryHeap<Label>();
		
		this.chemin = new Chemin();
    }
    
    public Chemin getChemin(){
    	return this.chemin;
    }

	
	public boolean DijkstraInverse(){
		long startTime = System.currentTimeMillis();
		this.tabLabel[this.origine].setCout(0.0d); // cout de 0 pour le sommet origine
		this.Tas.insert(this.tabLabel[this.origine],this.origine); // insertion dans le tas du sommet origine
		int numSommetMin = this.origine;  // numero du sommet min du tas
		Node SommetMin=this.graphe.getTabNodesInverse()[this.origine]; // sommet min du tas
		while(!this.Tas.isEmpty()&& !(SommetMin.equals(this.graphe.getTabNodesInverse()[this.destination]))){ //tant qu'il existe des sommets non marques
			SommetMin = this.graphe.getTabNodesInverse()[this.Tas.findMin().getSommetCourant()]; // recuperation du sommet min du tas
			numSommetMin = SommetMin.getNumNode(); // et son numero
			Label labelSommetMin=this.tabLabel[numSommetMin]; // et son label
			labelSommetMin.setCout(this.Tas.deleteMin().getCout()); //mise a jour du cout du label du sommet min 
			labelSommetMin.setMarq(); // mise  a jour du marquage du sommet min : marque
			this.nbSommetsMarques++;
			for (Route r : this.graphe.getTabNodesInverse()[numSommetMin].getRoutesSuccesseurs()){ //pour tous les successeurs de sommet min
				Node sommetSuccesseur=r.getNodeSucc(); // on recupere le sommet successeur
				int numSommetSuccesseur = sommetSuccesseur.getNumNode(); //et son numero
				Label labelSommetSucc=this.tabLabel[numSommetSuccesseur]; // et son label
				if(!labelSommetSucc.getMarq()){ // si ce sommet n'est pas marque
					if(labelSommetSucc.getCout()>(labelSommetMin.getCout()+r.getCoutRoute())){
						labelSommetSucc.setCout(labelSommetMin.getCout()+r.getCoutRoute());
						labelSommetSucc.setPere(numSommetMin); // mise a jour du pere					
						if(this.Tas.hmapContainsKey(labelSommetSucc)){	
							this.Tas.update(labelSommetSucc);
						}
						else{
							this.Tas.insert(labelSommetSucc,numSommetSuccesseur);
							this.nbSommetsExplores++;
							this.graphe.getDessin().drawPoint(sommetSuccesseur.getLong(), sommetSuccesseur.getLat(), 2);
						}						
					}
				}
			}
		}
		long endTime = System.currentTimeMillis();
		float executionTime = (endTime - startTime) / 1000f;
		System.out.println("Temps d'execution : "+executionTime+" secondes");
		return SommetMin.equals(this.graphe.getTabNodesInverse()[this.destination]);
	
	}
	
	public boolean Dijkstra(){
		long startTime = System.currentTimeMillis();
		this.tabLabel[this.origine].setCout(0.0d); // cout de 0 pour le sommet origine
		this.Tas.insert(this.tabLabel[this.origine],this.origine); // insertion dans le tas du sommet origine
		int numSommetMin = this.origine;  // numero du sommet min du tas
		Node SommetMin=this.graphe.getTabNodes()[this.origine]; // sommet min du tas
		while(!this.Tas.isEmpty()&& !(SommetMin.equals(this.graphe.getTabNodes()[this.destination]))){ //tant qu'il existe des sommets non marques
			SommetMin = this.graphe.getTabNodes()[this.Tas.findMin().getSommetCourant()]; // recuperation du sommet min du tas
			numSommetMin = SommetMin.getNumNode(); // et son numero
			Label labelSommetMin=this.tabLabel[numSommetMin]; // et son label
			labelSommetMin.setCout(this.Tas.deleteMin().getCout()); //mise a jour du cout du label du sommet min 
			labelSommetMin.setMarq(); // mise  a jour du marquage du sommet min : marque
			this.nbSommetsMarques++;
			for (Route r : this.graphe.getTabNodes()[numSommetMin].getRoutesSuccesseurs()){ //pour tous les successeurs de sommet min
				Node sommetSuccesseur=r.getNodeSucc(); // on recupere le sommet successeur
				int numSommetSuccesseur = sommetSuccesseur.getNumNode(); //et son numero
				Label labelSommetSucc=this.tabLabel[numSommetSuccesseur]; // et son label
				if(!labelSommetSucc.getMarq()){ // si ce sommet n'est pas marque
					if(labelSommetSucc.getCout()>(labelSommetMin.getCout()+r.getCoutRoute())){
						labelSommetSucc.setCout(labelSommetMin.getCout()+r.getCoutRoute());
						labelSommetSucc.setPere(numSommetMin); // mise a jour du pere					
						if(this.Tas.hmapContainsKey(labelSommetSucc)){	
							this.Tas.update(labelSommetSucc);
						}
						else{
							this.Tas.insert(labelSommetSucc,numSommetSuccesseur);
							this.nbSommetsExplores++;
							this.graphe.getDessin().drawPoint(sommetSuccesseur.getLong(), sommetSuccesseur.getLat(), 2);
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
		int dessiner = this.readarg.lireInt("Voulez-vous dessiner le chemin ? ( 1 si oui, 0 sinon ) ");
		
		for(int i = 0; i < this.tabLabel.length ; i++){
			this.tabLabel[i] = new Label(i); // initialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
		}
				
		
		
		this.graphe.getDessin().setColor(Color.green);
		if(Dijkstra()){ // dijkstra de pcc
			int numSommet=this.destination;

			ArrayList<Node> tempN=new ArrayList<Node>();
			while(numSommet!=this.origine){
				tempN.add(this.graphe.getTabNodes()[numSommet]);
				numSommet=this.tabLabel[numSommet].getPere();
			}
			
			tempN.add(this.graphe.getTabNodes()[this.origine]);
			chemin.reverseCopy(tempN);
			chemin.calculCoutChemin();
			
			System.out.println("Cout du plus court chemin : "+(float)chemin.getCout());
			System.out.println("Nombre de sommets explores : "+this.nbSommetsExplores);
			System.out.println("Nombre de sommets marques : "+this.nbSommetsMarques);
			System.out.println("Nombre maximum de sommets dans le tas : "+this.Tas.getNbMaxElementsTas());
			if(dessiner == 1){
				if (this.graphe.getTabNodes().length <= 1000000){
					chemin.DessinerChemin(this.graphe.getDessin());
				}else{
					chemin.DessinerChemin2(this.graphe.getDessin());
				}
			}
		}
		else{
			System.out.println("Ce chemin est inexistant ...");
		}
    
    }
	
}
