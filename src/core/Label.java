package core ;

/**
 *   Classe representant un graphe.
 *   A vous de completer selon vos choix de conception.
 */

import java.lang.*;

public class Label implements Comparable<Label>{
	private boolean marquage ; // Vrai si sommet entièrement fixé par l'algo

	protected double cout; // temps

	private int pere; // num sommet pere du plus court chemin courant 

	private int sommetCourant; // num sommet concerné par ce label
	
	private static int nbSommetsMarques=0;

	public Label(int sommetCourant){
		this.sommetCourant=sommetCourant;
		this.marquage = false;
		this.pere=0;
		this.cout = Double.POSITIVE_INFINITY;
	}
	
	public boolean getMarq(){
		return this.marquage;
	}
	
	public static int getNbSommetsMarques(){
		return nbSommetsMarques;
	}

	public double getCout(){
		return this.cout;
	}
	
	public int getPere(){
		return this.pere;
	}

	public int getSommetCourant(){ // si inutile supprimer !
		return this.sommetCourant;
	}

	public void setCout(double cout){
		this.cout = cout;
	}

	public void setMarq(){
		nbSommetsMarques++;
		this.marquage = true;
	}
	
	public void setPere(int pere){
		this.pere = pere;
	}

	public static void resetSommetsMarques(){
		nbSommetsMarques = 0;
	}
	
	public double getCoutEstime(){
		return 0.0;
	}

	@Override
	public int compareTo(Label L){
		if(getCout() + getCoutEstime() -( L.getCout()+L.getCoutEstime() ) < 0){
			return -1;
		}else{
			if(getCout() + getCoutEstime() -( L.getCout()+L.getCoutEstime()) > 0){
				return 1;
			}else{
				if(getCoutEstime() < L.getCoutEstime()){
					return -1;
				}else{
					return 1;			
				}
			}
		}
	}
}
