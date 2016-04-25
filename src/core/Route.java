package core;

import base.*;
import java.util.*;
public class Route{
	private Descripteur des;
	private Node noeudSucc;	
	private int longueur;
	private ArrayList<Segment> segments;

	public Route(Node noeudSucc, Descripteur des, int longueur){
		this.noeudSucc=noeudSucc;
		this.des=des;
		this.longueur=longueur;
		this.segments = new ArrayList<Segment>();
	}

	public Descripteur getDes(){
		return this.des;	
	}

	public Node getNodeSucc(){
		return this.noeudSucc;
	}

	public int getLongueur(){
		return this.longueur;
	}

	public double getCoutRoute(){
		return (double)longueur/(100.0d/6.0d*(double)des.vitesseMax());
	}		
	
	public double getCoutRoutePieton(){
		return (double)longueur/(100.0d/6.0d*(double)Pieton.vitessePieton);
	}

	public ArrayList<Segment> getSegments(){
		return this.segments;
	}

	public void copy(ArrayList<Segment> seg){
		for (Segment S : seg){
			this.segments.add(new Segment(S.getDeltaLong(),S.getDeltaLat(),false));
		}
	}

	public void reverseCopy(ArrayList<Segment> seg){
		int cpt = 0;
		for (Segment S : seg){
			if (cpt == 0){
				this.segments.add(new Segment(S.getDeltaLong(),S.getDeltaLat(),true));
			}
			else{
				this.segments.add(new Segment(S.getDeltaLong(),S.getDeltaLat(), false));
			}
			cpt++;
		}
	}


}
