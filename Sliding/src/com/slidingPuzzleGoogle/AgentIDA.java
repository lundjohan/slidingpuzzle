package com.slidingPuzzleGoogle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgentIDA extends Agent{
	private	Set<String>exploredSet = new HashSet<String>();
	private	int limit;
	private	int nextLimit;
	private	Tablet IDA(Tablet t){
		super.nodesChecked = -1;
		System.out.println("Timer startas");
		long startTimer = System.nanoTime();

		Node solution = null;
		Node root = new Node(new Tablet(t));
		limit = root.heuristics;	
		exploredSet.add(root.hashNr);	
		do{
			nextLimit = Integer.MAX_VALUE;	
			solution = DFS(root);	
			limit = nextLimit;	
		}while (solution==null);

		//kopiera den vinnande Tablet:
		Tablet copyWinner = new Tablet (solution.getTablet());	

		//Stanna tid och skriv ut den.
		long elapsedTimeMillis = (System.nanoTime() - startTimer)/1000000;
		super.time = elapsedTimeMillis;
		System.out.println("Elapsed time [ms]: "+ elapsedTimeMillis);
		
		//frigör utrymme (behövs eftersom det inte ska ligga kvar till nästa solve)
		deleteTree(root);
		clear();
		return copyWinner;	


	}
	private	Node DFS(Node n){	//Depth - first - search
		if (n.heuristics > limit){								
			if (n.heuristics < nextLimit)
				nextLimit = n.heuristics;
			return null;
		}
		if (n.getTablet().getPoints() == 0){	
			return n;
		}
		n.expand();
		if (n.up != null){
			if(exploredSet.contains(n.up.hashNr)){	
				n.up=null;
			}
			else{
				exploredSet.add(n.up.hashNr);
				Node solution = DFS(n.up);
				if (solution!=null) 
					return solution;
				exploredSet.remove(n.up.hashNr);
				n.up = null;
			}
		}
		if (n.down != null){		
			if (exploredSet.contains(n.down.hashNr)){
				n.down=null;
			}
			else{
				exploredSet.add(n.down.hashNr);
				Node solution = DFS(n.down);						
				if (solution!=null)								
					return solution;
				exploredSet.remove(n.down.hashNr);
				n.down = null;
			}
		}
		if (n.left != null){	
			if (exploredSet.contains(n.left.hashNr)){
				n.left=null;
			}
			else{
				exploredSet.add(n.left.hashNr);
				Node solution = DFS(n.left);
				if (solution!=null) 
					return solution;	
				exploredSet.remove(n.left.hashNr);
				n.left = null;
			}
		}
		if (n.right != null){
			if (exploredSet.contains(n.right.hashNr)){
				n.right=null;
			}
			else{
				exploredSet.add(n.right.hashNr);
				Node solution = DFS(n.right);
				if (solution!=null) 
					return solution;
				exploredSet.remove(n.right.hashNr); 
				n.right = null;
			}
		}
		return null;
	}
	//deleteTree deletar alla noder i trädet inklusive roten.
	public void deleteTree(Node root){
		if(root.up != null)
			deleteTree(root.up);
		if(root.down != null)
			deleteTree(root.down);
		if(root.right != null)
			deleteTree(root.right);
		if(root.left != null)
			deleteTree(root.left);
		root= null;
	}	


	public	List<Character> solve(Tablet t){
		t.clearOldMoves();	
		return IDA(t).forwardBackMoves();
		
	}
	public	String getName(){return "IDA*";}
	public	void clear(){exploredSet.clear();}	
}
