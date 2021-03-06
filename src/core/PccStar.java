package core ;

import java.awt.Color;
import java.io.* ;
import java.util.ArrayList;

import base.Readarg ;

public class PccStar extends Pcc {

    public PccStar(Graphe gr, PrintStream sortie, Readarg readarg) {
    	super(gr, sortie, readarg) ;
    }
    
    public PccStar(Graphe gr, PrintStream sortie, Readarg readarg, int numSommetDepart, int numSommetArrivee){
    	super(gr,sortie,readarg,numSommetDepart,numSommetArrivee);
    }

    public void run() {

	System.out.println("Run PCC-Star de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;

	this.graphe.getDessin().setColor(Color.black);
	this.graphe.getDessin().drawPoint(this.graphe.getTabNodes()[this.origine].getLong(), this.graphe.getTabNodes()[this.origine].getLat(), 4);
	this.graphe.getDessin().drawPoint(this.graphe.getTabNodes()[this.destination].getLong(), this.graphe.getTabNodes()[this.destination].getLat(), 4);
	this.graphe.getDessin().putText(this.graphe.getTabNodes()[this.origine].getLong(), this.graphe.getTabNodes()[this.origine].getLat(), "Noeud de d�part");
	this.graphe.getDessin().putText(this.graphe.getTabNodes()[this.destination].getLong(), this.graphe.getTabNodes()[this.destination].getLat(), "Noeud d'arriv�e");
		System.out.println("Destination : "+destination);
		for(int i = 0; i < this.tabLabel.length ; i++){
			// initialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
			double distanceI = Graphe.distance((double)this.graphe.getTabNodes()[i].getLong(), (double)this.graphe.getTabNodes()[i].getLat(),(double)this.graphe.getTabNodes()[destination].getLong() , (double)this.graphe.getTabNodes()[destination].getLat());
			this.tabLabel[i] = new LabelPccStar(i,distanceI/(100d*130d/6d));//vitesse 130km/h dist/(100d*130d/6d)
		}
		
		if(Dijkstra()){ 
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
			
			if(this.dessiner){
				this.graphe.getDessin().setColor(Color.green);
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
