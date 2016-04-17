package core;

public class Pieton extends Voyageur{
	
	static int vitessePieton = 4; //km/h
	
	static int vitesseRoutesInterdites = 110 ; // km/h
	
	public Pieton(Node noeudDepart, Node noeudArrivee){
		super(noeudDepart,noeudArrivee);
	}
}
