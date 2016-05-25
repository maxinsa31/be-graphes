package core;

import java.awt.Color;
import java.util.ArrayList;

import base.Dessin;

public class Chemin {

	private ArrayList <Node> chemin;
	
	private double cout;
	
	public Chemin(){
		this.chemin = new ArrayList<Node>();
		this.cout = 0;
	}
	
	public double getCout(){
		return this.cout;
	}
	
	public ArrayList <Node> getChemin(){
		return this.chemin;
	}
	
	public void ajouterSommet(Node n){
		this.chemin.add(n);
	}
	
	public void reverseCopy(ArrayList<Node> c){
		for (Node N : c){
			chemin.add(0,N);
		}
	}
	
	public void DessinerChemin(Dessin dessin){

		float current_long = chemin.get(0).getLong();
		float current_lat  = chemin.get(0).getLat();

		int cpt =0;
		for (Node N : chemin){
			if(cpt< chemin.size() -1){
				dessin.drawPoint (N.getLong(), N.getLat(), 7);
				boolean trace = false;
				double min = 1000000d;
				for(Route R : N.getRoutesSuccesseurs()){
					if(R.getNodeSucc().getNumNode() == chemin.get(cpt+1).getNumNode() && min>R.getCoutRoute()){
						min = R.getCoutRoute();
					}
				}
				for(Route R : N.getRoutesSuccesseurs()){
					if (R.getNodeSucc().getNumNode() == chemin.get(cpt+1).getNumNode() && R.getCoutRoute() == min){
						trace = true;
						for (Segment S : R.getSegments()){
                            if(S.getReverse()){
                                current_long = chemin.get(cpt+1).getLong();
						        current_lat = chemin.get(cpt+1).getLat();
                            }
							dessin.drawLine(current_long,current_lat,current_long+S.getDeltaLong(),current_lat+S.getDeltaLat());
							current_long+=S.getDeltaLong();
							current_lat+=S.getDeltaLat();
						}
                        if(R.getSegments().size() != 0){
                            if(R.getSegments().get(0).getReverse()){
                                dessin.drawLine(current_long,current_lat,chemin.get(cpt).getLong(),chemin.get(cpt).getLat());
                            }
                            else{                        
						        dessin.drawLine(current_long,current_lat,chemin.get(cpt+1).getLong(),chemin.get(cpt+1).getLat());
                            }
                        }
                        else{
                            dessin.drawLine(current_long,current_lat,chemin.get(cpt+1).getLong(),chemin.get(cpt+1).getLat());
                        }
						current_long = chemin.get(cpt+1).getLong();
						current_lat = chemin.get(cpt+1).getLat();
					}
					
				}
				if(!trace){
					current_long = chemin.get(cpt+1).getLong();
			        current_lat = chemin.get(cpt+1).getLat();
			        for(Route R : N.getRoutesSuccesseurs()){
						if(R.getNodeSucc().getNumNode() == chemin.get(cpt+1).getNumNode() && min>R.getCoutRoute()){
							min = R.getCoutRoute();
						}
					}
					for (Route R2 : chemin.get(cpt+1).getRoutesSuccesseurs() ){
						if(R2.getNodeSucc().getNumNode() == N.getNumNode() && R2.getCoutRoute() == min ){
							for (Segment S : R2.getSegments()){
	                            if(S.getReverse()){
	                                current_long = chemin.get(cpt).getLong();
							        current_lat = chemin.get(cpt).getLat();
	                            }
								dessin.drawLine(current_long,current_lat,current_long+S.getDeltaLong(),current_lat+S.getDeltaLat());
								current_long+=S.getDeltaLong();
								current_lat+=S.getDeltaLat();
							}
	                        if(R2.getSegments().size() != 0){
	                            if(R2.getSegments().get(0).getReverse()){
	                                dessin.drawLine(current_long,current_lat,chemin.get(cpt+1).getLong(),chemin.get(cpt+1).getLat());
	                            }
	                            else{                        
							        dessin.drawLine(current_long,current_lat,chemin.get(cpt).getLong(),chemin.get(cpt).getLat());
	                            }
	                        }
	                        else{
	                            dessin.drawLine(current_long,current_lat,chemin.get(cpt).getLong(),chemin.get(cpt).getLat());
	                        }
							current_long = chemin.get(cpt+1).getLong();
							current_lat = chemin.get(cpt+1).getLat();
						}
					}
				}
				
			}		
			cpt++;
		}
		dessin.drawPoint (chemin.get(cpt-1).getLong(), chemin.get(cpt-1).getLat(), 7);
		chemin.clear();
	}
	
	public void DessinerChemin2(Dessin dessin){
		dessin.setColor(Color.blue) ;
		int cpt =0;
		for (Node N : chemin){
			if(cpt< chemin.size() -1){
				dessin.drawPoint (N.getLong(), N.getLat(), 7);
				dessin.drawLine(N.getLong(),N.getLat(),chemin.get(cpt+1).getLong(),chemin.get(cpt+1).getLat());
			}
			cpt++;
		}
		dessin.drawPoint (chemin.get(cpt-1).getLong(), chemin.get(cpt-1).getLat(), 7);
		chemin.clear();
	}


	//cout en temps
	public void calculCoutChemin(){
		float cout = 0.0f;
		float min = 10000000.0f;
		int cpt = 0;
		for (Node N : chemin){ // nb noeuds du chemin
			if (cpt < chemin.size()-1){
				for(Route R : N.getRoutesSuccesseurs()){
					if ( (((float)R.getLongueur())/(100.0f/6.0f*R.getDes().vitesseMax()))< min && R.getNodeSucc().getNumNode()==chemin.get(cpt+1).getNumNode()){
						min =(((float)R.getLongueur())/(100.0f/6.0f*R.getDes().vitesseMax())) ;			
					}
				}
				cout+=min; 
				min = 10000000.0f;	
			}
			cpt++;
		}
		this.cout = cout;
	}
	//distance
	public void calculCoutChemin2(){
		int cout = 0;
		int min = 1000000;
		int cpt = 0;
		for (Node N : chemin){
			if (cpt < chemin.size()-1){
				for(Route R : N.getRoutesSuccesseurs()){
					if ( (R.getLongueur()< min) && R.getNodeSucc().getNumNode()==chemin.get(cpt+1).getNumNode()){
						min =R.getLongueur();			
					}
				}
				cout+=min; 
				min = 1000000;	
			}
			cpt++;
		}
		this.cout = cout;
	}
	
}
