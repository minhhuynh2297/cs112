package structures;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = 
							getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
	

	private static Queue<String> toQueueStr(ArrayList<Interval> interval){ //Put ArrayList into Queue as Strings
		Queue<String> strings=new Queue<String>();
		for(int i=0; i<interval.size(); i++){
			strings.enqueue(interval.get(i).toString());
		}
		return strings; 
	}
	
	private static Queue<Interval> toQueueInt(Queue<String> queue){ //Turn Queue of Strings to <ArrayList> 

		Queue<Interval> interval=new Queue<Interval>();
		
		while(!queue.isEmpty()){
			
			String temp=queue.dequeue(); 
			int i=temp.length(); 
			int L=Integer.parseInt(temp.substring(1,temp.indexOf(',')));
			int R=Integer.parseInt(temp.substring(temp.indexOf(',')+1,temp.indexOf(']')));
			String desc=temp.substring(temp.indexOf(' '), i); 
			Interval x=new Interval(L, R, desc);
					interval.enqueue(x);
		}
		
		return interval; 
	}
	
	private static ArrayList<Integer> toSorted(Queue<String> queue){
		
		ArrayList<Integer> sorted=new ArrayList<Integer>(); 
		
		while(!queue.isEmpty()){
			
			int temp=Integer.parseInt(queue.dequeue());
			sorted.add(temp);
			
		}
		return sorted; 
	}
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	
	private static Queue<Integer> toQueue(ArrayList<Integer> list){
		Queue<Integer> endPoints=new Queue<Integer>(); 
		for(int i=0; i<list.size(); i++){
			endPoints.enqueue(list.get(i));
		}
		return endPoints; 
	}
	
	private static float findSplit(IntervalTreeNode node, char lr){
		if(lr=='l'){
			if(node.leftChild==null){
				return node.splitValue; 
			}
			return findSplit(node.leftChild, 'l'); 
		}
		else if(lr=='r'){
			if(node.rightChild==null){
				return node.splitValue;
			}
			return findSplit(node.rightChild, 'r'); 
		}
		return 0;
	}
	
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) { //Works
		
		Queue<String> list=toQueueStr(intervals);
		Queue<String> sorted=new Queue<String>(); //Where the sorted queue will be
		Queue<String> temp=new Queue<String>(); //Temporary reference
		Queue<String> holder=new Queue<String>(); //Temporary Storage
		Queue<Interval> finished=new Queue<Interval>(); 
		String position; 
		if(sorted.isEmpty()){
			sorted.enqueue(list.dequeue());
		}
		if(lr=='l'){
			
			while(!list.isEmpty()){
				position=list.peek();
				String a=position.substring(1,position.indexOf(','));
				String b=sorted.peek().substring(1, sorted.peek().indexOf(','));
				
				if(a.compareTo(b)<0 || a.compareTo(b)==0){
					holder.enqueue(position);
					while(!sorted.isEmpty()){
						holder.enqueue(sorted.dequeue());
					}
					temp=holder;
					holder=sorted;
					sorted=temp;
					list.dequeue();
				}
				
				else{ //Less than
					holder.enqueue(sorted.dequeue());
					if(sorted.isEmpty()){
						holder.enqueue(position);
						temp=holder;
						holder=sorted;
						sorted=temp;
						list.dequeue();
					}
					else{
					b=sorted.peek().substring(1, sorted.peek().indexOf(','));
					while(sorted.size()!=1 && (a.compareTo(b)>0 ||a.compareTo(b)==0)){
						holder.enqueue(sorted.dequeue());
						b=sorted.peek().substring(1, sorted.peek().indexOf(','));
					}
					
					if(sorted.size()==1 && a.compareTo(b)>0 ||a.compareTo(b)==0){
						holder.enqueue(sorted.dequeue());
					}
					holder.enqueue(position);
					while(!sorted.isEmpty()){
						holder.enqueue(sorted.dequeue());
					}
					temp=holder;
					holder=sorted;
					sorted=temp;
					list.dequeue();
				}
			}
		}
			intervals.clear();
			finished=toQueueInt(sorted); 
			while(!finished.isEmpty()){
				intervals.add(finished.dequeue());
			}
		}	
		
		else{ //R
			while(!list.isEmpty()){
				position=list.peek();
				String a=position.substring(position.indexOf(',')+1,position.indexOf(']'));
				String b=sorted.peek().substring(sorted.peek().indexOf(',')+1,sorted.peek().indexOf(']')); 
				if(a.compareTo(b)<0 || a.compareTo(b)==0){
					holder.enqueue(position);
					while(!sorted.isEmpty()){
						holder.enqueue(sorted.dequeue());
					}
					temp=holder;
					holder=sorted;
					sorted=temp;	
					list.dequeue();
				}
				
				else{ //Greater than
					holder.enqueue(sorted.dequeue());
					if(sorted.isEmpty()){
						holder.enqueue(position);
						temp=holder;
						holder=sorted;
						sorted=temp;
						list.dequeue();
					}
					else{
							b=sorted.peek().substring(sorted.peek().indexOf(',')+1,sorted.peek().indexOf(']')); 
							while(sorted.size()!=1 && (a.compareTo(b)>0 ||a.compareTo(b)==0)){
								holder.enqueue(sorted.dequeue());
								b=sorted.peek().substring(sorted.peek().indexOf(',')+1,sorted.peek().indexOf(']')); 
							}
							if(sorted.size()==1 && a.compareTo(b)>0 ||a.compareTo(b)==0){
								holder.enqueue(sorted.dequeue());
							}
						holder.enqueue(position);
						while(!sorted.isEmpty()){
							holder.enqueue(sorted.dequeue());
						}	
						temp=holder;
						holder=sorted;
						sorted=temp;
						list.dequeue();
					}	
				}
			}
			intervals.clear();
			finished=toQueueInt(sorted); 
			while(!finished.isEmpty()){
				intervals.add(finished.dequeue());
			}
			}
		}	
	
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		Queue<String> leftSorted=toQueueStr(leftSortedIntervals); 
		Queue<String> rightSorted=toQueueStr(rightSortedIntervals); 
		Queue<String> noDuplicate=new Queue<String>(); 
		Queue<String> holder=new Queue<String>(); 
		Queue<String> erase=new Queue<String>();
		ArrayList<Integer> sortedEndPoints=new ArrayList<Integer>(); 
		String current;
		String a;
		String b; 
		
		while(!leftSorted.isEmpty()){ //Go through leftSorted
			current=leftSorted.dequeue();    a=current.substring(1,current.indexOf(','));

			if(noDuplicate.isEmpty()){
				noDuplicate.enqueue(a); 
				current=leftSorted.dequeue();    
				a=current.substring(1,current.indexOf(','));
			}
			
			innerLoop:
				
				while(!noDuplicate.isEmpty()){
					b=noDuplicate.dequeue();
					
					if(a.compareTo(b)<0){
						holder.enqueue(a);
						holder.enqueue(b); 
						while(!noDuplicate.isEmpty()){
							holder.enqueue(noDuplicate.dequeue());
						}
						break innerLoop;
					}
					
					else if(a.compareTo(b)>0){
						holder.enqueue(b);
						if(noDuplicate.isEmpty()){
							holder.enqueue(a);
							break innerLoop; 
						}
						continue; 
					}
					
					else if(a.equals(b)){
						holder.enqueue(b);
						break innerLoop; 
					}
					}
				erase=holder;
				holder=noDuplicate;
				noDuplicate=erase; 
		}
		
		while(!rightSorted.isEmpty()){
			current=rightSorted.dequeue();    a=current.substring(current.indexOf(',')+1, current.indexOf(']'));
			innerLoop:
				
				while(!noDuplicate.isEmpty()){
					b=noDuplicate.dequeue();
					
					if(a.compareTo(b)<0){
						holder.enqueue(a);
						holder.enqueue(b); 
						while(!noDuplicate.isEmpty()){
							holder.enqueue(noDuplicate.dequeue());
						}
						break innerLoop;
					}
					
					else if(a.compareTo(b)>0){
						holder.enqueue(b);
						if(noDuplicate.isEmpty()){
							holder.enqueue(a);
							break innerLoop; 
						}
						continue; 
					}
					
					else if(a.equals(b)){
						holder.enqueue(b);
						while(!noDuplicate.isEmpty()){
							holder.enqueue(noDuplicate.dequeue());
						}
						break innerLoop; 
					}
					}
				erase=holder;
				holder=noDuplicate;
				noDuplicate=erase;
		}
		
		sortedEndPoints=toSorted(noDuplicate); 
	
		return sortedEndPoints; 
		
	}
	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		Queue<Integer> list=toQueue(endPoints); 
		Queue<IntervalTreeNode> Nodes=new Queue<IntervalTreeNode>();
		IntervalTreeNode x; 
		IntervalTreeNode root; 
		IntervalTreeNode T1;
		IntervalTreeNode T2; 
		float max, min; 
		int y; 
		
		while(!list.isEmpty()){ //Fills list with leaf nodes of endpoints
			y=list.dequeue(); 
			x=new IntervalTreeNode(y, y, y); 
			Nodes.enqueue(x);
		}
		
		if(Nodes.size()==1){
			root=Nodes.dequeue();
			return root; 
		}
		
		else{
		while(Nodes.size()!=1){
			int size=Nodes.size();
			while(size>1){
				T1=Nodes.dequeue();
				T2=Nodes.dequeue(); 
				max=T1.maxSplitValue;
				min=T2.minSplitValue; 
				x=new IntervalTreeNode((max+min)/2, 0, 0); 
				x.leftChild=T1;
				x.rightChild=T2; 
				x.minSplitValue=findSplit(x, 'l');
				x.maxSplitValue=findSplit(x, 'r');
				Nodes.enqueue(x);
				size=size-2; 
			}
			if(size==1){
				x=Nodes.dequeue();
				Nodes.enqueue(x);
			}
		}
		}
		return Nodes.dequeue();
	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		for(int i=0; i<leftSortedIntervals.size(); i++){
			IntervalTreeNode T=root; 
			int a=leftSortedIntervals.get(i).leftEndPoint; 
			int b=leftSortedIntervals.get(i).rightEndPoint;
		
		innerLoop:
		while(T!=null){
			if(leftSortedIntervals.get(i).contains(T.splitValue)){
				if(T.leftIntervals==null){
					T.leftIntervals=new ArrayList<Interval>(); 
				}
				T.leftIntervals.add(leftSortedIntervals.get(i));
				break innerLoop; 
			}
			else{
				if(a>=T.splitValue){
					T=T.rightChild;
				}
				else if(b<=T.splitValue){
					T=T.leftChild; 
				}
			}
		}
	}
		for(int i=0; i<rightSortedIntervals.size(); i++){
			IntervalTreeNode T=root; 
			int a=rightSortedIntervals.get(i).leftEndPoint; 
			int b=rightSortedIntervals.get(i).rightEndPoint;
		innerLoop:
			while(T!=null){
			if(rightSortedIntervals.get(i).contains(T.splitValue)){
				if(T.rightIntervals==null){
					T.rightIntervals=new ArrayList<Interval>(); 
				}
				T.rightIntervals.add(rightSortedIntervals.get(i));
				break innerLoop;
			}
			else{
				if(a>=T.splitValue){
					T=T.rightChild;
				}
				else if(b<=T.splitValue){
					T=T.leftChild; 
				}
			}
		}
	}
}
	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		ArrayList<Interval> Resultlist=new ArrayList<Interval>();
		IntervalTreeNode R=root;
		float SplitVal=R.splitValue;
		ArrayList<Interval> Llist=R.leftIntervals;
		ArrayList<Interval> Rlist=R.rightIntervals;
		IntervalTreeNode Lsub=R.leftChild;
		IntervalTreeNode Rsub=R.rightChild;
		IntervalTreeNode temp;
		
		if(Lsub==null && Rsub==null){
			return Resultlist; 
		}
		
		if(q.contains(SplitVal)){
			for(int i=0; i<Llist.size(); i++){
				Resultlist.add(Llist.get(i));
			}
	/*	while(Rsub!=null){
			int i=Llist.size();
			while(i>=0 && (Llist.get(i).contains(q.leftEndPoint)|| Llist.get(i).contains(q.rightEndPoint))){
					Resultlist.add(Llist.get(i));
				}
				temp=Rsub.rightChild;
				R=Rsub;
				Rsub=temp; 
				Llist=R.leftIntervals; 
			}
			while(Lsub!=null){
				int i=Llist.size();
				while(i>=0 && (Llist.get(i).contains(q.leftEndPoint)|| Llist.get(i).contains(q.rightEndPoint))){
					Resultlist.add(Llist.get(i));
				}
				temp=Lsub.leftChild;
				R=Lsub;
				Lsub=temp; 
				Llist=R.leftIntervals; 
			}
		}*/
		}
		else if(SplitVal<q.leftEndPoint){
		while(Rsub!=null){
			int i=Rlist.size();
			while(i>=0 && (Rlist.get(i).contains(q.leftEndPoint)|| Rlist.get(i).contains(q.rightEndPoint))){
				Resultlist.add(Rlist.get(i));
			}
			temp=Rsub.rightChild;
			R=Rsub;
			Rsub=temp; 
			Rlist=R.rightIntervals; 
		}
	}
		
		else if(SplitVal>q.rightEndPoint){
			while(Lsub!=null){
				int i=Llist.size();
				while(i>=0 && (Llist.get(i).contains(q.leftEndPoint)|| Llist.get(i).contains(q.rightEndPoint))){
					Resultlist.add(Llist.get(i));
				}
				temp=Lsub.leftChild;
				R=Lsub;
				Lsub=temp; 
				Llist=R.leftIntervals; 
			}
		}
		return Resultlist;
	}
}


