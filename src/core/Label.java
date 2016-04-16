package core ;

/**
 *   Classe representant un graphe.
 *   A vous de completer selon vos choix de conception.
 */


public class Label implements Comparable<Label>{
	private boolean marquage ; // Vrai si sommet entièrement fixé par l'algo

	protected double cout; // temps

	private int pere; // num sommet pere du plus court chemin courant 

	private int sommetCourant; // num sommet concerné par ce label

	public Label(int sommetCourant){
		this.sommetCourant=sommetCourant;
		this.marquage = false;
		this.pere=0;
		this.cout = Double.POSITIVE_INFINITY;
	}
	
	public boolean getMarq(){
		return this.marquage;
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
		this.marquage = true;
	}
	
	public void setPere(int pere){
		this.pere = pere;
	}

	@Override
	public int compareTo(Label L){
		if(L instanceof LabelPccStar ){			
			LabelPccStar  x = (LabelPccStar)this;
			if(x.getCout() + x.getCoutEstime() -( ((LabelPccStar)(L)).getCout()+((LabelPccStar)(L)).getCoutEstime() ) < 0){
				return -1;
			}else{
				if(x.getCout() + x.getCoutEstime() -( ((LabelPccStar)(L)).getCout()+((LabelPccStar)(L)).getCoutEstime()) > 0){
					return 1;
				}else{
					if(x.getCoutEstime() < ((LabelPccStar)(L)).getCoutEstime()){
						return -1;
					}else{
						return 1;			
					}
				}
			}
		}else{
			if (this.cout - L.getCout() < 0){
				return -1;
			}else{
				if(this.cout - L.getCout() == 0){
					return 0;
				}else{
					return 1;
				}
			}	
		}
	}
}
