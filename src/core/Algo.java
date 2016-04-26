package core ;

import java.io.* ;
import base.* ;

/**
 * Classe abstraite representant un algorithme (connexite, plus court chemin, etc.)
 */
public abstract class Algo {

    protected PrintStream sortie ;
    protected Graphe graphe ;
    protected Readarg readarg ;
    
    protected Algo(Graphe gr, PrintStream fichierSortie, Readarg readarg) {
    	this.graphe = gr ;
    	this.sortie = fichierSortie ;	
    	this.readarg = readarg ;
    }
    
    
	public abstract void run() ;

	public Graphe getGraphe(){
		return this.graphe;
	}

}
