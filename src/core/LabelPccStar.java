package core;
import java.lang.Comparable;

public class LabelPccStar extends Label{
	
	private double coutEstimeDest; // cout estime du noeud a la destination (vol d'oiseau,...)
	
	public LabelPccStar(int sommetCourant, double coutEstimeDest){
		super(sommetCourant);
		this.coutEstimeDest=coutEstimeDest;
	}
	
	@Override
	public double getCoutEstime(){
		return this.coutEstimeDest;
	}
	
	

}
