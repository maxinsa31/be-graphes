package core;

public class Segment{


	private float deltaLong;
		
	private float deltaLat;

	private boolean reverse;

	public Segment(float deltaLong, float deltaLat, boolean reverse){
		this.deltaLong = deltaLong;
		this.deltaLat = deltaLat;
		this.reverse=reverse;
	}

	public float getDeltaLong(){
		return this.deltaLong;
	}	

	public float getDeltaLat(){
		return this.deltaLat;
	}

	public boolean getReverse(){
		return this.reverse;
	}




}
