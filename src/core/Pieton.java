package core;

public class Pieton extends Voyageur{
	
	static int vitessePieton = 4;
	
	private int vitesseRoutesInterdites;
	
	public Pieton(Node noeudDepart, Node noeudArrivee){
		super(noeudDepart,noeudArrivee);
		this.vitesseRoutesInterdites = 110; // 110 km/h
	}
	
	public int vitesseRoutesInterdites(){
		return this.vitesseRoutesInterdites;
	}

}
