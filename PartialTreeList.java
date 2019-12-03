package apps;

import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Vertex;


public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
  //  	System.out.println("Inside Append: "+tree.toString());
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    //	System.out.println("After Append: "+rear.tree.toString());
    //	System.out.println("After Append: "+rear.next.tree.toString());
    	size++;
    }

    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    	if(rear.equals(null)){
    		throw new NoSuchElementException();
    	}
    		/* COMPLETE THIS METHOD */
    	PartialTree finish;
    	if(size>2){
    		finish=rear.next.tree; 
    		rear.next=rear.next.next; 
    		System.out.println("REAR AT BEGINNING: "+rear.tree.getRoot());
    		size--; 
    		return finish; 
    	}
    	else if(size==2){
    		finish=rear.next.tree;
    		rear.next=rear;
    		size--;
    		System.out.println("REAR AT BEGINNING: "+rear.tree.getRoot());
    		return finish;
    	}
    	else{
    		finish=rear.tree;
    		rear=null;
    		size--;
    		return finish; 
    	}
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
    		/* COMPLETE THIS METHOD */
    	PartialTree finish;
    	if(rear==null){ //No list
    		throw new NoSuchElementException(); 
    	}
    	Iterator<PartialTree.Arc> itr; 
    	if(size==1){ //Rear is the only node
    		itr=rear.tree.getArcs().iterator();
    		while(itr.hasNext()){
    			if(itr.next().v1.equals(vertex)){
    				finish=rear.tree; 
    				rear=null;
    				size--;
    				return finish;
    			}
    		}
    		throw new NoSuchElementException(); 
    	}  
    	if(size==2){
    		Node curr=rear.next; 
    		itr=curr.tree.getArcs().iterator();
    		while(itr.hasNext()){ // curr is the vertex
    			PartialTree.Arc a=itr.next();
    			if(a.v1.equals(vertex)){
    				finish=curr.tree; 
    				rear.next=rear; 
    				System.out.println("Rear: "+rear.tree.getRoot());
    				size--;
    				return finish;
    			}
    		}
    		
    		curr=rear; 
    		itr=curr.tree.getArcs().iterator();
    		while(itr.hasNext()){ // curr is the vertex
    			PartialTree.Arc a=itr.next();
    			if(a.v1.equals(vertex)){
    				finish=curr.tree; 
    				rear=rear.next; 
    				System.out.println("Rear: "+rear.tree.getRoot());
    				size--;
    				return finish;
    			}
    		}
    		
    		throw new NoSuchElementException();
    	}
    	Node prev=rear;
    	Node curr=rear.next; 
    	System.out.println("CURR: "+curr.tree.getRoot());
    	Vertex test=curr.tree.getRoot(); 
    	itr=curr.tree.getArcs().iterator();
		while(itr.hasNext()){ // curr is the vertex
			PartialTree.Arc a=itr.next();
			if(a.v1.equals(vertex)){
				finish=curr.tree; 
				prev.next=curr.next;
				System.out.println("Rear: "+rear.tree.getRoot());
				size--;
				return finish;
			}
		}
		prev=curr;
		curr=curr.next;
    	System.out.println("CURR: "+curr.tree.getRoot());
    	test=curr.tree.getRoot(); 
		while(curr!=rear.next){
			itr=curr.tree.getArcs().iterator();
			while(itr.hasNext()){ // curr is the vertex
				PartialTree.Arc a=itr.next();
				if(a.v1.equals(vertex)){
					finish=curr.tree; 
					prev.next=curr.next;
					System.out.println("Rear2: "+rear.tree.getRoot());
					size--;
					return finish;
				}
			}
			prev=curr;
			curr=curr.next; 
	    	System.out.println("CURR: "+curr.tree.getRoot());
		   	test=curr.tree.getRoot(); 
		}
		if(curr==rear.next){
			throw new NoSuchElementException();
		}
		return null;
     }
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}


