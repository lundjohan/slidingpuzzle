package com.slidingPuzzleGoogle;

import java.util.List;



public class Facade {
	public	Facade(){
		System.out.println("Inuti Facade 1");
		//************************************
		//Options
		//************************************						
		Tablet.lowerRightCorner = 0;				//true(1) för målnollan längst ner till höger, falskt(0) för uppe till vänster.
		Tablet.heuristics = new LinearConflict();	//Manhattan, LinearConflict, PatternDb
	}
	public List<Character> solve(List<Integer>nrs){
		System.out.println("Inuti Facade 2");
		Tablet.side = (int)Math.sqrt(nrs.size());							//3 = 8-puzzle, 4 = 15-puzzle
		Tablet tablet = new Tablet();
		tablet.customVec(nrs);
		Agent agent = new AgentIDA();
		System.out.println("Inuti Facade 3");
		return agent.solve(tablet);
	}
}
