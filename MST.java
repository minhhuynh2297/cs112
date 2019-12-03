package apps;

import structures.*;
import java.util.Iterator;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) { //Done
	
		/* COMPLETE THIS METHOD */
		PartialTreeList L = new PartialTreeList(); 
		PartialTree T;
		PartialTree.Arc arc;
		Vertex current;
		Vertex.Neighbor nbr;
		Vertex[] vertices=graph.vertices;
		
		for(int i=0; i<vertices.length; i++){
			T=new PartialTree(vertices[i]); //Creates a new tree	
			MinHeap<PartialTree.Arc> temp=new MinHeap<PartialTree.Arc>(); 
			current=vertices[i];
			nbr=current.neighbors;  
			while(nbr!=null){
				arc=new PartialTree.Arc(current, nbr.vertex, nbr.weight);
				temp.insert(arc);
				nbr=nbr.next;
			}
			MinHeap<PartialTree.Arc> PQ=new MinHeap<PartialTree.Arc>(); 
			while(temp.size()!=0){
				PQ.insert(temp.deleteMin());
			}
			T.getArcs().merge(PQ);
			L.append(T);
		}
		return L;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		
		/* COMPLETE THIS METHOD */
		ArrayList<PartialTree.Arc> finish = new ArrayList<PartialTree.Arc>();  
		ArrayList<PartialTree> dump = new ArrayList<PartialTree>(); 
		while (ptlist.size()>1) {
			System.out.println();
			System.out.println("START");
			
			 Iterator<PartialTree> iter = ptlist.iterator();
			 while (iter.hasNext()) {
			      PartialTree pt = iter.next();
			      System.out.println(pt.toString());
			  }
			PartialTree PTX = ptlist.remove();
			MinHeap<PartialTree.Arc> PQX=PTX.getArcs();
			PartialTree.Arc highestPriorityArc = PQX.deleteMin(); 	
			Vertex start = highestPriorityArc.v1;
			System.out.println("START: "+ start);
			Vertex end = highestPriorityArc.v2;
			System.out.println("END: "+ end);
			Iterator<PartialTree.Arc> itrA=PQX.iterator(); 
			while(itrA.hasNext()){
				PartialTree.Arc temp=itrA.next();
				if(temp.v1.equals(end)){
					highestPriorityArc = PQX.deleteMin();
					start=highestPriorityArc.v1;
					end=highestPriorityArc.v2;
					itrA=PQX.iterator();
				}
			}
			PartialTree PTY = ptlist.removeTreeContaining(end);
			finish.add(highestPriorityArc); //add arc to be part of MST
			MinHeap<PartialTree.Arc> temp=new MinHeap<PartialTree.Arc>(); 
			PTX.merge(PTY);

			while(PTX.getArcs().size()!=0){
				temp.insert(PTX.getArcs().deleteMin()); 
			}
			while(temp.size()!=0){
				PTX.getArcs().insert(temp.deleteMin());
			}
			System.out.println("To be inserted: " + PTX.toString());
			/*for(int i=0; i<ptlist.size(); i++){
				ptlist.append(ptlist.remove());
			}*/
		/*	dump.add(PTX);
			for(int i=0; i<dump.size(); i++){
				System.out.println("DUMP :"+dump.get(i).toString());
			}*/
			for(int i=0; i<ptlist.size(); i++){
				ptlist.append(ptlist.remove());
			}
			ptlist.append(PTX);
		}
		for(int i=0; i<finish.size(); i++){
			System.out.println(finish.get(i));
		}
		return finish;
	}
}
