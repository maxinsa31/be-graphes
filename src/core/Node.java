package core;
import java.util.*;
import base.*;

public class Node{
	
	private float longitude;
	private float latitude;
	private int numNode;
	private ArrayList<Route> RoutesSuccesseurs;
	
	

	public Node(float longitude, float latitude,int numNode){
		this.longitude=longitude;
		this.latitude=latitude;
		this.RoutesSuccesseurs = new ArrayList<Route>();
		this.numNode = numNode;
	}

	public void add_Routes(Route route){
		this.RoutesSuccesseurs.add(route);
	}

	public float getLong(){
		return this.longitude;
	}

	public float getLat(){
		return this.latitude;
	}

	public ArrayList<Route> getRoutesSuccesseurs(){
		return this.RoutesSuccesseurs;
	}

	public int getNumNode(){
		return this.numNode;
	}
}
