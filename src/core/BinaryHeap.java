//
// ******************PUBLIC OPERATIONS*********************
// void insert( x )       --> Insert x
// Comparable deleteMin( )--> Return and remove smallest item
// Comparable findMin( )  --> Return smallest item
// boolean isEmpty( )     --> Return true if empty; else false
// ******************ERRORS********************************
// Throws RuntimeException for findMin and deleteMin when empty
package core;
import java.util.* ;

/**
 * Implements a binary heap.
 * Note that all "matching" is based on the compareTo method.
 * @author Mark Allen Weiss
 * @author DLB
 */


public class BinaryHeap<E extends Comparable<E>> {

    private int currentSize; // Number of elements in heap
    
    protected int nbMaxElementsTas;

    // Java genericity does not work with arrays.
    // We have to use an ArrayList
    private ArrayList<E> array; // The heap array

    private HashMap <E,Integer> hmap; 

	
	public int getNbMaxElementsTas(){
		return this.nbMaxElementsTas;
	}
	
	public boolean hmapContainsKey(E element){
		return this.hmap.containsKey(element);
	}

    /**
     * Construct the binary heap.
     */
    public BinaryHeap() {
        this.currentSize = 0;
        this.array = new ArrayList<E>() ;
		this.hmap = new HashMap<E,Integer>();
		this.nbMaxElementsTas=0;
    }

    // Constructor used for debug.
    private BinaryHeap(BinaryHeap<E> heap) {
    	this.currentSize = heap.currentSize ;
    	this.array = new ArrayList<E>(heap.array) ;
    	this.hmap = new HashMap<E,Integer>(heap.hmap);	
    	this.nbMaxElementsTas=0;	
        this.currentSize = 0;
    }

    // Sets an element in the array
    private void arraySet(int index, E value) { 
    	if (index == this.array.size()) {       
    		this.array.add(value) ;             
    		this.hmap.put(value,index);
    	}
    	else {
    		this.array.set(index, value) ;
    		this.hmap.put(value,index);
    	}
    }


    /**
     * Test if the heap is logically empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() { return this.currentSize == 0; }
    
    /**
     * Returns size.
     * @return current size.
     */
    public int size() { return this.currentSize; }
        
    
    /**
     * Returns index of parent.
     */
    private int index_parent(int index) {
	return (index - 1) / 2 ;
    }

    /**
     * Returns index of left child.
     */
    private int index_left(int index) {
	return index * 2 + 1 ;
    }

    /**
     * Insert into the heap.
     * @param x the item to insert.
     */
    public void insert(E x, int numSommet) {
		int index = this.currentSize++ ;
		if(this.nbMaxElementsTas<this.currentSize){
			this.nbMaxElementsTas=this.currentSize;
		}
		this.arraySet(index, x) ;
		this.percolateUp(index) ;
    }

    /**
     * Internal method to percolate up in the heap.
     * @param index the index at which the percolate begins.
     */
    private void percolateUp(int index) { //fait remonter la val[index] au plus haut tant qu'elle est plus petite que son pere
		E x = this.array.get(index) ;
        for( ; index > 0 && x.compareTo(this.array.get(index_parent(index)) ) < 0; index = index_parent(index) ) {
	    	E moving_val = this.array.get(index_parent(index)) ;
        	this.arraySet(index, moving_val) ;
		}
        this.arraySet(index, x) ;
    }

    /**
     * Internal method to percolate down in the heap.
     * @param index the index at which the percolate begins.
     */
    private void percolateDown(int index) { //fait descendre la valeur de l'index index au plus bas possible si val[index] est plus grand que ses fils
		int ileft = index_left(index) ;
		int iright = ileft + 1 ;

		if (ileft < this.currentSize) {
	   	 	E current = this.array.get(index) ;
	    	E left = this.array.get(ileft) ;
	    	boolean hasRight = iright < this.currentSize ;
	    	E right = (hasRight)?this.array.get(iright):null ;
	    
	   		if (!hasRight || left.compareTo(right) < 0) { //s'il n'y a pas de fils droit OU fils gauche < fils droit
			// Left is smaller than current
				if (left.compareTo(current) < 0) { 
		  		  this.arraySet(index, left) ;
		  		  this.arraySet(ileft, current) ;
		   		  this.percolateDown( ileft ) ;
				}
	    	}
	    else { // s'il a un fils droit ou que fils droit > fils gauche
		// Right is smaller than current
		if (right.compareTo(current) < 0) {
		    this.arraySet(index, right) ;
		    this.arraySet(iright, current) ;
		    this.percolateDown( iright ) ;
		}		
	    }
	}
    }



	public ArrayList<E> getArray(){ // ICI
		return this.array;
	}

    /**
     * Find the smallest item in the heap.
     * @return the smallest item.
     * @throws Exception if empty.
     */
    public E findMin( ) {
        if( isEmpty() )
            throw new RuntimeException( "Empty binary heap" );
        return this.array.get(0);
    }
    
    /**
     * Remove the smallest item from the heap.
     * @return the smallest item.
     * @throws Exception if empty.
     */
    public E deleteMin( ) {
        E minItem = findMin( );
        E lastItem = this.array.get(--this.currentSize) ;
        this.hmap.remove(minItem);	
        this.arraySet(0, lastItem) ;
        this.percolateDown( 0 );
        return minItem;
    }
    
    /**
     * Prints the heap
     */
    public void print() {
	System.out.println() ;
	System.out.println("========  HEAP  (size = " + this.currentSize + ")  ========") ;
	System.out.println() ;

	for (int i = 0 ; i < this.currentSize ; i++) {
	    System.out.println(this.array.get(i).toString()) ;
	}

	System.out.println() ;
	System.out.println("--------  End of heap  --------") ;
	System.out.println() ;
    }

    /**
     * Prints the elements of the heap according to their respective order.
     */
    public void printSorted() {

    	BinaryHeap<E> copy = new BinaryHeap<E>(this) ;

    	System.out.println() ;
    	System.out.println("========  Sorted HEAP  (size = " + this.currentSize + ")  ========") ;
    	System.out.println() ;

    	while (!copy.isEmpty()) {
    		System.out.println(copy.deleteMin()) ;
    	}

    	System.out.println() ;
    	System.out.println("--------  End of heap  --------") ;
    	System.out.println() ;
	}

	public void update(E elementSuiv){
        this.percolateUp(this.hmap.get(elementSuiv));
		this.percolateDown(this.hmap.get(elementSuiv));
	}

    
    // Test program : compare with the reference implementation PriorityQueue.
    public static void main(String [] args) {
        BinaryHeap<Integer> heap = new BinaryHeap<Integer>() ;
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>() ;

        int count = 0 ;
        int blocksize = 10000 ;

        System.out.println("Interrupt to stop the test.") ;
	
        while (true) {

        	// Insert up to blocksize elements
        	int nb_insert = (int)(Math.random() * (blocksize + 1)) ;
	    
        	for (int i = 0 ; i < nb_insert ; i++) {
        		Integer obj = new Integer(i) ;
        		heap.insert(obj,i) ;
        		queue.add(obj) ;
        	}

        	// Remove up to blocksize elements
        	int nb_remove = (int)(Math.random() * blocksize * 1.1) ;
	    
        	if (nb_remove > queue.size()) {
        		nb_remove = queue.size() ;
        	}

        	for (int i = 0 ; i < nb_remove ; i++) {

        		int removed1 = queue.poll().intValue() ;
        		int removed2 = heap.deleteMin().intValue() ;
		
        		if (removed1 != removed2) {
        			System.out.println("Ouch : expected " + removed1 + "  .. but got " + removed2) ;
        			System.exit(1) ;
        		}
        	}

        	if (heap.size() != queue.size()) {
        		System.out.println("Ouch : heap size = " + heap.size() + "  queue size = " + queue.size() ) ;
        		System.exit(1) ;
        	}

        	count += nb_remove ;
	    
        	if (count > 1000000) {
        		System.out.println("" + count + " items successfully compared. Heap size : " + heap.size()) ;
        		count = 0 ;
        	}
        }
    }
}
