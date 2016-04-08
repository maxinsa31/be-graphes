package core;

public class Pieton extends Voyageur{
	
	private int vitessePieton;
	
	private int vitesseRoutesInterdites;
	
	public Pieton(Node noeudDepart, Node noeudArrivee){
		super(noeudDepart,noeudArrivee);
		this.vitessePieton = 4; // 4 km/h
		this.vitesseRoutesInterdites = 110; // 110 km/h
	}
	
	public int getVitessePieton(){
		return this.vitessePieton;
	}
	
	public int vitesseRoutesInterdites(){
		return this.vitesseRoutesInterdites;
	}

}
