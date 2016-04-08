package core ;

import java.io.* ;
import java.util.ArrayList;

import base.Readarg ;

public class PccStar extends Pcc {

    public PccStar(Graphe gr, PrintStream sortie, Readarg readarg) {
    	super(gr, sortie, readarg) ;
    }

    public void run() {

	System.out.println("Run PCC-Star de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;

	// A vous d'implementer la recherche de plus court chemin A*
		System.out.println("Destination : "+destination);
		for(int i = 0; i < this.tabLabel.length ; i++){
			// initialisation des labels ( sommets non marques, cout infini, pas de sommet pred pcc)
			double distanceI = Graphe.distance((double)this.graphe.getTabNodes()[i].getLong(), (double)this.graphe.getTabNodes()[i].getLat(),(double)this.graphe.getTabNodes()[destination].getLong() , (double)this.graphe.getTabNodes()[destination].getLat());
			this.tabLabel[i] = new LabelPccStar(i,distanceI/(100d*130d/6d));//vitesse 130km/h dist/(100d*130d/6d)
			//System.out.println("distance vol d'oiseau depuis "+i+" :"+distanceI); 
		}		
		//System.out.println("ICI : 139 - 25 : "+Graphe.distance((double)this.graphe.getTabNodes()[25].getLong(),(double)this.graphe.getTabNodes()[25].getLat(),(double)this.graphe.getTabNodes()[139].getLong(),(double)this.graphe.getTabNodes()[139].getLat()));			
			//this.tabLabel[i] = new LabelPccStar(i,Math.abs( (double)this.graphe.getTabNodes()[i].getLong() - (double)this.graphe.getTabNodes()[destination].getLong()) + Math.abs((double)this.graphe.getTabNodes()[destination].getLat() - (double)this.graphe.getTabNodes()[i].getLat()));
		
		
		if(Dijkstra()){ 
			int numSommet=this.destination;

			ArrayList<Node> tempN=new ArrayList<Node>();
			while(numSommet!=this.origine){
				tempN.add(this.graphe.getTabNodes()[numSommet]);
				numSommet=this.tabLabel[numSommet].getPere();
			}
			tempN.add(this.graphe.getTabNodes()[this.origine]);
			this.reverseCopy(tempN);
			System.out.println("Cout du plus court chemin : "+this.graphe.calculCoutChemin());
			System.out.println("Nombre de sommets explores : "+this.Tas.getNbSommetsExplores());
			System.out.println("Nombre de sommets marques : "+LabelPccStar.getNbSommetsMarques());
			Label.resetSommetsMarques();
			System.out.println("Nombre maximum de sommets dans le tas : "+this.Tas.getNbMaxElementsTas());
		}
		else{
			System.out.println("Ce chemin est inexistant ...");
		}
		
    }

}
